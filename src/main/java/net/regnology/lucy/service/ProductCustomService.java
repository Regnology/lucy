package net.regnology.lucy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

import net.regnology.lucy.config.ApplicationProperties;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.domain.enumeration.*;
import net.regnology.lucy.domain.helper.BasicAuthentication;
import net.regnology.lucy.domain.helper.DifferenceView;
import net.regnology.lucy.domain.helper.Upload;
import net.regnology.lucy.domain.statistics.CountOccurrences;
import net.regnology.lucy.domain.statistics.ProductOverview;
import net.regnology.lucy.domain.statistics.ProductStatistic;
import net.regnology.lucy.domain.statistics.Series;
import net.regnology.lucy.repository.LicenseRiskCustomRepository;
import net.regnology.lucy.repository.ProductCustomRepository;
import net.regnology.lucy.repository.RequirementCustomRepository;
import net.regnology.lucy.repository.UserCustomRepository;
import net.regnology.lucy.service.exceptions.*;
import net.regnology.lucy.service.helper.LicenseZipHelper;
import net.regnology.lucy.service.helper.OssListHelper;
import net.regnology.lucy.service.helper.net.HttpHelper;
import net.regnology.lucy.service.helper.sourceCode.SourceCodeHelper;
import net.regnology.lucy.service.pipeline.MavenLicenseStep;
import net.regnology.lucy.service.pipeline.NpmLicenseStep;
import net.regnology.lucy.service.pipeline.Pipeline;
import net.regnology.lucy.service.upload.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

