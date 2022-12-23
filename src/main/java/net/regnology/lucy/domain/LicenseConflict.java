package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import net.regnology.lucy.domain.enumeration.CompatibilityState;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A LicenseConflict.
 */
@Entity
@Table(name = "license_conflict")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LicenseConflict implements Serializable, Comparable<LicenseConflict> {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LicenseConflict.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "compatibility")
    private CompatibilityState compatibility;

    @Size(max = 4096)
    @Column(name = "comment", length = 4096)
    private String comment;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "licenseConflicts", "lastReviewedBy", "licenseRisk", "requirements", "libraryPublishes", "libraryFiles" },
        allowSetters = true
    )
    private License firstLicenseConflict;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "licenseConflicts", "lastReviewedBy", "licenseRisk", "requirements", "libraryPublishes", "libraryFiles" },
        allowSetters = true
    )
    private License secondLicenseConflict;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LicenseConflict id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompatibilityState getCompatibility() {
        return this.compatibility;
    }

    public LicenseConflict compatibility(CompatibilityState compatibility) {
        this.setCompatibility(compatibility);
        return this;
    }

    public void setCompatibility(CompatibilityState compatibility) {
        this.compatibility = compatibility;
    }

    public String getComment() {
        return this.comment;
    }

    public LicenseConflict comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public License getFirstLicenseConflict() {
        return this.firstLicenseConflict;
    }

    public void setFirstLicenseConflict(License license) {
        this.firstLicenseConflict = license;
    }

    public LicenseConflict firstLicenseConflict(License license) {
        this.setFirstLicenseConflict(license);
        return this;
    }

    public License getSecondLicenseConflict() {
        return this.secondLicenseConflict;
    }

    public void setSecondLicenseConflict(License license) {
        this.secondLicenseConflict = license;
    }

    public LicenseConflict secondLicenseConflict(License license) {
        this.setSecondLicenseConflict(license);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicenseConflict)) {
            return false;
        }
        return id != null && id.equals(((LicenseConflict) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicenseConflict{" +
            "id=" + getId() +
            ", compatibility='" + getCompatibility() + "'" +
            ", comment='" + getComment() + "'" +
            "}";
    }

    @Override
    public int compareTo(LicenseConflict o) {
        if (
            (o != null && o.getSecondLicenseConflict() != null && o.getSecondLicenseConflict().getShortIdentifier() != null) &&
            (secondLicenseConflict != null && secondLicenseConflict.getShortIdentifier() != null)
        ) {
            return secondLicenseConflict.getShortIdentifier().compareTo(o.getSecondLicenseConflict().getShortIdentifier());
        } else {
            return -1;
        }
    }
}
