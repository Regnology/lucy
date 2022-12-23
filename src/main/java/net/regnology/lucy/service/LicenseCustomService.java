package net.regnology.lucy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.domain.enumeration.CompatibilityState;
import net.regnology.lucy.domain.enumeration.ExportFormat;
import net.regnology.lucy.domain.enumeration.LinkType;
import net.regnology.lucy.repository.*;
import net.regnology.lucy.service.dto.LicenseConflictSimpleDTO;
import net.regnology.lucy.service.dto.LicenseConflictWithRiskDTO;
import net.regnology.lucy.service.dto.LicenseSimpleDTO;
import net.regnology.lucy.service.exceptions.ExportException;
import net.regnology.lucy.service.exceptions.LicenseAlreadyExistException;
import net.regnology.lucy.service.exceptions.LicenseException;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service implementation for managing {@link License}.
 */
@Service
@Transactional
public class LicenseCustomService extends LicenseService {

    private final Logger log = LoggerFactory.getLogger(LicenseCustomService.class);

    private final LicenseCustomRepository licenseRepository;
    private final LicenseNamingMappingCustomRepository licenseNamingMappingRepository;
    private final GenericLicenseUrlCustomRepository genericLicenseUrlRepository;
    private final RequirementCustomRepository requirementRepository;
    private final LicenseRiskCustomRepository licenseRiskRepository;
    private final LicenseConflictCustomRepository licenseConflictRepository;
    private final LibraryCustomService libraryService;

    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    private License unknownLicense;
    private License nonLicensedLicense;

    public LicenseCustomService(
        LicenseCustomRepository licenseRepository,
        LicenseNamingMappingCustomRepository licenseNamingMappingRepository,
        GenericLicenseUrlCustomRepository genericLicenseUrlRepository,
        RequirementCustomRepository requirementRepository,
        LicenseRiskCustomRepository licenseRiskRepository,
        LicenseConflictCustomRepository licenseConflictRepository,
        ObjectMapper objectMapper,
        EntityManager entityManager,
        @Lazy LibraryCustomService libraryService
    ) {
        super(licenseRepository);
        this.licenseRepository = licenseRepository;
        this.licenseNamingMappingRepository = licenseNamingMappingRepository;
        this.genericLicenseUrlRepository = genericLicenseUrlRepository;
        this.requirementRepository = requirementRepository;
        this.licenseRiskRepository = licenseRiskRepository;
        this.licenseConflictRepository = licenseConflictRepository;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
        this.libraryService = libraryService;
    }

    /**
     * Initial check if "Unknown" and "Non-Licensed" license entities exists.
     * If not, they will be automatically created.
     *
     * @throws LicenseException LicenseException if the related {@link LicenseRisk} doesn't exist.
     */
    @PostConstruct
    public void init() throws LicenseException {
        Optional<License> optionalUnknown = licenseRepository.findOneByShortIdentifier(Constants.UNKNOWN);

        if (optionalUnknown.isEmpty()) {
            License license = new License();
            license.setFullName("Unknown License");
            license.setShortIdentifier(Constants.UNKNOWN);
            license.setOther("Do not remove!");

            Optional<LicenseRisk> optionalLicenseRisk = licenseRiskRepository.findOneByName(Constants.UNKNOWN);
            if (optionalLicenseRisk.isPresent()) {
                license.setLicenseRisk(optionalLicenseRisk.get());
            } else {
                throw new LicenseException(
                    "Cannot create" + "\"" + Constants.UNKNOWN + "\"" + "license. \"Unknown\" license risk is missing"
                );
            }

            save(license);
        } else {
            unknownLicense = optionalUnknown.get();
        }

        Optional<License> optionalNonLicensed = licenseRepository.findOneByShortIdentifier(Constants.NON_LICENSED);

        if (optionalNonLicensed.isEmpty()) {
            License license = new License();
            license.setFullName("Library Without License");
            license.setShortIdentifier(Constants.NON_LICENSED);
            license.setOther("Do not remove!");

            Optional<LicenseRisk> optionalLicenseRisk = licenseRiskRepository.findOneByName(Constants.UNKNOWN);
            if (optionalLicenseRisk.isPresent()) {
                license.setLicenseRisk(optionalLicenseRisk.get());
            } else {
                throw new LicenseException(
                    "Cannot create" + "\"" + Constants.NON_LICENSED + "\"" + "license. \"Unknown\" license risk is missing"
                );
            }

            save(license);
        } else {
            nonLicensedLicense = optionalNonLicensed.get();
        }
    }