/**
 * Custom service implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductCustomService extends ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductCustomService.class);

    private final Logger archiveLog = LoggerFactory.getLogger("archive");

    private final ProductCustomRepository productRepository;

    private final LibraryPerProductCustomService libraryPerProductService;

    private final LicenseCustomService licenseService;

    private final LibraryCustomService libraryService;

    private final RequirementCustomRepository requirementRepository;

    private final LicenseRiskCustomRepository licenseRiskRepository;

    private final UserCustomRepository userRepository;

    private final ApplicationProperties applicationProperties;

    private final EntityManager entityManager;

    private final AssetManager<Library> assetManager;

    public ProductCustomService(
        ProductCustomRepository productRepository,
        LibraryPerProductCustomService libraryPerProductService,
        LicenseCustomService licenseService,
        RequirementCustomRepository requirementRepository,
        LicenseRiskCustomRepository licenseRiskRepository,
        LibraryCustomService libraryService,
        ObjectMapper objectMapper,
        UserCustomRepository userRepository,
        EntityManager entityManager,
        ApplicationProperties applicationProperties
    ) {
        super(productRepository);
        this.productRepository = productRepository;
        this.libraryPerProductService = libraryPerProductService;
        this.licenseService = licenseService;
        this.libraryService = libraryService;
        this.requirementRepository = requirementRepository;
        this.licenseRiskRepository = licenseRiskRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.applicationProperties = applicationProperties;

        assetManager = new AssetManager<>();

        AssetLoader<Library> bomLoader = new BomLoader();
        AssetLoader<Library> csvLoader = new LibraryCsvLoader();
        AssetLoader<Library> archiveLoader = new ArchiveLoader(libraryService);

        assetManager.addLoader(bomLoader, "text/xml");
        assetManager.addLoader(bomLoader, "application/xml");
        assetManager.addLoader(csvLoader, "application/vnd.ms-excel");
        assetManager.addLoader(csvLoader, "text/csv");
        assetManager.addLoader(archiveLoader, "application/zip");
        assetManager.addLoader(archiveLoader, "application/x-zip-compressed");
        assetManager.addLoader(archiveLoader, "application/gzip");
        assetManager.addLoader(archiveLoader, "application/java-archive");
        assetManager.addLoader(archiveLoader, "application/x-tar");
        assetManager.addLoader(archiveLoader, "application/octet-stream");
        assetManager.addLoader(
            file -> {
                try {
                    return objectMapper.readValue(file.getFile(), new TypeReference<>() {
                    });
                } catch (IOException e) {
                    log.error("Error while parsing JSON file : {}", e.getMessage());
                    throw new UploadException("JSON file can't be read");
                }
            },
            "application/json"
        );
    }

    /**
     * Save a product. Checks if the delivered field got changed to the set the delivered date.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    public Product saveWithCheck(Product product) {
        log.debug("Request to save Product : {}", product);
        /*
        If ID is null, product is a new Object
        If ID is not null, product Object gets updated.
         */
        if (product.getId() != null) {
            Optional<Product> productInDb = findOne(product.getId());
            if (productInDb.isPresent()) {
                if (product.getDelivered() && !productInDb.get().getDelivered()) {
                    product.setDeliveredDate(Instant.now());
                } else if (!product.getDelivered() && productInDb.get().getDelivered()) {
                    product.setDeliveredDate(null);
                }
            }
        }

        return productRepository.save(product);
    }

    public Product saveAndFlush(Product product) {
        log.debug("Request to save and flush Product : {}", product);
        return productRepository.saveAndFlush(product);
    }

    /**
     * Delete the product by id with libraries.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        libraryPerProductService.deleteByProduct(id);
        productRepository.deleteById(id);
    }

    /**
     * Überprüfe, ob ein Projekt anhand der ID existiert.
     *
     * @param id ID des Projekts.
     * @return true, wenn das Projekt existiert, ansonsten false.
     */
    public boolean existsById(Long id) {
        log.debug("Request to check if a Product exist by ID : {}", id);
        return productRepository.existsById(id);
    }

    /**
     * Get one product by identifier and version.
     *
     * @param identifier the product identifier
     * @param version    the product version
     * @return the optional product entity
     */
    public Optional<Product> findIdentifierVersion(String identifier, String version) {
        log.debug("Request to get Product : {} with Version : {}", identifier, version);
        return productRepository.findByIdentifierAndVersion(identifier, version);
    }

    /**
     * Create a license ZIP file for a specific product.
     *
     * @param product the product entity
     * @return the license ZIP as a byte array
     */
    public byte[] createLicenseZip(Product product) throws FileNotFoundException {
        List<Library> libraries = libraryPerProductService
            .findLibraryPerProductsByProductId(product.getId())
            .stream()
            .filter(LibraryPerProduct::isHidden)
            .map(LibraryPerProduct::getLibrary)
            .collect(Collectors.toList());
        try {
            LicenseZipHelper licenseZipHelper = new LicenseZipHelper(product, libraries);
            licenseZipHelper.addLicenseTexts();
            licenseZipHelper.addCopyrights();
            licenseZipHelper.addDisclaimer(product.getDisclaimer());
            licenseZipHelper.addHtmlOverview("FOSS_Software_" + product.getName(), "style");

            return licenseZipHelper.getLicenseZip();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * Creates an OSS list as a CSV or HTML file.
     * It can create three different types of OSS lists.
     * DEFAULT: OSS list with all libraries and different information like license risk.
     * PUBLISH: OSS list with distinct libraries (based on GroupId, ArtifactId) and license(s) to be published.
     * REQUIREMENT: OSS list with all libraries and the corresponding requirements.
     *
     * @param product A product entity
     * @param format  {@link ExportFormat Export format}. Supports CSV or HTML
     * @param ossType The {@link OssType OSS list type}
     * @return A {@link File} object
     */
    public File createOssList(Product product, ExportFormat format, OssType ossType) throws ExportException {
        String fileName =
            product.getIdentifier() +
                "_" +
                product.getVersion() +
                "_" +
                "%{type}%" +
                "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));

        byte[] content = new byte[]{};
        String contentType = null;
        Deque<String> headers = null;
        StringBuilder csvBuilder = null;
        List<Deque<String>> libraries = null;

        switch (ossType) {
            case REQUIREMENT:
                fileName = fileName.replace("%{type}%", Constants.OSS_REQUIREMENT_PREFIX);
                if (format.equals(ExportFormat.HTML)) {
                    try {
                        OssListHelper ossListHelper = new OssListHelper(
                            product,
                            libraryPerProductService.findLibrariesByProductId(product.getId()),
                            false
                        );

                        List<String> requirementsLookup = requirementRepository
                            .findAll()
                            .stream()
                            .map(Requirement::getShortText)
                            .collect(Collectors.toList());

                        ossListHelper.createFullHtml(requirementsLookup);
                        content = ossListHelper.getHtml().getBytes(StandardCharsets.UTF_8);
                    } catch (FileNotFoundException e) {
                        throw new ExportException("HTML template could not be found");
                    }
                } else if (format.equals(ExportFormat.CSV)) {
                    headers = new ArrayDeque<>(
                        Arrays.asList("GroupId", "ArtifactId", "Version", "License", "LicenseRisk", "LicensesTotal", "Comment", "ComplianceComment")
                    );

                    List<String> requirementsLookup = requirementRepository
                        .findAll()
                        .stream()
                        .map(Requirement::getShortText)
                        .collect(Collectors.toList());

                    headers.addAll(requirementsLookup);
                    csvBuilder = new StringBuilder(65536);
                    libraries = createOssListWithRequirements(product, requirementsLookup);
                }
                break;
            case DEFAULT:
                fileName = fileName.replace("%{type}%", Constants.OSS_DEFAULT_PREFIX);
                if (format.equals(ExportFormat.HTML)) {
                    try {
                        OssListHelper ossListHelper = new OssListHelper(
                            product,
                            libraryPerProductService.findLibrariesByProductId(product.getId()),
                            false
                        );
                        ossListHelper.createHml(false);
                        content = ossListHelper.getHtml().getBytes(StandardCharsets.UTF_8);
                    } catch (FileNotFoundException e) {
                        throw new ExportException("HTML template could not be found");
                    }
                } else if (format.equals(ExportFormat.CSV)) {
                    headers = new ArrayDeque<>(8);
                    headers.add("GroupId");
                    headers.add("ArtifactId");
                    headers.add("Version");
                    headers.add("Type");
                    headers.add("License");
                    headers.add("LicenseRisk");
                    headers.add("LicenseUrl");
                    headers.add("SourceCodeUrl");

                    csvBuilder = new StringBuilder(65536);

                    libraries =
                        libraryPerProductService
                            .findLibrariesByProductId(product.getId())
                            .stream()
                            .map(library -> {
                                Deque<String> records = new ArrayDeque<>(8);
                                records.add(library.getGroupId());
                                records.add(library.getArtifactId());
                                records.add(library.getVersion() != null ? "V" + library.getVersion() : "");
                                records.add(library.getType() != null ? library.getType().getValue() : "");
                                records.add(library.printLinkedLicenses());
                                records.add(library.getLicenseRisk(library.getLicenseToPublishes()).getName());
                                records.add(
                                    library.getLicenseUrl() != null &&
                                        !StringUtils.isBlank(library.getLicenseUrl()) &&
                                        !library.getLicenseUrl().equalsIgnoreCase(Constants.NO_URL)
                                        ? library.getLicenseUrl()
                                        : library.getLicenseToPublishes().stream().map(License::getUrl).collect(Collectors.joining(", "))
                                );
                                records.add(library.getSourceCodeUrl() != null ? library.getSourceCodeUrl() : "");
                                return records;
                            })
                            .collect(Collectors.toList());
                }
                break;
            case PUBLISH:
                fileName = fileName.replace("%{type}%", Constants.OSS_PUBLISH_PREFIX);
                if (format.equals(ExportFormat.HTML)) {
                    try {
                        OssListHelper ossListHelper = new OssListHelper(
                            product,
                            libraryPerProductService
                                .findLibraryPerProductsByProductId(product.getId())
                                .stream()
                                .filter(LibraryPerProduct::isHidden)
                                .map(LibraryPerProduct::getLibrary)
                                .collect(Collectors.toList()),
                            true
                        );
                        ossListHelper.createHml(true);
                        content = ossListHelper.getHtml().getBytes(StandardCharsets.UTF_8);
                    } catch (FileNotFoundException e) {
                        throw new ExportException("HTML template could not be found");
                    }
                } else if (format.equals(ExportFormat.CSV)) {
                    List<Library> libraryList = libraryPerProductService
                        .findLibraryPerProductsByProductId(product.getId())
                        .stream()
                        .filter(LibraryPerProduct::isHidden)
                        .map(LibraryPerProduct::getLibrary)
                        .collect(Collectors.toList());

                    OssListHelper ossListHelper = new OssListHelper(libraryList);
                    try {
                        byte[] csv = ossListHelper.createPublishCsv();

                        return new File(fileName + ".csv", csv, "text/csv");
                    } catch (IOException e) {
                        log.error("OSS list could not be created as a CSV : {}", e.getMessage());
                        throw new ExportException("Error while exporting OSS list");
                    }
                } else {
                    throw new ExportException("Unsupported OSS list format");
                }
                break;
            default:
                throw new ExportException("Unsupported OSS list type");
        }

        if (format.equals(ExportFormat.HTML)) {
            fileName = fileName + ".html";
            contentType = "text/html";
        } else if (format.equals(ExportFormat.CSV)) {
            fileName = fileName + ".csv";
            contentType = "text/csv";
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader(headers.toArray(new String[0]))
                .build();

            try (CSVPrinter csvPrinter = new CSVPrinter(csvBuilder, csvFormat)) {
                for (Deque<String> library : libraries) {
                    csvPrinter.printRecord(library.toArray());
                }

                csvPrinter.flush();
                content = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("OSS list could not be created as a CSV : {}", e.getMessage());
                throw new ExportException("Error while exporting OSS list");
            }
        }

        return new File(fileName, content, contentType);
    }

    /**
     * Helper for the "Full" OSS CSV report to create the content with library information and
     * the requirements to fulfill.
     *
     * @param product            For which the list should be created
     * @param requirementsLookup List with all requirements
     * @return List with Deque objects which contains all information per row in the CSV
     */
    private List<Deque<String>> createOssListWithRequirements(Product product, List<String> requirementsLookup) {
        List<Deque<String>> content = new ArrayList<>();
        Set<String> totalLicenses = new HashSet<>();

        List<Library> libraries = libraryPerProductService.findLibrariesByProductId(product.getId());
        for (Library libraryPerProduct : libraries) {
            String groupId = libraryPerProduct.getGroupId();
            String artifactId = libraryPerProduct.getArtifactId();
            String version = libraryPerProduct.getVersion();
            String licenseRisk = libraryPerProduct.getLicenseRisk(libraryPerProduct.getLicenseToPublishes()).getName();
            List<String> requirements = new ArrayList<>(16);
            String comment = libraryPerProduct.getComment();
            String complianceComment = libraryPerProduct.getComplianceComment();

            // Set initially empty string for every requirement
            for (String ignored : requirementsLookup) {
                requirements.add("");
            }

            libraryPerProduct.getLicenses().forEach(e -> totalLicenses.add(e.getLicense().getShortIdentifier()));

            if (libraryPerProduct.getLicenseToPublishes() != null && !libraryPerProduct.getLicenseToPublishes().isEmpty()) {
                for (License licenseToPublish : libraryPerProduct.getLicenseToPublishes()) {
                    if (licenseToPublish.getRequirements() != null) {
                        for (Requirement requirement : licenseToPublish.getRequirements()) {
                            if (requirementsLookup.contains(requirement.getShortText())) {
                                requirements.remove(requirementsLookup.indexOf(requirement.getShortText()));
                                requirements.add(requirementsLookup.indexOf(requirement.getShortText()), "X");
                            }
                        }
                    }
                }
            }

            List<String> libraryRow = new ArrayList<>(Arrays.asList(
                groupId,
                artifactId,
                "V" + version,
                libraryPerProduct.printLinkedLicenses(),
                licenseRisk,
                String.valueOf(totalLicenses.size()),
                comment != null ? comment : "",
                complianceComment != null ? complianceComment : ""
            ));

            libraryRow.addAll(requirements);
            content.add(new ArrayDeque<>(libraryRow));
        }

        return content;
    }

    public void transferBundleToTarget(Product product) {
        log.debug("Transfer OSS bundle to target");

        if (!StringUtils.isBlank(product.getTargetUrl())) {
            try {
                File html_default = createOssList(product, ExportFormat.HTML, OssType.DEFAULT);
                File csv_default = createOssList(product, ExportFormat.CSV, OssType.DEFAULT);
                File html_publish = createOssList(product, ExportFormat.HTML, OssType.PUBLISH);
                File csv_publish = createOssList(product, ExportFormat.CSV, OssType.PUBLISH);
                HttpHelper.transferToTarget(product.getTargetUrl(), html_default.getFile(), html_default.getFileName());
                HttpHelper.transferToTarget(product.getTargetUrl(), csv_default.getFile(), csv_default.getFileName());
                HttpHelper.transferToTarget(product.getTargetUrl(), html_publish.getFile(), html_publish.getFileName());
                HttpHelper.transferToTarget(product.getTargetUrl(), csv_publish.getFile(), csv_publish.getFileName());
                HttpHelper.transferToTarget(
                    product.getTargetUrl(),
                    createLicenseZip(product),
                    product.getIdentifier() +
                        "_" +
                        product.getVersion() +
                        "_" +
                        Constants.OSS_ZIP_PREFIX +
                        "_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)) +
                        ".zip"
                );
            } catch (ExportException e) {
                log.error(
                    "Error while creating OSS list for product : {} {} : {}",
                    product.getName(),
                    product.getVersion(),
                    e.getMessage()
                );
            } catch (FileNotFoundException e) {
                log.error(
                    "Error while creating License Text Archive for product : {} {} : {}",
                    product.getName(),
                    product.getVersion(),
                    e.getMessage()
                );
            }
        }
    }

    public ProductOverview getProductOverview(Product product) {
        // Total libraries from current product version and previous version
        long numberOfLibraries = libraryPerProductService.countLibrariesByProduct(product.getId());
        long numberOfLibrariesPrevious = libraryPerProductService.countLibrariesByProduct(product.getPreviousProductId());

        // Number of licenses
        long numberOfLicenses = libraryPerProductService
            .countDistributedLicensesByProductId(product.getId())
            .stream()
            .map(CountOccurrences::getValue)
            .count();

        // Number of licenses from the previous product version
        long numberOfLicensesPrevious = libraryPerProductService
            .countDistributedLicensesByProductId(product.getPreviousProductId())
            .stream()
            .map(CountOccurrences::getValue)
            .count();

        // Total number of reviewed libraries for the current product
        long numberOfReviewedLibraries = libraryPerProductService.countReviewedLibrariesByProduct(product.getId());

        return new ProductOverview(
            numberOfLibraries,
            numberOfLibrariesPrevious,
            numberOfLicenses,
            numberOfLicensesPrevious,
            numberOfReviewedLibraries
        );
    }

    /**
     * Get a {@link ProductOverview} of a product.
     *
     * @param product product entity
     * @return a ProductOverview
     */
    public ProductStatistic getProductStatistic(Product product) {
        // Distributed licenses list
        List<CountOccurrences> distributedLicenses = libraryPerProductService.countDistributedLicensesByProductId(product.getId());

        List<Series> licenseRiskSeries = new ArrayList<>(7);
        List<Series> numberOfLibrariesSeries = new ArrayList<>(1);

        // Add initial total libraries from current product
        Series librariesSeries = new Series("Libraries", new ArrayDeque<>(Constants.MAX_SERIES_LIMIT));
        librariesSeries
            .getSeries()
            .addFirst(new CountOccurrences(product.getVersion(), libraryPerProductService.countLibrariesByProduct(product.getId())));
        numberOfLibrariesSeries.add(librariesSeries);

        // Add initial risks from current product
        getProductRisk(product)
            .forEach(e -> {
                Deque<CountOccurrences> riskPerVersion = new ArrayDeque<>(Constants.MAX_SERIES_LIMIT);
                riskPerVersion.add(new CountOccurrences(product.getVersion(), e.getValue()));
                licenseRiskSeries.add(new Series(e.getName(), riskPerVersion));
            });

        // Get previous product
        Optional<Product> prevProduct = Optional.empty();
        if (product.getPreviousProductId() != null) {
            prevProduct = productRepository.findById(product.getPreviousProductId());
        }

        int maxSeriesCounter = 1;
        while (prevProduct.isPresent() && maxSeriesCounter <= Constants.MAX_SERIES_LIMIT) {
            List<CountOccurrences> risks = getProductRisk(prevProduct.get());
            librariesSeries.addSeriesEntry(
                prevProduct.get().getVersion(),
                libraryPerProductService.countLibrariesByProduct(prevProduct.get().getId())
            );

            int counter = 0;
            for (CountOccurrences e : risks) {
                Series risk = licenseRiskSeries.get(counter);

                if (e.getName().equals(risk.getName())) {
                    risk.addSeriesEntry(prevProduct.get().getVersion(), e.getValue());
                }

                counter++;
            }

            if (prevProduct.get().getPreviousProductId() != null) {
                prevProduct = productRepository.findById(prevProduct.get().getPreviousProductId());
            } else {
                prevProduct = Optional.empty();
            }
            maxSeriesCounter++;
        }

        return new ProductStatistic(distributedLicenses, getProductRisk(product), licenseRiskSeries, numberOfLibrariesSeries);
    }

    /**
     * Count the number of libraries by the license risks of a product.
     *
     * @param product Product entity
     * @return an ordered map of license risk name and number of libraries.
     */
    public List<CountOccurrences> getProductRisk(Product product) {
        List<Library> libraries = libraryPerProductService.findLibrariesByProductId(product.getId());

        return getProductRisk(libraries);
    }

    /**
     * Count the number of libraries by the license risks of a product.
     *
     * @param id Product id
     * @return an ordered map of license risk name and number of libraries.
     */
    public List<CountOccurrences> getProductRisk(Long id) {
        List<Library> libraries = libraryPerProductService.findLibrariesByProductId(id);

        return getProductRisk(libraries);
    }

    /**
     * Count the number of libraries by the license risks of a product.
     *
     * @param libraries Libraries of a Product
     * @return an ordered map of license risk name and number of libraries.
     */
    public List<CountOccurrences> getProductRisk(List<Library> libraries) {
        List<CountOccurrences> risks = new ArrayList<>(8);

        licenseRiskRepository
            .findAll()
            .forEach(risk -> {
                long riskCount = libraries
                    .stream()
                    .filter(library -> library.getLicenseRisk(library.getLicenseToPublishes()).getId().equals(risk.getId()))
                    .count();
                risks.add(new CountOccurrences(risk.getName(), riskCount));
            });

        return risks;
    }

    /**
     * Count the number of licenses of a product.
     *
     * @param product product entity
     * @return a map of license names (shortIdentifier) and number of licenses.
     */
    public List<CountOccurrences> getProductLicenses(Product product) {
        return libraryPerProductService.countDistributedLicensesByProductId(product.getId());
    }

    /**
     * Create an archive from the libraries in a product. Include all or only the necessary libraries to the archive.
     * This archive can be downloaded or directly transferred to target that is specified in the application config.
     *
     * @param product  product for which the archive should be created
     * @param format   an {@link ArchiveFormat} (FULL or DELIVERY)
     * @param shipment the {@link ShipmentMethod} of the archive (DOWNLOAD or EXPORT)
     * @return Map of the archive file and status (archive in/complete)
     * @throws StorageException          if a problem by writing to the local system occurs
     * @throws RemoteRepositoryException if the remote repository is not accessible
     * @throws ZIPException              if the ZIP archive cannot be created
     * @throws UploadException           if the ZIP archive could not be made downloadable or the upload to the remote repository fails
     * @throws FileNotFoundException     if the local storage folder cannot be found
     */
    public Map<File, Boolean> createArchive(Product product, ArchiveFormat format, ShipmentMethod shipment)
        throws StorageException, RemoteRepositoryException, ZIPException, UploadException, FileNotFoundException {
        //Starts the creation of the archive
        archiveLog.info("Creating new archive for {} - {}.", product.getName(), product.getVersion());

        //Initializes the Map to be returned
        //complete indicates if the source code was found for all libraries
        var complete = true;
        Map<File, Boolean> returnMap = new HashMap<>(1);

        //Loads the settings
        //application.properties are ultimately retrieved from the docker-compose file
        String remoteIndexFilePath = applicationProperties.getSourceCodeArchive().getRemoteIndex();
        String[] remoteIndexPaths = remoteIndexFilePath.split("/");
        String remoteIndexFileName = remoteIndexPaths[(remoteIndexPaths.length) - 1];
        String remoteIndexPath = remoteIndexFilePath.replaceAll(remoteIndexFileName + "$", "");
        String remoteArchivePath = applicationProperties.getSourceCodeArchive().getRemoteArchive();
        String uploadPlatformURL = applicationProperties.getSourceCodeArchive().getUploadPlatform();
        String uploadUser = applicationProperties.getSourceCodeArchive().getUploadUser();
        String uploadPassword = applicationProperties.getSourceCodeArchive().getUploadPassword();

        //If no platform for the export is given, but export is requested, an UploadException is thrown
        if (shipment.equals(ShipmentMethod.EXPORT) && StringUtils.isBlank(uploadPlatformURL)) {
            archiveLog.error("External platform for upload not defined.");
            throw new UploadException("Platform for upload not defined.");
        }

        //Parses the archive's name based on the requested format
        String archiveName;
        if (format.equals(ArchiveFormat.FULL)) {
            archiveName = product.getIdentifier() + "_" + product.getVersion() + "_full-3rd-party-library-archive";
        } else {
            archiveName = product.getIdentifier() + "_" + product.getVersion() + "_3rd-party-library-archive-for-delivery";
        }

        //Sets the local paths and files
        var localStorageDirectory = ResourceUtils.getFile(SourceCodeHelper.LOCAL_STORAGE_URI);
        var localStoragePath = localStorageDirectory.getPath() + Constants.FILE_SEPARATOR;
        var localArchiveDirectory = new java.io.File(localStoragePath + archiveName);
        var localArchivePath = localArchiveDirectory.getPath() + Constants.FILE_SEPARATOR;
        var localIndexFile = new java.io.File(localStoragePath + Constants.INDEX);

        //Prepares the local file system, throws a StorageException if an error occurs
        //(Re)Creates the local archive directory
        try {
            FileUtils.deleteDirectory(localArchiveDirectory);
            FileUtils.forceMkdir(localArchiveDirectory);
        } catch (IOException e) {
            archiveLog.error("Could not create new archive {} in local storage {} : {}", archiveName, localStoragePath, e.getMessage());
            throw new StorageException("Could not access local filesystem.");
        }
        //Deletes local, outdated index.csv file if existing
        try {
            FileUtils.forceDelete(localIndexFile);
        } catch (FileNotFoundException e) {
            archiveLog.info("Index.csv is not existing in local storage {}.", localStoragePath);
        } catch (IOException e) {
            archiveLog.error("Could not delete index.csv in local storage {} : {}", localStoragePath, e.getMessage());
            throw new StorageException("Could not access local filesystem.");
        }

        //Downloads the up-to-date index.csv file of the remote 3rd-party repository, throws a RemoteRepositoryException if the file is not available
        try {
            HttpHelper.downloadResource(remoteIndexFilePath, localIndexFile);
        } catch (IOException e) {
            archiveLog.error("Could not download index.csv file from {} : {}", remoteIndexFilePath, e.getMessage());
            throw new RemoteRepositoryException("Could not access the remote repository.");
        }

        // Retrieve all libraries for the specified productId without hidden libraries
        List<Library> libraries = libraryPerProductService
            .findLibraryPerProductsByProductId(product.getId())
            .stream()
            .filter(LibraryPerProduct::isHidden)
            .map(LibraryPerProduct::getLibrary)
            .collect(Collectors.toList());

        //Iterates through all the libraries of the product
        for (Library library : libraries) {
            //Full identifier of the library in 3rd-party repository (with file extension)
            String identifier;
            //Short identifier of the library in the internal 3rd-party repository (without file extension)
            String label = SourceCodeHelper.createLabel(library);

            //For the delivery archive, checks if the library must be included
            //If not, skips the library
            if (format.equals(ArchiveFormat.DELIVERY) && !SourceCodeHelper.isCodeSharingRequired(library)) {
                archiveLog.info("Library {} does not need to be contained in delivery archive.", label);
                continue;
            }

            //Parses the full identifier from the index.csv file via the label
            try {
                identifier = SourceCodeHelper.checkRepository(localIndexFile, label);

                if (identifier == null) {
                    if (library.sourceCodeUrlIsValid()) {
                        archiveLog.info(
                            "Library {} is not available in the 3rd-party repository. Trying to download the source code URL from the library.",
                            label
                        );
                        SourceCodeHelper.updateRepository(label, library, localIndexFile, localArchivePath, remoteArchivePath);
                    } else {
                        identifier = SourceCodeHelper.checkRepositoryWithFuzzySearch(localIndexFile, label);

                        if (identifier != null) {
                            archiveLog.info("The source code archive was found using the fuzzy search : {}", identifier);
                            library.addErrorLog(
                                "Source Code",
                                "The source code archive was found using the fuzzy search." +
                                    " This may not be the right archive and should be verified : " +
                                    identifier,
                                LogSeverity.MEDIUM
                            );

                            var libraryPackage = new java.io.File(localArchivePath + identifier);
                            HttpHelper.downloadResource(remoteArchivePath + identifier, libraryPackage);
                        }
                    }
                } else {
                    archiveLog.info("Library {} is available in the 3rd-party repository and will be downloaded from there.", label);
                    var libraryPackage = new java.io.File(localArchivePath + identifier);
                    HttpHelper.downloadResource(remoteArchivePath + identifier, libraryPackage);
                }
            } catch (IOException e) {
                complete = false;
                archiveLog.error("Could not download library {} : {}", label, e.getMessage());
            }
        }

        //Uploads the local, updated index.csv file to the remote repository, throws a RemoteRepositoryException if the upload fails
        //remoteIndexPath
        //remoteIndexFile
        try {
            HttpHelper.transferToTarget(remoteIndexPath, Files.readAllBytes(localIndexFile.toPath()), remoteIndexFileName);
        } catch (IOException e) {
            archiveLog.error("Could not upload index.csv file to {} : {}", remoteIndexFilePath, e.getMessage());
            throw new RemoteRepositoryException("Could not access the remote repository.");
        }

        //Zips the archive folder, throws a ZIPException if the zipping fails
        java.io.File zipFile;
        try {
            zipFile = SourceCodeHelper.createZip(localArchiveDirectory);
        } catch (IOException e) {
            archiveLog.error("Could not create zip file {}.zip in directory {} : {}", archiveName, localStoragePath, e.getMessage());
            throw new ZIPException("Could not create the archive.");
        }

        //TODO: The option to download in-browser might not work for big files, activate and rework this
        //long threshold = Runtime.getRuntime().maxMemory() - 1*1000000000;
        //if (zipFile.length() > threshold) {
        //    log.debug("Threshold exceeded.");
        //}

        //Uploads the archive file to the remote repository or enables in-browser-download
        if (shipment.equals(ShipmentMethod.EXPORT)) {
            //Uploads the zip file, throws an UploadException if it fails
            //Adds an empty file to the Return-Map
            try {
                HttpHelper.transferZipToTargetWithCredentials(uploadPlatformURL, uploadUser, uploadPassword, zipFile);
                returnMap.put(new File(archiveName + ".zip", new byte[0], "N/A"), complete);
            } catch (IOException e) {
                archiveLog.error("Could not upload zip file {}.zip to platform {} : {}", archiveName, uploadPlatformURL, e.getMessage());
                throw new UploadException("Could not access the upload platform.");
            }
        } else {
            //Adds the file to the Return-Map, throws an UploadException if it fails
            byte[] content;
            try {
                content = Files.readAllBytes(zipFile.toPath());
                returnMap.put(new File(archiveName + ".zip", content, "application/zip"), complete);
            } catch (IOException e) {
                archiveLog.error("Could not make zip file {}.zip downloadable: {}", archiveName, e.getMessage());
                throw new UploadException("Could not prepare the archive for download.");
            }
        }

        //Returns the Map
        archiveLog.info("Archive successfully created.");
        return returnMap;
    }

    public ProductOverview createProductOverview() {
        return new ProductOverview();
    }

    /**
     * Create the product version based on another product. Copies all fields except the old version and the libraries
     * list but the manually added libraries can be copied.
     *
     * @param id        ID of the product
     * @param version   Version of the new product
     * @param delivered Set the delivered filed of the base product to true or not
     * @param copying   Copy manually added libraries to the new product or not
     * @return The new product
     */
    public Product createNextVersion(final Long id, String version, boolean delivered, boolean copying) {
        // Detach base product and change attributes
        Product product = entityManager.find(Product.class, id);
        product.setDelivered(delivered);
        product.setDeliveredDate(Instant.now());
        product = entityManager.merge(product);

        entityManager.flush();
        entityManager.detach(product);

        product.setId(null);
        product.setVersion(version);
        product.setPreviousProductId(id);
        product.setDelivered(false);
        product.setDeliveredDate(null);
        product.setLastUpdatedDate(null);
        product.setCreatedDate(LocalDate.now());

        product = entityManager.merge(product);

        // Copy manually added libraries to new product
        if (copying) {
            Product lppProduct = product;
            libraryPerProductService
                .findLibraryPerProductsByProductIdAndAddedManuallyIsTrue(id)
                .forEach(libraryPerProduct -> {
                    LibraryPerProduct lpp = new LibraryPerProduct()
                        .product(lppProduct)
                        .library(libraryPerProduct.getLibrary())
                        .addedManually(libraryPerProduct.getAddedManually());
                    try {
                        libraryPerProductService.saveWithCheck(lpp);
                    } catch (LibraryException e) {
                        log.debug("Library [ {} ] is already in Product [ {} ]", lpp.getLibrary().getId(), lpp.getProduct().getId());
                    }
                });
        }

        return product;
    }

    /**
     * Processing of an SBOM for a project. Based on the SBOM format the AssetManager recognizes which loader has to be
     * selected. The loaders are registered individually in the constructor. If the SBOM contains duplicates, then these
     * are added only once. Likewise, the components, which are already contained in the project (delete=false) are
     * checked, so that no renewed adding takes place.
     *
     * @param product Product entity.
     * @param upload  {@link Upload} object with SBOM.
     * @param delete  True if already existing components should be deleted from the project, otherwise components from
     *                the upload will be added to the previous list.
     * @throws UploadException If the project cannot be found or errors occur when processing the Upload.
     */
    @Async
    @Transactional
    public void processUpload(Product product, Upload upload, boolean delete) throws UploadException {
        log.info("Start processing upload for Product : {}", product.getId());

        try {
            Set<Library> libraries = assetManager.load(upload.getFile(), upload.getFile().getFileContentType());

            if (libraries != null) {
                if (delete) {
                    libraryPerProductService.deleteByProductAndNotAddedManually(product.getId());
                }

                Stream<Library> libraryStream = libraries.stream();

                if (!StringUtils.isBlank(product.getUploadFilter())) {
                    for (String line : product.getUploadFilter().split("\n")) {
                        if (!StringUtils.isBlank(line)) {
                            libraryStream =
                                libraryStream.filter(library ->
                                    !(Pattern.matches(line, library.getGroupId()) || Pattern.matches(line, library.getArtifactId()))
                                );
                        }
                    }
                }

                AtomicInteger libraryCounter = new AtomicInteger(0);

                libraryStream.forEach(library -> {
                    log.info(
                        "[{}] Processing library for product {} : {} - {} - {}",
                        libraryCounter.get(),
                        product.getId(),
                        library.getGroupId(),
                        library.getArtifactId(),
                        library.getVersion()
                    );
                    libraryCounter.incrementAndGet();

                    try {
                        //library.setLicenseUrl("");
                        //library.setSourceCodeUrl("");

                        if (library.getLastReviewedBy() != null) {
                            Optional<User> optionalUser = userRepository.findOneByLogin(library.getLastReviewedBy().getLogin());
                            optionalUser.ifPresent(library::setLastReviewedBy);
                        }

                        // TODO: linkedLicenses field

                        /*Optional<String> licenseToPublish = library.getLicenseToPublishes().stream().map(License::getFullName).findFirst();
                        if (licenseToPublish.isPresent()) {
                            library.setLicenseToPublishes(licenseService.findShortIdentifier(licenseToPublish.get()));
                        }

                        Optional<String> licenseOfFiles = library.getLicenseOfFiles().stream().map(License::getFullName).findFirst();
                        if (licenseOfFiles.isPresent()) {
                            library.setLicenseOfFiles(licenseService.findShortIdentifier(licenseOfFiles.get()));
                        }*/

                        library = libraryService.saveWithCheck(library);
                    } catch (LibraryException e) {
                        Library dbLibrary = e.getLibrary();
                        dbLibrary.updateEmptyFields(library);

                        library = new Pipeline<>(new MavenLicenseStep()).pipe(new NpmLicenseStep()).execute(library);
                        libraryService.licenseAutocomplete(dbLibrary);
                        //libraryService.hasIncompatibleLicenses(library);
                        libraryService.removeGenericLicenseUrl(dbLibrary);
                        // library.setLicenseUrl("");
                        // library.setSourceCodeUrl("");
                        libraryService.urlAutocomplete(dbLibrary);
                        libraryService.licenseTextAutocomplete(library);
                        libraryService.copyrightAutocomplete(dbLibrary);
                        libraryService.calculateLibraryRisk(library);
                        library = libraryService.save(dbLibrary);
                    }

                    LibraryPerProduct libraryPerProduct = new LibraryPerProduct();
                    libraryPerProduct.setProduct(product);
                    libraryPerProduct.setLibrary(library);

                    try {
                        libraryPerProductService.saveWithCheck(libraryPerProduct);
                    } catch (LibraryException e) {
                        log.debug("Library [ {} ] is already in Product [ {} ]", library.getId(), product.getId());
                    }
                });

                if (upload.getAdditionalLibraries() != null && upload.getAdditionalLibraries().getFile() != null) {
                    AdditionalLibrariesLoader additionalLibrariesLoader = new AdditionalLibrariesLoader();
                    Set<Library> additionalLibraries = additionalLibrariesLoader.load(upload.getAdditionalLibraries());

                    for (Library library : additionalLibraries) {
                        log.info(
                            "Processing additional library : {} - {} - {}",
                            library.getGroupId(),
                            library.getArtifactId(),
                            library.getVersion()
                        );

                        try {
                            library = libraryService.saveWithCheck(library);
                        } catch (LibraryException e) {
                            library = e.getLibrary();
                        }

                        LibraryPerProduct libraryPerProduct = new LibraryPerProduct();
                        libraryPerProduct.setProduct(product);
                        libraryPerProduct.setLibrary(library);
                        libraryPerProduct.setAddedManually(true);

                        try {
                            libraryPerProductService.saveWithCheck(libraryPerProduct);
                        } catch (LibraryException e) {
                            log.debug("Library [ {} ] is already in Product [ {} ]", library.getId(), product.getId());
                        }
                    }
                }

                product.setUploadState(UploadState.SUCCESSFUL);
                product.setLastUpdatedDate(LocalDate.now());
                saveWithCheck(product);
                transferBundleToTarget(product);
            }
            log.info("Finished processing upload!");
        } catch (Exception e) { // Catch alle Exceptions
            product.setUploadState(UploadState.FAILURE);
            save(product);

            throw new UploadException(e.getMessage());
        }
    }

    /**
     * Check if an upload by URL is valid.
     *
     * @param product Product entity
     * @param url     URL to a file
     * @return Content type of the file
     * @throws UploadException if the file doesn't exist, can't be downloaded or exceeds the upload limit size.
     */
    public String preCheckForUploadByUrl(Product product, String url, BasicAuthentication credentials) throws UploadException {
        log.info("Start processing upload by URL for Product : {}", product.getId());

        int uploadLimit = applicationProperties.getUpload().getLimit();

        String contentType;
        try {
            HttpResponse<String> response;
            if (StringUtils.isBlank(credentials.getUsername()) || StringUtils.isBlank(credentials.getPassword())) {
                response = HttpHelper.httpHeadRequest(url);
            } else {
                response = HttpHelper.httpHeadRequestWithAuthorization(url, credentials.getUsername(), credentials.getPassword());
            }

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                log.error("Response code is not 200. Wrong url, credentials or another access problem.");
                throw new UploadException("File cannot be accessed.");
            }

            Optional<String> optionalContentLength = response.headers().firstValue("Content-Length");
            if (optionalContentLength.isPresent()) {
                String contentLengthResponse = optionalContentLength.get();
                long contentLength = Long.parseLong(contentLengthResponse);
                contentLength = contentLength / 1000 / 1000;

                if (contentLength > uploadLimit) {
                    log.error("File size is too large. Maximum upload limit: {} MB", uploadLimit);
                    throw new UploadException("File size is too large. Maximum upload limit: " + uploadLimit + " MB");
                }
            }

            Optional<String> optionalContentType = response.headers().firstValue("Content-Type");
            if (optionalContentType.isPresent()) {
                contentType = optionalContentType.get();
            } else {
                throw new UploadException(
                    "Undefined content type. For the subsequent processing the 'Content-Type' HTTP header is necessary."
                );
            }
        } catch (NumberFormatException e) {
            throw new UploadException("Incompatible upload");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            product.setUploadState(UploadState.FAILURE);
            save(product);
            throw new UploadException("Cannot download file : " + e.getMessage());
        }

        return contentType;
    }

    @Async
    public void processUploadByUrl(Product product, String url, BasicAuthentication credentials, boolean delete, String contentType)
        throws UploadException {
        log.info("Start processing upload by URL for Product : {}", product.getId());
        try (
            InputStream downloadStream = (
                StringUtils.isBlank(credentials.getUsername()) || StringUtils.isBlank(credentials.getPassword())
                    ? HttpHelper.downloadResource(url)
                    : HttpHelper.downloadResourceWithAuthentication(url, credentials.getUsername(), credentials.getPassword())
            )
        ) {
            File file = new File("Upload", downloadStream, contentType);
            Upload upload = new Upload();
            upload.setFile(file);

            // Will not be asynchronously executed
            // Async method call in same class doesn't work
            processUpload(product, upload, delete);

            upload.getFile().getFilestream().close();
        } catch (IOException e) {
            product.setUploadState(UploadState.FAILURE);
            save(product);
            throw new UploadException("Cannot download file : " + e.getMessage());
        } finally {
            System.gc();
        }
    }

    /**
     * Add {@link Library libraries} to a product. These libraries will be marked as "manually added" in the product.
     *
     * @param product   The product to add the library list
     * @param libraries A list of {@link Library libraries}
     */
    public void addLibraries(Product product, List<Library> libraries) {
        for (Library library : libraries) {
            LibraryPerProduct libraryPerProduct = new LibraryPerProduct().product(product).library(library).addedManually(true);
            try {
                libraryPerProductService.saveWithCheck(libraryPerProduct);
            } catch (LibraryException e) {
                log.debug("Library [ {} ] is already in Product [ {} ]", library.getId(), product.getId());
            }
        }
    }

    /**
     * Search in a product line the product that is in status "In Development".
     * The identifier is used from the passed product to execute the search. If multiple products or none
     * are "In Development" then a {@link ProductException} will be thrown.
     *
     * @param product The product with which to search for the product that is "In Development" from the product line
     * @return The product which is in status "In Development"
     * @throws ProductException if no unique product could be found.
     */
    public Product getInDevelopmentProduct(Product product) throws ProductException {
        Optional<Product> optionalProduct = productRepository.findByIdAndDeliveredIsFalse(product.getIdentifier());

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new ProductException("No unique product which is \"In Development\" could be found");
        }
    }

    public DifferenceView compare(Product firstProduct, Product secondProduct) {
        List<Library> firstProductWithoutDuplicates = libraryPerProductService.onlyLibrariesFromFirstProductWithoutIntersection(
            firstProduct.getId(),
            secondProduct.getId()
        );

        List<Library> secondProductWithoutDuplicates = libraryPerProductService.onlyLibrariesFromFirstProductWithoutIntersection(
            secondProduct.getId(),
            firstProduct.getId()
        );

        List<Library> firstProductLibraries = libraryPerProductService.findLibrariesByProductId(firstProduct.getId());
        List<Library> secondProductLibraries = libraryPerProductService.findLibrariesByProductId(secondProduct.getId());

        List<Library> firstProductNewLibraries = new ArrayList<>(16);

        for (Library libraryFromFirstProductWithoutDuplicates : firstProductWithoutDuplicates) {
            boolean notUnique = false;

            for (Library libraryFromSecondProduct : secondProductLibraries) {
                if (DifferenceView.isEqualLibraryNameAndType(libraryFromFirstProductWithoutDuplicates, libraryFromSecondProduct)) {
                    notUnique = true;
                }
            }

            if (!notUnique) {
                firstProductNewLibraries.add(libraryFromFirstProductWithoutDuplicates);
            }
        }

        List<Library> secondProductNewLibraries = new ArrayList<>(16);

        for (Library libraryFromSecondProductWithoutDuplicates : secondProductWithoutDuplicates) {
            boolean notUnique = false;

            for (Library libraryFromFirstProduct : firstProductLibraries) {
                if (DifferenceView.isEqualLibraryNameAndType(libraryFromSecondProductWithoutDuplicates, libraryFromFirstProduct)) {
                    notUnique = true;
                }
            }

            if (!notUnique) {
                secondProductNewLibraries.add(libraryFromSecondProductWithoutDuplicates);
            }
        }

        return new DifferenceView(
            libraryPerProductService.libraryIntersectionOfProducts(firstProduct.getId(), secondProduct.getId()),
            firstProductWithoutDuplicates,
            secondProductWithoutDuplicates,
            firstProductNewLibraries,
            secondProductNewLibraries
        );
    }
}
