package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Requirement.
 */
@Entity
@Table(name = "requirement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Requirement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "short_text", nullable = false)
    private String shortText;

    @Size(max = 2048)
    @Column(name = "description", length = 2048)
    private String description;

    @ManyToMany(mappedBy = "requirements")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "lastReviewedBy", "licenseRisk", "requirements", "libraries", "libraryPublishes", "libraryFiles" },
        allowSetters = true
    )
    private Set<License> licenses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Requirement id(Long id) {
        this.id = id;
        return this;
    }

    public String getShortText() {
        return this.shortText;
    }

    public Requirement shortText(String shortText) {
        this.shortText = shortText;
        return this;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getDescription() {
        return this.description;
    }

    public Requirement description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<License> getLicenses() {
        return this.licenses;
    }

    public Requirement licenses(Set<License> licenses) {
        this.setLicenses(licenses);
        return this;
    }

    public Requirement addLicense(License license) {
        this.licenses.add(license);
        license.getRequirements().add(this);
        return this;
    }

    public Requirement removeLicense(License license) {
        this.licenses.remove(license);
        license.getRequirements().remove(this);
        return this;
    }

    public void setLicenses(Set<License> licenses) {
        if (this.licenses != null) {
            this.licenses.forEach(i -> i.removeRequirement(this));
        }
        if (licenses != null) {
            licenses.forEach(i -> i.addRequirement(this));
        }
        this.licenses = licenses;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Requirement)) {
            return false;
        }
        return id != null && id.equals(((Requirement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Requirement{" +
            "id=" + getId() +
            ", shortText='" + getShortText() + "'" +
            ", description='" + StringUtils.abbreviate(getDescription(), 14) + "'" +
            "}";
    }
}
