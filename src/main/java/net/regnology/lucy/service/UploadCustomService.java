package net.regnology.lucy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.repository.LicenseRiskCustomRepository;
import net.regnology.lucy.repository.RequirementCustomRepository;
import net.regnology.lucy.repository.UploadCustomRepository;
import net.regnology.lucy.repository.UserCustomRepository;
import net.regnology.lucy.service.exceptions.LibraryException;
import net.regnology.lucy.service.exceptions.LicenseAlreadyExistException;
import net.regnology.lucy.service.exceptions.LicenseException;
import net.regnology.lucy.service.exceptions.UploadException;
import net.regnology.lucy.service.upload.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service implementation for managing {@link Upload}.
 */
@Service
@Transactional
public class UploadCustomService extends UploadService {

    private final Logger log = LoggerFactory.getLogger(UploadCustomService.class);

    private final UploadCustomRepository uploadRepository;

    private final LibraryCustomService libraryService;

    private final LicenseCustomService licenseService;

    private final UserCustomRepository userRepository;

    private final LicenseRiskCustomRepository licenseRiskRepository;

    private final RequirementCustomRepository requirementRepository;

    private final ObjectMapper objectMapper;

    public UploadCustomService(
        UploadCustomRepository uploadRepository,
        LibraryCustomService libraryService,
        LicenseCustomService licenseService,
        UserCustomRepository userRepository,
        LicenseRiskCustomRepository licenseRiskRepository,
        RequirementCustomRepository requirementRepository,
        ObjectMapper objectMapper
    ) {
        super(uploadRepository);
        this.uploadRepository = uploadRepository;
        this.libraryService = libraryService;
        this.licenseService = licenseService;
        this.userRepository = userRepository;
        this.licenseRiskRepository = licenseRiskRepository;
        this.requirementRepository = requirementRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Save a upload.
     *
     * @param upload the entity to save.
     * @return the persisted entity.
     */
    public Upload save(Upload upload) {
        log.debug("Request to save Upload : {}", upload);

        if (upload.getFileContentType().equals("application/vnd.ms-excel")) upload.setFileContentType("text/csv");

        return uploadRepository.save(upload);
    }

    /**
     * Handler to process uploaded file that contains libraries.
     * Can parse libraries from CSV and XML (CycloneDX BOM) files.
     * Saves and updates libraries to the {@link Library} or {@link Product} entity.
     *
     * @param upload the Upload entity to process.
     * @throws UploadException if an error occurs during processing of the uploaded file.
     */
    @SuppressWarnings("unchecked")
    @Async
    public void uploadHandler(Upload upload) throws UploadException {
        log.info("Start processing upload : {}", upload.getId());
        switch (upload.getEntityToUpload()) {
            case LIBRARY:
                AssetManager<Library> assetManager = new AssetManager<>();
                assetManager.addLoader(new BomLoader(), "text/xml");
                assetManager.addLoader(new LibraryCsvLoader(), "application/vnd.ms-excel");
                assetManager.addLoader(new LibraryCsvLoader(), "text/csv");
                assetManager.addLoader(new ArchiveLoader(libraryService), "application/zip");
                assetManager.addLoader(new ArchiveLoader(libraryService), "application/x-zip-compressed");
                assetManager.addLoader(
                    file -> {
                        try {
                            return objectMapper.readValue(file.getFile(), new TypeReference<>() {});
                        } catch (IOException e) {
                            log.error("Error while parsing JSON file : {}", e.getMessage());
                            throw new UploadException("JSON file can't be read");
                        }
                    },
                    "application/json"
                );
                Set<Library> libraries = assetManager.load(
                    new File("", upload.getFile(), upload.getFileContentType()),
                    upload.getFileContentType()
                );

                if (libraries != null) {
                    for (Library library : libraries) {
                        log.info("Processing library : {} - {} - {}", library.getGroupId(), library.getArtifactId(), library.getVersion());
                        try {
                            if (library.getLastReviewedBy() != null) {
                                Optional<User> optionalUser = userRepository.findOneByLogin(library.getLastReviewedBy().getLogin());
                                optionalUser.ifPresent(library::setLastReviewedBy);
                            }

                            // TODO: linkedLicenses field

                            /*Optional<String> licenses = library.getLicenses().stream().map(License::getFullName).findFirst();
                            if (licenses.isPresent()) {
                                library.setLicenses(licenseService.findShortIdentifier(licenses.get()));
                            }*/

                            Optional<String> licenseToPublish = library
                                .getLicenseToPublishes()
                                .stream()
                                .map(License::getFullName)
                                .findFirst();
                            if (licenseToPublish.isPresent()) {
                                library.setLicenseToPublishes(licenseService.findShortIdentifier(licenseToPublish.get()));
                            }

                            Optional<String> licenseOfFiles = library.getLicenseOfFiles().stream().map(License::getFullName).findFirst();
                            if (licenseOfFiles.isPresent()) {
                                library.setLicenseOfFiles(licenseService.findShortIdentifier(licenseOfFiles.get()));
                            }

                            library.setLicenseUrl("");
                            library.setSourceCodeUrl("");

                            library = libraryService.saveWithCheck(library);
                        } catch (LibraryException e) {
                            Library dbLibrary = e.getLibrary();
                            dbLibrary.updateEmptyFields(library);
                            libraryService.licenseAutocomplete(dbLibrary);
                            libraryService.removeGenericLicenseUrl(dbLibrary);
                            // library.setLicenseUrl("");
                            // library.setSourceCodeUrl("");
                            libraryService.urlAutocomplete(dbLibrary);
                            libraryService.copyrightAutocomplete(dbLibrary);
                            libraryService.save(dbLibrary);
                        }
                    }
                }
                break;
            case LICENSE:
                AssetManager<License> assetManagerLicense = new AssetManager<>();
                assetManagerLicense.addLoader(new LicenseCsvLoader(), "application/vnd.ms-excel");
                assetManagerLicense.addLoader(new LicenseCsvLoader(), "text/csv");
                assetManagerLicense.addLoader(
                    file -> {
                        try {
                            Set<License> licenses = objectMapper.readValue(file.getFile(), new TypeReference<>() {});
                            licenses.forEach(e -> e.setId(null));
                            licenses.forEach(e -> e.setLicenseConflicts(new TreeSet<>()));
                            licenses.forEach(e -> e.setLastReviewedBy(null));
                            return licenses;
                        } catch (IOException e) {
                            log.error("Error while parsing JSON file : {}", e.getMessage());
                            throw new UploadException("JSON file cannot be read");
                        }
                    },
                    "application/json"
                );

                Set<License> licenses = assetManagerLicense.load(
                    new File("", upload.getFile(), upload.getFileContentType()),
                    upload.getFileContentType()
                );

                for (License license : licenses) {
                    log.info("Processing license : {}", license.getShortIdentifier());

                    if (license.getLastReviewedBy() != null) {
                        Optional<User> optionalUser = userRepository.findOneByLogin(license.getLastReviewedBy().getLogin());
                        optionalUser.ifPresentOrElse(license::setLastReviewedBy, () -> license.setLastReviewedBy(null));
                        optionalUser.ifPresent(license::setLastReviewedBy);
                    }

                    if (license.getLicenseRisk() != null) {
                        Optional<LicenseRisk> risk = licenseRiskRepository.findOneByName(license.getLicenseRisk().getName());
                        risk.ifPresent(license::setLicenseRisk);
                    }

                    if (license.getRequirements().size() > 1) {
                        Set<Requirement> requirementEntities = license
                            .getRequirements()
                            .stream()
                            .map(e -> {
                                Optional<Requirement> optionalRequirement = requirementRepository.findOneByShortText(e.getShortText());
                                if (optionalRequirement.isPresent()) {
                                    return optionalRequirement.get();
                                } else {
                                    e.setId(null);
                                    return requirementRepository.save(e);
                                }
                            })
                            .collect(Collectors.toSet());

                        license.setRequirements(requirementEntities);
                    } else {
                        Optional<String> optionalRequirements = license
                            .getRequirements()
                            .stream()
                            .map(Requirement::getShortText)
                            .findFirst();
                        if (optionalRequirements.isPresent()) {
                            String splitRegex = ";";

                            String requirements = optionalRequirements.get().trim();
                            String[] shortTexts;
                            shortTexts = requirements.split(splitRegex);

                            if (shortTexts.length == 0) {
                                shortTexts = new String[] { requirements };
                            }

                            Set<Requirement> requirementEntities = Arrays
                                .stream(shortTexts)
                                .map(String::trim)
                                .map(requirementRepository::findOneByShortText)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toSet());

                            license.setRequirements(requirementEntities);
                        }
                    }

                    try {
                        License savedLicense = licenseService.saveWithCheck(license);
                        licenseService.createLicenseConflictsForNewLicense(savedLicense);
                        licenseService.createLicenseConflictsForExistingLicenses(savedLicense);
                    } catch (LicenseAlreadyExistException e) {
                        License dbLicense = e.getLicense();
                        dbLicense.updateEmptyFields(license);
                        dbLicense.setRequirements(license.getRequirements());
                    } catch (LicenseException e) {
                        throw new UploadException(e.getMessage());
                    }
                }

                break;
        }
        log.info("Finished processing upload : {}", upload.getId());
    }
}