    /**
     * Save a license. Checks if the license short identifier already exist.
     *
     * @param license the entity to save.
     * @return the persisted entity.
     */
    public License saveWithCheck(License license) throws LicenseException {
        log.debug("Request to save License : {}", license);
        /*
        If ID is null, license is a new Object
        If ID is not null, license Object gets updated
         */
        if (license.getId() == null) {
            Optional<License> optionalLicense = licenseRepository.findOneByShortIdentifier(license.getShortIdentifier());
            if (optionalLicense.isPresent()) {
                throw new LicenseAlreadyExistException(
                    "License with shortIdentifier : " + optionalLicense.get().getShortIdentifier() + " already exist",
                    optionalLicense.get()
                );
            }

            if (license.getLicenseRisk() == null) {
                Optional<LicenseRisk> optionalLicenseRisk = licenseRiskRepository.findOneByName(Constants.UNKNOWN);
                if (optionalLicenseRisk.isPresent()) {
                    license.setLicenseRisk(optionalLicenseRisk.get());
                } else {
                    throw new LicenseException(
                        "Cannot create" +
                        "\"" +
                        license.getShortIdentifier() +
                        "\"" +
                        "license. \"" +
                        Constants.UNKNOWN +
                        "\" license risk is missing."
                    );
                }
            }
            //createLicenseConflictsForNewLicense(license);
        } else {
            Optional<License> optionalLicense = findOne(license.getId());

            if (optionalLicense.isPresent()) {
                License licenseInDb = optionalLicense.get();

                if (!licenseInDb.getShortIdentifier().equals(license.getShortIdentifier())) {
                    if (licenseRepository.findOneByShortIdentifier(license.getShortIdentifier()).isPresent()) {
                        throw new LicenseAlreadyExistException("ShortIdentifier cannot be changed. License already exist");
                    }
                }
            }

            // findChangedLicenseConflict(license.getId(), license.getLicenseConflicts());
            licenseConflictRepository.saveAll(license.getLicenseConflicts());
            // TODO Deactivated as it is very slow and license cannot be saved. A new fast algorithm is necessary
            //libraryService.reevaluateIncompatibleLicenses(license);
        }

        return licenseRepository.save(license);
    }

    /**
     * Get all the licenses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<License> findAllWithEagerRequirements(Pageable pageable) {
        long count = (long) entityManager.createQuery("select count(license.id) from License license").getSingleResult();

        List<Long> licenseIds = entityManager
            .createQuery("select distinct license.id from License license", Long.class)
            .setFirstResult((pageable.getPageNumber()) * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        List<License> licenses = entityManager
            .createQuery(
                "select distinct license from License license left join fetch license.requirements where license.id in (:licenseIds)",
                License.class
            )
            .setParameter("licenseIds", licenseIds)
            .getResultList();

        return new PageImpl<>(licenses, pageable, count);
    }

    /**
     * Get one license entity by a license name.
     * Maps the license name to a unique shortIdentifier to get the correct license entity.
     * {@link LicenseNamingMapping} Entity contains all mappings.
     *
     * @param license name
     * @return The concrete license entity if it could be found.
     * "Unknown" license entity if there is no license mapping or the license is not in the database.
     * "Non-Licensed" license entity if the license name is empty or "-".
     */
    @Transactional(readOnly = true)
    public License findOneShortIdentifier(String license) {
        log.debug("Request to get License by license name : {}", license);

        if (license.isEmpty() || license.equals("-")) return nonLicensedLicense;
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();

        for (LicenseNamingMapping mapping : licenseNamingMappingList) {
            String pattern = mapping.getRegex();
            String uniformShortIdentifier = mapping.getUniformShortIdentifier();
            String shortIdentifierReplaced = license.replaceAll(pattern, uniformShortIdentifier);

            if (shortIdentifierReplaced.equals(uniformShortIdentifier)) {
                license = shortIdentifierReplaced;
                break;
            }
        }

        Optional<License> optionalLicense = licenseRepository.findOneByShortIdentifier(license);
        return optionalLicense.orElse(unknownLicense);
    }

    /**
     * Get a set of license(s) by a string that contains different licenses.
     * String can contain connections between licenses.
     * Possible connections (case insensitive): "/", "+", "or", "and"
     *
     * @param license name(s) of a library
     * @return a Set of License entities
     */
    @Transactional(readOnly = true)
    public Set<License> findShortIdentifier(String license) {
        log.debug("Request to get License(s) by license name : {}", license);
        String splitRegex = "/|\\s\\+\\s|\\s[oO][rR]\\s|\\s[aA][nN][dD]\\s";

        license = license.trim();
        String[] identifiers;
        identifiers = license.split(splitRegex);

        if (identifiers.length == 0) {
            identifiers = new String[] { license };
        }

        return Arrays.stream(identifiers).map(String::trim).map(this::findOneShortIdentifier).collect(Collectors.toSet());
    }

