package net.regnology.lucy.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LicenseNamingMapping.
 */
@Entity
@Table(name = "license_naming_mapping")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LicenseNamingMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 512)
    @Column(name = "regex", length = 512, nullable = false)
    private String regex;

    @Column(name = "uniform_short_identifier")
    private String uniformShortIdentifier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LicenseNamingMapping id(Long id) {
        this.id = id;
        return this;
    }

    public String getRegex() {
        return this.regex;
    }

    public LicenseNamingMapping regex(String regex) {
        this.setRegex(regex);
        return this;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getUniformShortIdentifier() {
        return this.uniformShortIdentifier;
    }

    public LicenseNamingMapping uniformShortIdentifier(String uniformShortIdentifier) {
        this.setUniformShortIdentifier(uniformShortIdentifier);
        return this;
    }

    public void setUniformShortIdentifier(String uniformShortIdentifier) {
        this.uniformShortIdentifier = uniformShortIdentifier;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicenseNamingMapping)) {
            return false;
        }
        return id != null && id.equals(((LicenseNamingMapping) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicenseNamingMapping{" +
            "id=" + getId() +
            ", regex='" + getRegex() + "'" +
            ", uniformShortIdentifier='" + getUniformShortIdentifier() + "'" +
            "}";
    }
}
