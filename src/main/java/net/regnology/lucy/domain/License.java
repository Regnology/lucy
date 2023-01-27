package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A License.
 */
@Entity
@Table(name = "license")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class License implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "short_identifier", nullable = false)
    private String shortIdentifier;

    @Column(name = "spdx_identifier")
    private String spdxIdentifier;

    @Size(max = 2048)
    @Column(name = "url", length = 2048)
    private String url;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "generic_license_text")
    private String genericLicenseText;

    @Size(max = 8192)
    @Column(name = "other", length = 8192)
    private String other;

    @Column(name = "reviewed")
    private Boolean reviewed = false;

    @Column(name = "last_reviewed_date")
    private LocalDate lastReviewedDate;

    @OneToMany(mappedBy = "firstLicenseConflict", fetch = FetchType.LAZY)
    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "firstLicenseConflict" }, allowSetters = true)
    @OrderBy("secondLicenseConflict")
    private SortedSet<LicenseConflict> licenseConflicts = new TreeSet<>();

    @ManyToOne
    private User lastReviewedBy;

    @ManyToOne
    private LicenseRisk licenseRisk;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_license__requirement",
        joinColumns = @JoinColumn(name = "license_id"),
        inverseJoinColumns = @JoinColumn(name = "requirement_id")
    )
    @JsonIgnoreProperties(value = { "licenses" }, allowSetters = true)
    private Set<Requirement> requirements = new HashSet<>();

    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "license" }, allowSetters = true)
    private Set<LicensePerLibrary> linkedLibraries = new HashSet<>();

    @ManyToMany(mappedBy = "licenseToPublishes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "lastReviewedBy", "linkedLicenses", "licenseToPublishes", "licenseOfFiles" }, allowSetters = true)
    private Set<Library> libraryPublishes = new HashSet<>();

    @ManyToMany(mappedBy = "licenseOfFiles")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "lastReviewedBy", "linkedLicenses", "licenseToPublishes", "licenseOfFiles" }, allowSetters = true)
    private Set<Library> libraryFiles = new HashSet<>();

    /* Methods */

    public void updateEmptyFields(License license) {
        if (!getReviewed()) {
            if (StringUtils.isBlank(getSpdxIdentifier())) setSpdxIdentifier(license.getSpdxIdentifier());
            if (StringUtils.isBlank(getUrl())) setUrl(license.getUrl());
            if (StringUtils.isBlank(getGenericLicenseText())) setGenericLicenseText(license.getGenericLicenseText());
            if (StringUtils.isBlank(getOther())) setOther(license.getOther());
            setReviewed(license.getReviewed());
            if (getLastReviewedDate() == null) setLastReviewedDate(license.getLastReviewedDate());
            if (getLastReviewedBy() == null) setLastReviewedBy(license.getLastReviewedBy());
            if (getLicenseRisk() == null) setLicenseRisk(license.getLicenseRisk());
        }
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public License id(Long id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return this.fullName;
    }

    public License fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortIdentifier() {
        return this.shortIdentifier;
    }

    public License shortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
        return this;
    }

    public void setShortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
    }

    public String getSpdxIdentifier() {
        return this.spdxIdentifier;
    }

    public License spdxIdentifier(String spdxIdentifier) {
        this.spdxIdentifier = spdxIdentifier;
        return this;
    }

    public void setSpdxIdentifier(String spdxIdentifier) {
        this.spdxIdentifier = spdxIdentifier;
    }

    public String getUrl() {
        return this.url;
    }

    public License url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenericLicenseText() {
        return this.genericLicenseText;
    }

    public License genericLicenseText(String genericLicenseText) {
        this.genericLicenseText = genericLicenseText;
        return this;
    }

    public void setGenericLicenseText(String genericLicenseText) {
        this.genericLicenseText = genericLicenseText;
    }

    public String getOther() {
        return this.other;
    }

    public License other(String other) {
        this.other = other;
        return this;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Boolean getReviewed() {
        return this.reviewed;
    }

    public License reviewed(Boolean reviewed) {
        this.reviewed = reviewed;
        return this;
    }

    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }

    public LocalDate getLastReviewedDate() {
        return this.lastReviewedDate;
    }

    public License lastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
        return this;
    }

    public void setLastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
    }

    public SortedSet<LicenseConflict> getLicenseConflicts() {
        return this.licenseConflicts;
    }

    public void setLicenseConflicts(SortedSet<LicenseConflict> licenseConflicts) {
        if (this.licenseConflicts != null) {
            this.licenseConflicts.forEach(i -> i.setFirstLicenseConflict(null));
        }
        if (licenseConflicts != null) {
            licenseConflicts.forEach(i -> i.setFirstLicenseConflict(this));
        }
        this.licenseConflicts = licenseConflicts;
    }

    public License licenseConflicts(SortedSet<LicenseConflict> licenseConflicts) {
        this.setLicenseConflicts(licenseConflicts);
        return this;
    }

    public License addLicenseConflict(LicenseConflict licenseConflict) {
        this.licenseConflicts.add(licenseConflict);
        licenseConflict.setFirstLicenseConflict(this);
        return this;
    }

    public License removeLicenseConflict(LicenseConflict licenseConflict) {
        this.licenseConflicts.remove(licenseConflict);
        licenseConflict.setFirstLicenseConflict(null);
        return this;
    }

    public User getLastReviewedBy() {
        return this.lastReviewedBy;
    }

    public License lastReviewedBy(User user) {
        this.setLastReviewedBy(user);
        return this;
    }

    public void setLastReviewedBy(User user) {
        this.lastReviewedBy = user;
    }

    public LicenseRisk getLicenseRisk() {
        return this.licenseRisk;
    }

    public License licenseRisk(LicenseRisk licenseRisk) {
        this.setLicenseRisk(licenseRisk);
        return this;
    }

    public void setLicenseRisk(LicenseRisk licenseRisk) {
        this.licenseRisk = licenseRisk;
    }

    public Set<Requirement> getRequirements() {
        return this.requirements;
    }

    public License requirements(Set<Requirement> requirements) {
        this.setRequirements(requirements);
        return this;
    }

    public License addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
        requirement.getLicenses().add(this);
        return this;
    }

    public License removeRequirement(Requirement requirement) {
        this.requirements.remove(requirement);
        requirement.getLicenses().remove(this);
        return this;
    }

    public void setRequirements(Set<Requirement> requirements) {
        this.requirements = requirements;
    }

    public Set<LicensePerLibrary> getLinkedLibraries() {
        return linkedLibraries;
    }

    public void setLinkedLibraries(Set<LicensePerLibrary> linkedLibraries) {
        this.linkedLibraries = linkedLibraries;
    }

    public License linkedLibraries(Set<LicensePerLibrary> linkedLibraries) {
        this.setLinkedLibraries(linkedLibraries);
        return this;
    }

    /*    public Set<Library> getLibraries() {
        return this.libraries;
    }

    public License libraries(Set<Library> libraries) {
        this.setLibraries(libraries);
        return this;
    }

    public License addLibrary(Library library) {
        this.libraries.add(library);
        library.getLicenses().add(this);
        return this;
    }

    public License removeLibrary(Library library) {
        this.libraries.remove(library);
        library.getLicenses().remove(this);
        return this;
    }

    public void setLibraries(Set<Library> libraries) {
        if (this.libraries != null) {
            this.libraries.forEach(i -> i.removeLicense(this));
        }
        if (libraries != null) {
            libraries.forEach(i -> i.addLicense(this));
        }
        this.libraries = libraries;
    }*/

    public Set<Library> getLibraryPublishes() {
        return this.libraryPublishes;
    }

    public License libraryPublishes(Set<Library> libraries) {
        this.setLibraryPublishes(libraries);
        return this;
    }

    public License addLibraryPublish(Library library) {
        this.libraryPublishes.add(library);
        library.getLicenseToPublishes().add(this);
        return this;
    }

    public License removeLibraryPublish(Library library) {
        this.libraryPublishes.remove(library);
        library.getLicenseToPublishes().remove(this);
        return this;
    }

    public void setLibraryPublishes(Set<Library> libraries) {
        if (this.libraryPublishes != null) {
            this.libraryPublishes.forEach(i -> i.removeLicenseToPublish(this));
        }
        if (libraries != null) {
            libraries.forEach(i -> i.addLicenseToPublish(this));
        }
        this.libraryPublishes = libraries;
    }

    public Set<Library> getLibraryFiles() {
        return this.libraryFiles;
    }

    public License libraryFiles(Set<Library> libraries) {
        this.setLibraryFiles(libraries);
        return this;
    }

    public License addLibraryFiles(Library library) {
        this.libraryFiles.add(library);
        library.getLicenseOfFiles().add(this);
        return this;
    }

    public License removeLibraryFiles(Library library) {
        this.libraryFiles.remove(library);
        library.getLicenseOfFiles().remove(this);
        return this;
    }

    public void setLibraryFiles(Set<Library> libraries) {
        if (this.libraryFiles != null) {
            this.libraryFiles.forEach(i -> i.removeLicenseOfFiles(this));
        }
        if (libraries != null) {
            libraries.forEach(i -> i.addLicenseOfFiles(this));
        }
        this.libraryFiles = libraries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof License)) {
            return false;
        }
        return id != null && id.equals(((License) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "License{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", shortIdentifier='" + getShortIdentifier() + "'" +
            ", spdxIdentifier='" + getSpdxIdentifier() + "'" +
            ", url='" + getUrl() + "'" +
            ", genericLicenseText='" + StringUtils.abbreviate(getGenericLicenseText(), 14) + "'" +
            ", other='" + StringUtils.abbreviate(getOther(), 14) + "'" +
            ", reviewed='" + getReviewed() + "'" +
            ", lastReviewedDate='" + getLastReviewedDate() + "'" +
            "}";
    }
}
