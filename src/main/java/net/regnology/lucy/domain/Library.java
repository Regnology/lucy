package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.domain.enumeration.LogSeverity;
import net.regnology.lucy.domain.enumeration.LogStatus;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Library.
 */
@Entity
@Table(name = "library")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Library implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(Library.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "group_id")
    @Convert(converter = EmptyStringToNullConverter.class)
    private String groupId = "";

    @NotNull
    @Column(name = "artifact_id", nullable = false)
    private String artifactId;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LibraryType type = LibraryType.UNKNOWN;

    @Size(max = 2048)
    @Column(name = "original_license", length = 2048)
    private String originalLicense = "";

    @Size(max = 2048)
    @Column(name = "license_url", length = 2048)
    private String licenseUrl;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "license_text")
    private String licenseText;

    @Size(max = 2048)
    @Column(name = "source_code_url", length = 2048)
    private String sourceCodeUrl;

    @Size(max = 2048)
    @Column(name = "p_url", length = 2048)
    private String pUrl;

    @Size(max = 16384)
    @Column(name = "copyright", length = 16384)
    private String copyright;

    @Column(name = "compliance")
    private String compliance;

    @Size(max = 4096)
    @Column(name = "compliance_comment", length = 4096)
    private String complianceComment;

    @Size(max = 4096)
    @Column(name = "comment", length = 4096)
    private String comment;

    @Column(name = "reviewed")
    private Boolean reviewed = false;

    @Column(name = "reviewed_deep_scan")
    private Boolean reviewedDeepScan = false;

    @Column(name = "last_reviewed_date")
    private LocalDate lastReviewedDate;

    @Column(name = "last_reviewed_deep_scan_date")
    private LocalDate lastReviewedDeepScanDate;

    @Column(name = "created_date")
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "hide_for_publishing")
    private Boolean hideForPublishing = false;

    @Size(max = 32)
    @Column(name = "md5", length = 32)
    private String md5;

    @Size(max = 40)
    @Column(name = "sha1", length = 40)
    private String sha1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private Fossology fossology;

    @OneToMany(mappedBy = "library", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "library" }, allowSetters = true)
    private Set<LibraryErrorLog> errorLogs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "last_reviewed_by_id", nullable = true)
    private User lastReviewedBy;

    @ManyToOne
    @JoinColumn(name = "last_reviewed_deep_scan_by_id", nullable = true)
    private User lastReviewedDeepScanBy;

    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "library", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderId")
    @JsonIgnoreProperties(value = { "library" }, allowSetters = true)
    private SortedSet<LicensePerLibrary> licenses = new TreeSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_library__license_to_publish",
        joinColumns = @JoinColumn(name = "library_id"),
        inverseJoinColumns = @JoinColumn(name = "license_to_publish_id")
    )
    @JsonIgnoreProperties(
        value = { "lastReviewedBy", "linkedLibraries", "libraries", "libraryPublishes", "libraryFiles", "genericLicenseText", "other" },
        allowSetters = true
    )
    private Set<License> licenseToPublishes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_library__license_of_files",
        joinColumns = @JoinColumn(name = "library_id"),
        inverseJoinColumns = @JoinColumn(name = "license_of_files_id")
    )
    @JsonIgnoreProperties(
        value = { "lastReviewedBy", "linkedLibraries", "libraries", "libraryPublishes", "libraryFiles", "genericLicenseText", "other" },
        allowSetters = true
    )
    private Set<License> licenseOfFiles = new HashSet<>();

    @ManyToOne
    private LicenseRisk libraryRisk;

    /* Methods */

    public void updateEmptyFields(Library library) {
        if (Boolean.FALSE.equals(getReviewed())) {
            if (getType() == null || getType().equals(LibraryType.UNKNOWN)) setType(library.getType());
            if (StringUtils.isBlank(getOriginalLicense())) setOriginalLicense(library.getOriginalLicense());
            if (StringUtils.isBlank(getLicenseUrl())) setLicenseUrl(library.getLicenseUrl());
            if (StringUtils.isBlank(getLicenseText())) setLicenseText(library.getLicenseText());
            if (StringUtils.isBlank(getSourceCodeUrl())) setSourceCodeUrl(library.getSourceCodeUrl());
            if (StringUtils.isBlank(getpUrl())) setpUrl(library.getpUrl());
            if (StringUtils.isBlank(getCopyright())) setCopyright(library.getCopyright());
            if (StringUtils.isBlank(getCompliance())) setCompliance(library.getCompliance());
            if (StringUtils.isBlank(getComplianceComment())) setComplianceComment(library.getComplianceComment());
            if (StringUtils.isBlank(getComment())) setComment(library.getComment());
            //if (getLicenses().isEmpty()) setLicenses(library.getLicenses());
            if (getLicenseToPublishes().isEmpty()) setLicenseToPublishes(library.getLicenseToPublishes());
            if (getLicenseOfFiles().isEmpty()) setLicenseOfFiles(library.getLicenseOfFiles());
            if (Boolean.FALSE.equals(getHideForPublishing())) setHideForPublishing(library.getHideForPublishing());
            setReviewed(library.getReviewed());
            if (Boolean.FALSE.equals(getReviewedDeepScan())) setReviewedDeepScan(library.getReviewedDeepScan());
            if (getLastReviewedDate() == null) setLastReviewedDate(library.getLastReviewedDate());
            if (getLastReviewedDeepScanDate() == null) setLastReviewedDeepScanDate(library.getLastReviewedDeepScanDate());
            if (getLastReviewedBy() == null) setLastReviewedBy(library.getLastReviewedBy());
            if (getLastReviewedDeepScanBy() == null) setLastReviewedDeepScanBy(library.getLastReviewedDeepScanBy());
            if (getMd5() == null) setMd5(library.getMd5());
            if (getSha1() == null) setSha1(library.getSha1());
        }
    }

    /**
     * Extracts the LibraryType of a Package URL
     * @param pUrl Package URL
     * @return A LibraryType if this LibraryType exist, otherwise UNKNOWN
     */
    public LibraryType extractTypeFromPUrl(String pUrl) {
        String typeFromPurl = pUrl.replace("pkg:", "").split("/")[0];
        for (LibraryType libraryType : LibraryType.values()) {
            if (typeFromPurl.equals(libraryType.getValue())) {
                return LibraryType.getLibraryTypeByValue(typeFromPurl);
            }
        }
        return LibraryType.UNKNOWN;
    }

    /**
     * Get the {@link LicenseRisk} from a Set of License entities.
     * If the Set contains an Unknown risk then it will be returned.
     *
     * @return a LicenseRisk entity with the highest risk level or the Unknown risk
     */
    public LicenseRisk getLicenseRisk(Set<License> licenses) {
        LicenseRisk highestLicenseRisk = null;

        for (License license : licenses) {
            if (highestLicenseRisk == null) {
                highestLicenseRisk = license.getLicenseRisk();
            } else {
                // If license risk is Unknown then return it always
                if (license.getLicenseRisk() != null && license.getLicenseRisk().getName().equals(Constants.UNKNOWN)) {
                    return license.getLicenseRisk();
                }

                if (
                    license.getLicenseRisk() != null && highestLicenseRisk.getLevel() < license.getLicenseRisk().getLevel()
                ) /* If new license risk is higher than last then set new risk */highestLicenseRisk = license.getLicenseRisk();
            }
        }

        return highestLicenseRisk;
    }

    public boolean validateLinkedLicenses() {
        boolean valid = true;
        int size = licenses.size();

        int counter = 0;

        for (LicensePerLibrary linkedLicense : licenses) {
            if (linkedLicense.getLicense() == null) {
                log.info("License validation failed for Library {} - Linked licenses contains at least one \"null\" license", getId());
                valid = false;
                break;
            }

            if (counter < size - 1 && linkedLicense.getLinkType() == null) {
                log.info("License validation failed for Library {} - Link between two licenses is \"null\"", getId());
                valid = false;
                break;
            }

            if (counter == size - 1 && linkedLicense.getLinkType() != null) {
                log.info(
                    "License validation failed for Library {} - The last license contains a link to another license, but no license follows",
                    getId()
                );
                valid = false;
                break;
            }
            counter++;
        }

        return valid;
    }

    /**
     * Build a String from the Set of all linked licenses.
     *
     * @return the linked licenses as a String
     */
    public String printLinkedLicenses() {
        StringBuilder license = new StringBuilder(32);
        for (LicensePerLibrary linkedLicense : licenses) {
            if (linkedLicense.getLicense() != null) {
                license.append(linkedLicense.getLicense().getShortIdentifier());
                if (linkedLicense.getLinkType() != null) license.append(" ").append(linkedLicense.getLinkType().getValue()).append(" ");
            }
        }
        return license.toString();
    }

    /**
     * Checks if the given SourceCodeURL is valid. That means the URL is not null, empty or {@link Constants#NO_URL}.
     * @return true, if the source code URL is valid, otherwise false.
     */
    public boolean sourceCodeUrlIsValid() {
        return !StringUtils.isBlank(sourceCodeUrl) && !sourceCodeUrl.equals(Constants.NO_URL);
    }

    /**
     * Check if an error log message is already saved in a library.
     * @return true if the error message is in the library or false if not.
     */
    public boolean containsErrorLogByMessage(String message) {
        for (LibraryErrorLog errorLog : errorLogs) {
            if (errorLog.getMessage().equals(message)) {
                return true;
            }
        }
        return false;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Library id(Long id) {
        this.id = id;
        return this;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public Library groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId.equals("-") ? "" : groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public Library artifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return this.version;
    }

    public Library version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version.startsWith("V") || version.startsWith("v") ? version.substring(1) : version;
    }

    public LibraryType getType() {
        return this.type;
    }

    public Library type(LibraryType type) {
        this.type = type;
        return this;
    }

    public void setType(LibraryType type) {
        this.type = type;
    }

    public String getOriginalLicense() {
        return this.originalLicense;
    }

    public Library originalLicense(String originalLicense) {
        this.originalLicense = originalLicense;
        return this;
    }

    public void setOriginalLicense(String originalLicense) {
        if (originalLicense != null) this.originalLicense = originalLicense.equals("-") ? "" : originalLicense;
    }

    public String getLicenseUrl() {
        return this.licenseUrl;
    }

    public Library licenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
        return this;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getLicenseText() {
        return this.licenseText;
    }

    public Library licenseText(String licenseText) {
        this.licenseText = licenseText;
        return this;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }

    public String getSourceCodeUrl() {
        return this.sourceCodeUrl;
    }

    public Library sourceCodeUrl(String sourceCodeUrl) {
        this.sourceCodeUrl = sourceCodeUrl;
        return this;
    }

    public void setSourceCodeUrl(String sourceCodeUrl) {
        this.sourceCodeUrl = sourceCodeUrl;
    }

    public String getpUrl() {
        return this.pUrl;
    }

    public Library pUrl(String pUrl) {
        this.pUrl = pUrl;
        return this;
    }

    public void setpUrl(String pUrl) {
        this.pUrl = pUrl;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public Library copyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public void setCopyright(String copyright) {
        if (StringUtils.length(copyright) > 16384) {
            copyright = StringUtils.abbreviate(copyright, 16384);

            addErrorLog(
                "Copyright",
                "The result of the copyright analysis is longer than 16384 characters " +
                "and has been shortened for that reason. It needs to be checked",
                LogSeverity.MEDIUM
            );
        }

        this.copyright = copyright;
    }

    public String getCompliance() {
        return this.compliance;
    }

    public Library compliance(String compliance) {
        this.compliance = compliance;
        return this;
    }

    public void setCompliance(String compliance) {
        this.compliance = compliance;
    }

    public String getComplianceComment() {
        return this.complianceComment;
    }

    public Library complianceComment(String complianceComment) {
        this.complianceComment = complianceComment;
        return this;
    }

    public void setComplianceComment(String complianceComment) {
        this.complianceComment = complianceComment;
    }

    public String getComment() {
        return this.comment;
    }

    public Library comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getReviewed() {
        return this.reviewed;
    }

    public Library reviewed(Boolean reviewed) {
        this.reviewed = reviewed;
        return this;
    }

    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }

    public Boolean getReviewedDeepScan() {
        return this.reviewedDeepScan;
    }

    public Library reviewedDeepScan(Boolean reviewedDeepScan) {
        this.reviewedDeepScan = reviewedDeepScan;
        return this;
    }

    public void setReviewedDeepScan(Boolean reviewedDeepScan) {
        this.reviewedDeepScan = reviewedDeepScan;
    }

    public LocalDate getLastReviewedDate() {
        return this.lastReviewedDate;
    }

    public Library lastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
        return this;
    }

    public void setLastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
    }

    public LocalDate getLastReviewedDeepScanDate() {
        return this.lastReviewedDeepScanDate;
    }

    public Library lastReviewedDeepScanDate(LocalDate lastReviewedDeepScanDate) {
        this.lastReviewedDeepScanDate = lastReviewedDeepScanDate;
        return this;
    }

    public void setLastReviewedDeepScanDate(LocalDate lastReviewedDeepScanDate) {
        this.lastReviewedDeepScanDate = lastReviewedDeepScanDate;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public Library createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getHideForPublishing() {
        return this.hideForPublishing;
    }

    public Library hideForPublishing(Boolean hideForPublishing) {
        this.hideForPublishing = hideForPublishing;
        return this;
    }

    public String getMd5() {
        return this.md5;
    }

    public Library md5(String md5) {
        this.md5 = md5;
        return this;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return this.sha1;
    }

    public Library sha1(String sha1) {
        this.sha1 = sha1;
        return this;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public Fossology getFossology() {
        return this.fossology;
    }

    public Library fossology(Fossology fossology) {
        this.fossology = fossology;
        return this;
    }

    public void setFossology(Fossology fossology) {
        this.fossology = fossology;
    }

    public Library addErrorLog(LibraryErrorLog errorLogs) {
        this.errorLogs.add(errorLogs);
        errorLogs.setLibrary(this);
        return this;
    }

    /**
     * Create and add an error log with a issue, message and severity.
     * From the issue and the message the final error message will be compound.<br>
     * Pattern: issue - message<br>
     * Every log will be created with with the status "OPEN".
     *
     * @param issue Name of the issue where the error occurred
     * @param message Description of the error
     * @param severity Severity of the error
     * @return a Library entity
     */
    public Library addErrorLog(String issue, String message, LogSeverity severity) {
        String errorMessage = issue + " - " + message;
        LibraryErrorLog log = new LibraryErrorLog(errorMessage, severity, LogStatus.OPEN);
        this.errorLogs.add(log);
        log.setLibrary(this);
        return this;
    }

    public Library removeErrorLog(LibraryErrorLog errorLogs) {
        this.errorLogs.remove(errorLogs);
        errorLogs.setLibrary(null);
        return this;
    }

    public void setErrorLogs(Set<LibraryErrorLog> errorLogs) {
        //this.errorLogs = errorLogs;

        if (this.errorLogs != null) {
            this.errorLogs.forEach(i -> i.setLibrary(null));
        }
        if (errorLogs != null) {
            errorLogs.forEach(i -> i.setLibrary(this));
        }
        this.getErrorLogs().clear();
        this.getErrorLogs().addAll(errorLogs != null ? errorLogs : new HashSet<>());
    }

    public Set<LibraryErrorLog> getErrorLogs() {
        return this.errorLogs;
    }

    public Library errorLogs(Set<LibraryErrorLog> errorLogs) {
        this.errorLogs = errorLogs;
        return this;
    }

    public void setHideForPublishing(Boolean hideForPublishing) {
        this.hideForPublishing = hideForPublishing;
    }

    public SortedSet<LicensePerLibrary> getLicenses() {
        return this.licenses;
    }

    public Library licenses(SortedSet<LicensePerLibrary> licensePerLibraries) {
        this.setLicenses(licensePerLibraries);
        return this;
    }

    public Library addLicenses(LicensePerLibrary licensePerLibrary) {
        this.licenses.add(licensePerLibrary);
        licensePerLibrary.setLibrary(this);
        return this;
    }

    public Library removeLicenses(LicensePerLibrary licensePerLibrary) {
        this.licenses.remove(licensePerLibrary);
        licensePerLibrary.setLibrary(null);
        return this;
    }

    public void setLicenses(SortedSet<LicensePerLibrary> licensePerLibraries) {
        if (this.licenses != null) {
            this.licenses.forEach(i -> i.setLibrary(null));
        }
        if (licensePerLibraries != null) {
            licensePerLibraries.forEach(i -> i.setLibrary(this));
        }
        this.getLicenses().clear();
        this.getLicenses().addAll(licensePerLibraries != null ? licensePerLibraries : new TreeSet<>());
        //this.linkedLicenses = licensePerLibraries;
    }

    public User getLastReviewedBy() {
        return this.lastReviewedBy;
    }

    public Library lastReviewedBy(User user) {
        this.setLastReviewedBy(user);
        return this;
    }

    public void setLastReviewedBy(User user) {
        this.lastReviewedBy = user;
    }

    public User getLastReviewedDeepScanBy() {
        return this.lastReviewedDeepScanBy;
    }

    public Library lastReviewedDeepScanBy(User user) {
        this.setLastReviewedDeepScanBy(user);
        return this;
    }

    public void setLastReviewedDeepScanBy(User user) {
        this.lastReviewedDeepScanBy = user;
    }

    public Set<License> getLicenseToPublishes() {
        return this.licenseToPublishes;
    }

    public Library licenseToPublishes(Set<License> licenses) {
        this.setLicenseToPublishes(licenses);
        return this;
    }

    public Library addLicenseToPublish(License license) {
        this.licenseToPublishes.add(license);
        license.getLibraryPublishes().add(this);
        return this;
    }

    public Library removeLicenseToPublish(License license) {
        this.licenseToPublishes.remove(license);
        license.getLibraryPublishes().remove(this);
        return this;
    }

    public void setLicenseToPublishes(Set<License> licenses) {
        this.licenseToPublishes = licenses;
    }

    public Set<License> getLicenseOfFiles() {
        return this.licenseOfFiles;
    }

    public Library licenseOfFiles(Set<License> licenses) {
        this.setLicenseOfFiles(licenses);
        return this;
    }

    public Library addLicenseOfFiles(License license) {
        this.licenseOfFiles.add(license);
        license.getLibraryFiles().add(this);
        return this;
    }

    public Library removeLicenseOfFiles(License license) {
        this.licenseOfFiles.remove(license);
        license.getLibraryFiles().remove(this);
        return this;
    }

    public void setLicenseOfFiles(Set<License> licenses) {
        this.licenseOfFiles = licenses;
    }

    public LicenseRisk getLibraryRisk() {
        return this.libraryRisk;
    }

    public Library libraryRisk(LicenseRisk libraryRisk) {
        this.setLibraryRisk(libraryRisk);
        return this;
    }

    public void setLibraryRisk(LicenseRisk libraryRisk) {
        this.libraryRisk = libraryRisk;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Library)) {
            return false;
        }
        return id != null && id.equals(((Library) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Library{" +
            "id=" + getId() +
            ", groupId='" + getGroupId() + "'" +
            ", artifactId='" + getArtifactId() + "'" +
            ", version='" + getVersion() + "'" +
            ", type='" + getType() + "'" +
            ", originalLicense='" + getOriginalLicense() + "'" +
            ", licenseUrl='" + getLicenseUrl() + "'" +
            ", licenseText='" + StringUtils.abbreviate(getLicenseText(), 14) + "'" +
            ", sourceCodeUrl='" + getSourceCodeUrl() + "'" +
            ", pUrl='" + getpUrl() + "'" +
            ", copyright='" + StringUtils.abbreviate(getCopyright(), 14) + "'" +
            ", compliance='" + getCompliance() + "'" +
            ", complianceComment='" + getComplianceComment() + "'" +
            ", comment='" + StringUtils.abbreviate(getComment(), 14) + "'" +
            ", reviewed='" + getReviewed() + "'" +
            ", reviewedDeepScan='" + getReviewedDeepScan() + "'" +
            ", lastReviewedDate='" + getLastReviewedDate() + "'" +
            ", lastReviewedDeepScanDate='" + getLastReviewedDeepScanDate() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", hideForPublishing='" + getHideForPublishing() + "'" +
            ", md5='" + getMd5() + "'" +
            ", sha1='" + getSha1() + "'" +
            "}";
    }
}