    public SortedSet<LicensePerLibrary> findLicenseWithLinking(String license) {
        log.debug("Request to get linked license(s) by license : {}", license);

        license = license.trim();
        String[] identifiers;
        identifiers = license.split(Constants.licenseSplitRegex);

        SortedSet<LicensePerLibrary> linkedLicenses = new TreeSet<>();
        int orderIdCounter = 0;

        // Counter for elements. Even value equals license. Odd value equals link type
        int counter = 0;
        LicensePerLibrary lastLinkedLicense = null;
        for (String identifier : identifiers) {
            if (counter % 2 == 0) {
                lastLinkedLicense = new LicensePerLibrary();
                lastLinkedLicense.setLicense(findOneShortIdentifier(identifier.trim()));
                lastLinkedLicense.setOrderId(orderIdCounter);
                orderIdCounter++;
            } else {
                lastLinkedLicense.setLinkType(LinkType.getLinkType(identifier.trim()));
            }

            linkedLicenses.add(lastLinkedLicense);
            counter++;
        }

        return linkedLicenses;
    }

    public Set<License> findLicenseToPublish(SortedSet<LicensePerLibrary> license) {
        Stack<License> licenses = new Stack<>();

        LinkType lastLinkType = LinkType.AND;
        for (LicensePerLibrary linkedLicense : license) {
            if (licenses.empty()) {
                licenses.add(linkedLicense.getLicense());
            } else {
                if (lastLinkType == null) {
                    break;
                }
                switch (lastLinkType) {
                    case OR:
                        // If Unknown or Non-licensed license then add it always
                        if (linkedLicense.getLicense().equals(unknownLicense) || linkedLicense.getLicense().equals(nonLicensedLicense)) {
                            licenses.pop();
                            licenses.push(linkedLicense.getLicense());
                            break;
                        }

                        License licenseTop = licenses.peek();

                        // Remove license on top if the next license has a lower risk and license on top is not
                        if (
                            linkedLicense.getLicense().getLicenseRisk().getLevel() < licenseTop.getLicenseRisk().getLevel() &&
                            (!licenseTop.equals(unknownLicense) && !licenseTop.equals(nonLicensedLicense))
                        ) {
                            licenses.pop();
                            licenses.push(linkedLicense.getLicense());
                        }
                        break;
                    case AND:
                        licenses.push(linkedLicense.getLicense());
                        break;
                }
            }
            lastLinkType = linkedLicense.getLinkType();
        }

        return new HashSet<>(licenses);
    }

