package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import net.regnology.lucy.domain.enumeration.LinkType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A LicensePerLibrary.
 */
@Entity
@Table(name = "license_per_library")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LicensePerLibrary implements Serializable, Comparable<LicensePerLibrary> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LicensePerLibrary.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_type")
    private LinkType linkType;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "lastReviewedBy", "requirements", "libraries", "libraryPublishes", "libraryFiles" },
        allowSetters = true
    )
    private License license;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "licenses", "errorLogs", "lastReviewedBy", "licenses", "licenseToPublishes", "licenseOfFiles" },
        allowSetters = true
    )
    private Library library;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LicensePerLibrary id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return this.orderId;
    }

    public LicensePerLibrary orderId(Integer orderId) {
        this.setOrderId(orderId);
        return this;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LinkType getLinkType() {
        return this.linkType;
    }

    public LicensePerLibrary linkType(LinkType linkType) {
        this.setLinkType(linkType);
        return this;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public License getLicense() {
        return this.license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public LicensePerLibrary license(License license) {
        this.setLicense(license);
        return this;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public LicensePerLibrary library(Library library) {
        this.setLibrary(library);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicensePerLibrary)) {
            return false;
        }
        return id != null && id.equals(((LicensePerLibrary) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicensePerLibrary{" +
            "id=" + getId() +
            ", orderId='" + getOrderId() + "'" +
            ", linkType='" + getLinkType() + "'" +
            "}";
    }

    @Override
    public int compareTo(LicensePerLibrary o) {
        return orderId.compareTo(o.getOrderId());
    }
}
