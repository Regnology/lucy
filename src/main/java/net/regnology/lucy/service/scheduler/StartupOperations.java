package net.regnology.lucy.service.scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.domain.enumeration.CompatibilityState;
import net.regnology.lucy.repository.*;
import net.regnology.lucy.service.LibraryCustomService;
import net.regnology.lucy.service.LibraryQueryCustomService;
import net.regnology.lucy.service.LicenseCustomService;
import net.regnology.lucy.service.criteria.LibraryCustomCriteria;
import net.regnology.lucy.service.exceptions.LibraryException;
import net.regnology.lucy.service.helper.urlparsing.LicenseURLparser;
import net.regnology.lucy.service.helper.urlparsing.SourceURLparser;
import net.regnology.lucy.service.helper.urlparsing.URLparserHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tech.jhipster.service.filter.StringFilter;

@Component
public class StartupOperations {

    private final Logger log = LoggerFactory.getLogger(StartupOperations.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LibraryCustomService libraryService;

    @Autowired
    private LibraryCustomRepository libraryRepository;

    @Autowired
    private LicenseCustomRepository licenseRepository;

    @Autowired
    private LicenseCustomService licenseService;

    @Autowired
    private LibraryQueryCustomService libraryQueryService;

    @Autowired
    private LicenseConflictCustomRepository licenseConflictRepository;

    @Autowired
    private LicenseNamingMappingCustomRepository licenseNamingMappingCustomRepository;

    @Autowired
    private GenericLicenseUrlCustomRepository genericLicenseUrlCustomRepository;

    // @Scheduled(fixedDelay = 10000000, initialDelay = 1000)
    @Async
    public void allUnidentifiedLicenses() {
        /*StringFilter stringFilter = new StringFilter();
        stringFilter.setEquals("Unknown");
        LibraryCriteria libraryCriteria = new LibraryCriteria();
        libraryCriteria.setLicenseShortIdentifier(stringFilter);
        Specification<Library> specification = libraryQueryService.createSpecification(libraryCriteria);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Library> query = builder.createQuery(Library.class);

        Root<Library> root = query.from(Library.class);
        Predicate predicate = specification.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        root.fetch("licenses", JoinType.LEFT);
        query.select(root);*/

        //List<Library> libraries = libraryService.findAllWhereLicenseEmpty();
        //libraries.addAll(libraryService.findAllWhereLicenseEmpty());

        /*for (Library library : libraries) {
            libraryService.licenseAutocomplete(library);
            libraryService.removeGenericLicenseUrl(library);
            libraryService.saveForced(library);
        }*/
    }

    @Async
    public void reevaluateUnknownLicenses() {
        LibraryCustomCriteria libraryCriteria = new LibraryCustomCriteria();
        StringFilter filter = new StringFilter();
        filter.setContains(licenseService.getUnknownLicense().getShortIdentifier());
        libraryCriteria.setLinkedLicenseShortIdentifier(filter);
        List<Library> libraries = libraryQueryService.findByCriteria(libraryCriteria);

        for (Library library : libraries) {
            libraryService.licenseAutocomplete(library);
        }
    }

    // @Scheduled(fixedDelay = 10000000, initialDelay = 1000)
    @Async
    public void tmpDownloadLicenseTexts() {
        log.info("Start cleansing and download...");
        List<Library> libraries = libraryRepository.findAll();

        for (Library library : libraries) {
            if (library.getLicenseUrl() != null && library.getLicenseUrl().equals(Constants.NO_URL)) {
                log.info("Removing license URL : {}", library.getLicenseUrl());
                library.setLicenseUrl(null);
            }

            if (library.getSourceCodeUrl() != null && library.getSourceCodeUrl().equals(Constants.NO_URL)) {
                log.info("Removing source code URL : {}", library.getLicenseUrl());
                library.setSourceCodeUrl(null);
            }

            libraryService.licenseTextAutocomplete(library);
            libraryService.save(library);
        }
        log.info("Finished cleansing and download");
    }

    // @Scheduled(fixedDelay = 10000000, initialDelay = 1000)
    @Async
    public void checkUrl() {
        log.info("Start checking URLs...");
        List<Library> libraries = libraryRepository.findAll();

        LicenseURLparser licenseURLparser = new LicenseURLparser();
        licenseURLparser.initCommands();

        SourceURLparser sourceURLparser = new SourceURLparser();
        sourceURLparser.initCommands();

        URLparserHelper urLparserHelper = new URLparserHelper();

        for (Library library : libraries) {
            String originalLicenseUrl = library.getLicenseUrl();
            String originalSourceCodeUrl = library.getSourceCodeUrl();

            if (
                library.getLicenseUrl() != null &&
                library.getLicenseUrl().contains("github.com") &&
                (!library.getLicenseUrl().contains("/blob/") || !library.getLicenseUrl().contains("/tree/"))
            ) {
                try {
                    String url = urLparserHelper.updateGitHubURL(
                        library.getLicenseUrl(),
                        library.getVersion(),
                        URLparserHelper.paserType.LICENSE
                    );
                    if (!url.equals(Constants.NO_URL)) library.setLicenseUrl(url);
                } catch (IOException | InterruptedException | ParseException e) {
                    log.info(
                        "License URL not available. Library = GroupId : {} - ArtifactId : {} - Version : {}",
                        library.getGroupId(),
                        library.getArtifactId(),
                        library.getVersion()
                    );
                }
            } else if (
                StringUtils.isBlank(library.getLicenseUrl()) &&
                library.getSourceCodeUrl() != null &&
                library.getSourceCodeUrl().contains("github.com") &&
                (!library.getSourceCodeUrl().contains("/blob/") || !library.getSourceCodeUrl().contains("/tree/"))
            ) {
                try {
                    String url = urLparserHelper.updateGitHubURL(
                        library.getSourceCodeUrl(),
                        library.getVersion(),
                        URLparserHelper.paserType.LICENSE
                    );
                    if (!url.equals(Constants.NO_URL)) library.setLicenseUrl(url);
                } catch (IOException | InterruptedException | ParseException e) {
                    log.info(
                        "License URL not available. Library = GroupId : {} - ArtifactId : {} - Version : {}",
                        library.getGroupId(),
                        library.getArtifactId(),
                        library.getVersion()
                    );
                }
            }

            libraryService.licenseUrlBuilder(library);

            if (!StringUtils.isBlank(originalLicenseUrl) && library.getLicenseUrl().equals(Constants.NO_URL)) {
                library.setLicenseUrl(originalLicenseUrl);
            }

            libraryService.save(library);
        }
        log.info("Finished checking URLs");
    }

    // @Scheduled(fixedDelay = 10000000, initialDelay = 1000)
    @Async
    public void checkSourceUrl() {
        log.info("Start checking Source Code URLs...");
        List<Library> libraries = libraryRepository.findAll();

        LicenseURLparser licenseURLparser = new LicenseURLparser();
        licenseURLparser.initCommands();

        SourceURLparser sourceURLparser = new SourceURLparser();
        sourceURLparser.initCommands();

        URLparserHelper urLparserHelper = new URLparserHelper();

        int counter = 0;
        for (Library library : libraries) {
            String originalSourceCodeUrl = library.getSourceCodeUrl();

            if (
                library.getSourceCodeUrl() != null &&
                library.getSourceCodeUrl().matches(".*(\\.jar$|\\.zip$|\\.tar\\.gz$|\\.tgz$|\\.jar/download$)")
            ) {
                continue;
            }

            if (library.getSourceCodeUrl() != null && library.getSourceCodeUrl().matches(".*(bitbucket|gitlab).*")) log.info(
                "Gitlab or Bitbucket URL : {} - {} - {} : {}",
                library.getGroupId(),
                library.getArtifactId(),
                library.getVersion(),
                library.getSourceCodeUrl()
            );

            if (
                library.getSourceCodeUrl() != null &&
                library.getSourceCodeUrl().contains("github.com") &&
                (
                    !library.getSourceCodeUrl().contains("/archive/refs/tags/") ||
                    !library.getSourceCodeUrl().contains("/blob/") ||
                    !library.getSourceCodeUrl().contains("/tree/")
                )
            ) {
                try {
                    String url = urLparserHelper.updateGitHubURL(
                        library.getSourceCodeUrl(),
                        library.getVersion(),
                        URLparserHelper.paserType.SOURCE
                    );
                    if (!url.equals(Constants.NO_URL)) {
                        library.setSourceCodeUrl(url);
                    } else {
                        library.setSourceCodeUrl(null);
                    }
                } catch (IOException | InterruptedException | ParseException e) {
                    log.info(
                        "Source Code URL not available. Library = GroupId : {} - ArtifactId : {} - Version : {}",
                        library.getGroupId(),
                        library.getArtifactId(),
                        library.getVersion()
                    );
                }
            } else if (
                library.getSourceCodeUrl() != null &&
                library.getSourceCodeUrl().contains("github.com") &&
                library.getSourceCodeUrl().contains("/tree/")
            ) {
                String githubZip = library.getSourceCodeUrl().replace("/tree/", "/archive/refs/tags/") + ".zip";
                library.setSourceCodeUrl(githubZip);
            } else {
                if (
                    library.getSourceCodeUrl() != null &&
                    !library.getSourceCodeUrl().matches(".*(\\.jar$|\\.zip$|\\.tar\\.gz$|\\.tgz$|\\.jar/download$)")
                ) {
                    library.setSourceCodeUrl(null);
                } else {
                    continue;
                }
            }

            libraryService.sourceCodeUrlBuilder(library);

            if (
                !StringUtils.isBlank(originalSourceCodeUrl) &&
                library.getSourceCodeUrl() != null &&
                library.getSourceCodeUrl().equals(Constants.NO_URL)
            ) {
                counter++;
                log.info(
                    "No URL found. Original source code URL : {} - {} - {} : {}",
                    library.getGroupId(),
                    library.getArtifactId(),
                    library.getVersion(),
                    originalSourceCodeUrl
                );
                // library.setSourceCodeUrl(originalSourceCodeUrl);
            }

            libraryService.save(library);
        }
        log.info("Unknown source code URLs : {}", counter);
        log.info("Finished checking Source Code URLs...");
    }

    @Async
    //@Scheduled(fixedDelay = 100000000, initialDelay = 1000)
    public void reloadGithubLicenseText() {
        log.info("Start re-downloading wrong or missing license texts...");
        List<Library> libraries = libraryRepository.findAll();

        for (Library library : libraries) {
            if (!StringUtils.isBlank(library.getLicenseUrl()) && library.getLicenseUrl().contains("github")) {
                if (StringUtils.isBlank(library.getLicenseText())) {
                    try {
                        log.info(
                            "License URL detected but not downloaded for Library [ {} ]. Downloading from license URL : {}",
                            library.getId(),
                            library.getLicenseUrl()
                        );
                        String text = licenseService.downloadLicenseText(library.getLicenseUrl());
                        library.setLicenseText(text);

                        libraryService.save(library);
                    } catch (URISyntaxException e) {
                        log.error(
                            "License text could not be downloaded for Library [ {} ]. URL is invalid [ {} ] : {}",
                            library.getId(),
                            library.getLicenseUrl(),
                            e.getMessage()
                        );
                    } catch (IOException | InterruptedException e) {
                        log.error(
                            "License text could not be downloaded for Library [ {} ]. An error occurred : {}",
                            library.getId(),
                            e.getMessage()
                        );
                    }
                } else {
                    final Pattern pattern = Pattern.compile(
                        "<!doctype html(>| [^>]*>)|<html(>| [^>]*>)|<div class=",
                        Pattern.CASE_INSENSITIVE
                    );

                    if (pattern.matcher(library.getLicenseText()).find()) {
                        try {
                            log.info(
                                "Wrong license text detected for Library [ {} ]. Re-downloading from license URL : {}",
                                library.getId(),
                                library.getLicenseUrl()
                            );
                            String text = licenseService.downloadLicenseText(library.getLicenseUrl());
                            library.setLicenseText(text);

                            libraryService.save(library);
                        } catch (URISyntaxException e) {
                            log.error(
                                "License text could not be downloaded for Library [ {} ]. URL is invalid [ {} ] : {}",
                                library.getId(),
                                library.getLicenseUrl(),
                                e.getMessage()
                            );
                        } catch (IOException | InterruptedException e) {
                            log.error(
                                "License text could not be downloaded for Library [ {} ]. An error occurred : {}",
                                library.getId(),
                                e.getMessage()
                            );
                        }
                    }
                }
            }
        }
        log.info("Finished re-downloading wrong or missing license texts");
    }

    @Async
    // @Scheduled(fixedDelay = 100000000, initialDelay = 1000)
    public void analyseCopyright() {
        log.info("Start copyright analyses for libraries with a empty copyright field..");
        List<Library> libraries = libraryRepository.findByCopyrightNull();

        libraries.forEach(e ->
            log.info("Library with empty copyright field : {} - {} - {}", e.getGroupId(), e.getArtifactId(), e.getVersion())
        );

        libraries.forEach(e -> {
            libraryService.copyrightAutocomplete(e);
            libraryService.save(e);
        });

        log.info("Finished copyright analyses!");
    }

    @Async
    // @Scheduled(fixedDelay = 1000000000, initialDelay = 5000)
    public void saveLibraries() {
        log.info("Start saving all libraries...");
        int counter = 0;
        Pageable pageable = PageRequest.of(0, 500);
        Page<Library> page = libraryRepository.findAll(pageable);

        while (!page.isEmpty()) {
            pageable = pageable.next();

            for (Library library : page.getContent()) {
                log.info("Saving library : {}", library.getId());

                try {
                    libraryService.setLicenseOrderId(library);
                } catch (LibraryException e) {
                    log.warn("License order for library {} is broken", library.getId());
                }
                libraryService.save(library);
                counter++;
            }

            page = libraryRepository.findAll(pageable);
        }
        log.info("COUNTER : {}", counter);
        log.info("Finished saving all libraries!");
    }

    /**
     * Execute after first time run.
     */
    @Async
    //@Scheduled(fixedDelay = 1000000000, initialDelay = 5000)
    public void removeDoubleBackslashFromLicenseMapping() {
        log.info("Start removing double backslashes from License Naming Mapping and Generic License Urls..");
        List<LicenseNamingMapping> licenseNamingMappings = licenseNamingMappingCustomRepository.findAll();
        for (LicenseNamingMapping licenseNamingMapping : licenseNamingMappings) {
            licenseNamingMapping.setRegex(licenseNamingMapping.getRegex().replaceAll("\\\\\\\\", "\\\\"));
            licenseNamingMappingCustomRepository.save(licenseNamingMapping);
        }

        List<GenericLicenseUrl> genericLicenseUrls = genericLicenseUrlCustomRepository.findAll();
        for (GenericLicenseUrl genericLicenseUrl : genericLicenseUrls) {
            genericLicenseUrl.setUrl(genericLicenseUrl.getUrl().replaceAll("\\\\\\\\", "\\\\"));
            genericLicenseUrlCustomRepository.save(genericLicenseUrl);
        }
        log.info("Finished removing double backslashes from License Naming Mapping and Generic License Urls!");
    }

    /**
     * Execute after first time run and after removeDoubleBackslashFromLicenseMapping().
     */
    @Async
    // @Scheduled(fixedDelay = 1000000000, initialDelay = 5000)
    public void saveLibrariesToTriggerAutocompletions() {
        log.info("Start saving all libraries...");
        int counter = 0;
        Pageable pageable = PageRequest.of(0, 300);
        Page<Library> page = libraryRepository.findAll(pageable);

        while (!page.isEmpty()) {
            pageable = pageable.next();

            for (Library library : page.getContent()) {
                log.info("Saving library : {}", library.getId());

                library.setLicenseUrl("");
                library.setSourceCodeUrl("");

                try {
                    libraryService.saveWithCheck(library);
                    counter++;
                } catch (LibraryException e) {
                    log.error("Should not happen!");
                } catch (Exception e) {
                    log.error("Library {} could not be saved!", library.getId());
                }
            }

            page = libraryRepository.findAll(pageable);
        }
        log.info("COUNTER : {}", counter);
        log.info("Finished saving all libraries!");
    }

    /**
     * Execute after first time run and after saveLibrariesToTriggerAutocompletions().
     */
    @Async
    // @Scheduled(fixedDelay = 1000000000, initialDelay = 5000)
    public void createLicenseConflicts() {
        log.info("Start creating license conflicts..");
        List<License> licenses = licenseRepository.findAllWithLicenseConflicts();

        for (License licenseEntity : licenses) {
            log.debug("Processing license : {}", licenseEntity.getShortIdentifier());
            for (License licenseEntity2 : licenses) {
                if (!licenseEntity2.equals(licenseEntity)) {
                    log.debug("Adding license conflict for license : {}", licenseEntity2.getShortIdentifier());
                    LicenseConflict licenseConflict = new LicenseConflict()
                        .firstLicenseConflict(licenseEntity)
                        .secondLicenseConflict(licenseEntity2)
                        .compatibility(CompatibilityState.Unknown);

                    licenseEntity = licenseEntity.addLicenseConflict(licenseConflict);
                }
            }
            licenseConflictRepository.saveAll(licenseEntity.getLicenseConflicts());
        }

        log.info("Finished creating license conflicts!");
    }
}