    /**
     * Check if a URL points to a generic license text.
     *
     * @param url License URL
     * @return true if it is a generic license URL, otherwise false
     */
    public boolean isGenericLicenseUrl(String url) {
        log.debug("Checking if URL is generic license URL : {}", url);

        if (url != null && !url.isEmpty()) {
            for (GenericLicenseUrl genericLicenseUrl : genericLicenseUrlRepository.findAll()) {
                Pattern genericPattern = Pattern.compile(genericLicenseUrl.getUrl());
                Matcher genericMatcher = genericPattern.matcher(url);

                if (genericMatcher.find()) {
                    log.debug("Found generic license URL : {}", url);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a String contains URL(s) to generic license texts.
     *
     * @param urls License URL(s) that can be delimited by ","
     * @return a list of license URL(s)
     */
    public List<String> cleansedLicenseUrl(String urls) {
        log.debug("Checking if URL contains generic license URL : {}", urls);

        String[] urlSplitted = urls.split("\\s*,\\s*");
        if (urlSplitted.length == 0) urlSplitted = new String[] { urls };

        List<String> licenseUrls = new ArrayList<>(3);

        for (String url : urlSplitted) {
            if (!isGenericLicenseUrl(url)) {
                licenseUrls.add(url);
            }
        }

        return licenseUrls;
    }

    /**
     * Downloads the HTML page from an URL.
     *
     * @param url URL to the HTML page
     * @return Body of the HTML page
     * @throws URISyntaxException   if the url is not valid
     * @throws IOException          if an I/O error occurs while downloading the url
     * @throws InterruptedException if the download gets interrupted
     */
    public String downloadLicenseText(String url) throws URISyntaxException, IOException, InterruptedException {
        log.debug("Downloading URL : {}", url);

        if (url.contains("github.com") && url.contains("/blob/")) {
            url = url.replace("github.com", "raw.githubusercontent.com").replace("raw.raw.", "raw.").replace("/blob/", "/");
        }

        String licenseText = HttpHelper.httpGetRequest(url).body();

        if (!isHTML(licenseText)) {
            licenseText = "<pre>\n" + StringEscapeUtils.escapeHtml4(licenseText.stripTrailing()) + "\n</pre>";
        }

        Document document = Jsoup.parse(licenseText);

        return document.body().html();
    }

    /**
     * Checks if a text is a HTML page. Searches for the &lt;!DOCTYPE&gt; or &lt;html&gt; tag.
     *
     * @param text text to check
     * @return true if it is a HTML page, otherwise false
     */
    private static boolean isHTML(String text) {
        final Pattern pattern = Pattern.compile("<!doctype html(>| [^>]*>)|<html(>| [^>]*>)", Pattern.CASE_INSENSITIVE);

        return pattern.matcher(text).find();
    }

    public boolean unidentifiedLicense(SortedSet<LicensePerLibrary> licenses) {
        boolean allUnidentified = false;
        for (LicensePerLibrary licensePerLibrary : licenses) {
            if (licensePerLibrary.getLicense().getShortIdentifier().equals(nonLicensedLicense.getShortIdentifier())) {
                allUnidentified = true;
            } else {
                return false;
            }
        }

        return allUnidentified;
    }

    public boolean unidentifiedLicense(Set<License> licenses) {
        boolean allUnidentified = false;
        for (License license : licenses) {
            if (license.getShortIdentifier().equals(nonLicensedLicense.getShortIdentifier())) {
                allUnidentified = true;
            } else {
                return false;
            }
        }

        return allUnidentified;
    }

    public void createLicenseConflictsForNewLicense(License license) {
        log.debug("Create for new license {} LicenseConflicts to every other license", license.getShortIdentifier());
        List<License> licenses = licenseRepository.findAll();

        for (License licenseEntity : licenses) {
            if (!licenseEntity.equals(license)) {
                LicenseConflict licenseConflict = new LicenseConflict()
                    .firstLicenseConflict(license)
                    .secondLicenseConflict(licenseEntity)
                    .compatibility(CompatibilityState.Unknown);

                license = license.addLicenseConflict(licenseConflict);
            }
        }

        licenseConflictRepository.saveAll(license.getLicenseConflicts());
    }

    public void createLicenseConflictsForExistingLicenses(License license) {
        log.debug("Create LicenseConflicts in every existing license with license {}", license.getShortIdentifier());
        List<License> licenses = licenseRepository.findAll();

        for (License licenseEntity : licenses) {
            if (!licenseEntity.equals(license)) {
                LicenseConflict licenseConflict = new LicenseConflict()
                    .firstLicenseConflict(licenseEntity)
                    .secondLicenseConflict(license)
                    .compatibility(CompatibilityState.Unknown);

                //licenseEntity = licenseEntity.addLicenseConflict(licenseConflict);
                licenseConflictRepository.save(licenseConflict);
            }
        }
    }

    public boolean checkForLicenseIncompatibility(List<License> licenses) {
        Optional<LicenseConflict> licenseConflict = licenseConflictRepository.findIncompatibleLicenseConflict(
            licenses.get(0).getId(),
            licenses.get(1).getId()
        );

        return licenseConflict.isPresent();
    }

    public Set<LicenseConflict> findChangedLicenseConflict(Long licenseId, SortedSet<LicenseConflict> licenseConflicts) {
        List<LicenseConflict> licenseConflictsDB = licenseConflictRepository.findLicenseConflictsByLicenseId(licenseId);
        Set<LicenseConflict> changedLicenseConflicts = new HashSet<>();

        for (LicenseConflict licenseConflict : licenseConflicts) {
            for (LicenseConflict licenseConflictDB : licenseConflictsDB) {
                if (licenseConflict.getId().equals(licenseConflictDB.getId())) {
                    if (!licenseConflict.getCompatibility().equals(licenseConflictDB.getCompatibility())) {
                        changedLicenseConflicts.add(licenseConflict);
                    }
                    break;
                }
            }
        }

        return changedLicenseConflicts;
    }

    public List<LicenseConflictSimpleDTO> fetchLicenseConflicts(Long id) {
        return licenseRepository.fetchLicenseConflicts(id);
    }

    public List<LicenseConflictWithRiskDTO> fetchLicenseConflictsWithRisk(Long id) {
        return licenseRepository.fetchLicenseConflictsWithRisk(id);
    }

    public List<LicenseSimpleDTO> findAllSimpleDTO() {
        return licenseRepository.findAllSimpleDTO();
    }

    /**
     * Delete the license by id with license conflicts.
     *
     * @param id the id of the license entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete License : {}", id);
        licenseConflictRepository.deleteByLicenseId(id);
        licenseRepository.deleteById(id);
    }

    /**
     * Export the license table. Supported formats are JSON and CSV.
     *
     * @param format Format of the export. {@link ExportFormat}
     * @return A file with the license export as the content
     * @throws ExportException if the license table cannot be serialized or the format is not suppoerted.
     */
    public File export(ExportFormat format) throws ExportException {
        log.debug("Request to export licenses");
        final String baseFileName = "licenses";
        String fileName;

        switch (format) {
            case JSON:
                // Page<License> licensePageTest = findAllWithEagerRelationships(PageRequest.of(0, 200, Sort.by(Sort.Order.asc("shortIdentifier"))));
                List<License> licenses = licenseRepository.findAll();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2097152);

                try {
                    // outputStream.write(objectMapper.writeValueAsBytes(licensePageTest.getContent()));
                    outputStream.write(objectMapper.writeValueAsBytes(licenses));
                } catch (IOException e) {
                    log.error("Licenses could not be serialized to JSON : {}", e.getMessage());
                    throw new ExportException("Error while exporting licenses to JSON");
                }

                fileName = baseFileName + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".json";
                return new File(fileName, outputStream.toByteArray(), "application/json");
            case CSV:
                Page<License> licensePage = findAllWithEagerRequirements(
                    PageRequest.of(0, 200, Sort.by(Sort.Order.asc("shortIdentifier")))
                );
                int totalPages = licensePage.getTotalPages();
                int currentPage = 0;

                List<String> requirementShortTexts = requirementRepository
                    .findAll()
                    .stream()
                    .map(Requirement::getShortText)
                    .collect(Collectors.toList());

                List<String> headers = new ArrayList<>(16);
                headers.add("FullName");
                headers.add("ShortIdentifier");
                headers.add("SpdxIdentifier");
                headers.add("Url");
                headers.add("Other");
                headers.add("Reviewed");
                headers.add("LastReviewedBy");
                headers.add("LastReviewedDate");
                headers.add("LicenseRisk");
                headers.addAll(requirementShortTexts);

                StringBuilder csvBuilder = new StringBuilder(2097152);
                CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withHeader(headers.toArray(new String[0]));

                while (currentPage < totalPages) {
                    if (currentPage == 1) csvFormat = CSVFormat.DEFAULT.withDelimiter(';');
                    try (CSVPrinter csvPrinter = new CSVPrinter(csvBuilder, csvFormat)) {
                        for (License license : licensePage.getContent()) {
                            List<String> licenseRequirements = license
                                .getRequirements()
                                .stream()
                                .map(Requirement::getShortText)
                                .collect(Collectors.toList());

                            List<String> requirements = requirementShortTexts
                                .stream()
                                .map(requirement -> licenseRequirements.contains(requirement) ? "x" : "")
                                .collect(Collectors.toList());

                            List<String> record = new ArrayList<>(12);
                            record.add(license.getFullName());
                            record.add(license.getShortIdentifier());
                            record.add(license.getSpdxIdentifier());
                            record.add(license.getUrl());
                            record.add(license.getOther());
                            record.add(license.getReviewed() ? "x" : "");
                            record.add(license.getLastReviewedBy() != null ? license.getLastReviewedBy().getLogin() : "");
                            record.add(
                                license.getLastReviewedDate() != null
                                    ? license.getLastReviewedDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                                    : ""
                            );
                            record.add(license.getLicenseRisk().getName());
                            record.addAll(requirements);

                            csvPrinter.printRecord(record);
                        }
                        csvPrinter.flush();
                        currentPage++;
                        licensePage =
                            findAllWithEagerRequirements(PageRequest.of(currentPage, 200, Sort.by(Sort.Order.asc("shortIdentifier"))));
                    } catch (IOException e) {
                        log.error("Licenses could not be serialized to CSV");
                        throw new ExportException("Error while exporting licenses to CSV");
                    }
                }

                fileName = baseFileName + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".csv";
                return new File(fileName, csvBuilder.toString().getBytes(StandardCharsets.UTF_8), "text/csv");
            default:
                throw new ExportException("Unsupported export format : " + format.getValue());
        }
    }

    /* Getter and Setter */

    public License getUnknownLicense() {
        return unknownLicense;
    }

    public License getNonLicensedLicense() {
        return nonLicensedLicense;
    }
}
