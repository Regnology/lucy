package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import net.regnology.lucy.domain.enumeration.UploadState;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "identifier", nullable = false)
    private String identifier;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "created_date")
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "last_updated_date")
    private LocalDate lastUpdatedDate;

    @Size(max = 2048)
    @Column(name = "target_url", length = 2048)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_state")
    private UploadState uploadState = UploadState.SUCCESSFUL;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "disclaimer")
    private String disclaimer;

    @Column(name = "delivered")
    private Boolean delivered = false;

    @Column(name = "delivered_date")
    private Instant deliveredDate = null;

    @Size(max = 2048)
    @Column(name = "contact", length = 2048)
    private String contact;

    @Size(max = 4096)
    @Column(name = "comment", length = 4096)
    private String comment;

    @Column(name = "previous_product_id")
    private Long previousProductId;

    @Size(max = 2048)
    @Column(name = "upload_filter", length = 2048)
    private String uploadFilter;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "library", "product" }, allowSetters = true)
    private Set<LibraryPerProduct> libraries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Product identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return this.version;
    }

    public Product version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public Product createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    public Product lastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
        return this;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public Product targetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
        return this;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public UploadState getUploadState() {
        return this.uploadState;
    }

    public Product uploadState(UploadState uploadState) {
        this.uploadState = uploadState;
        return this;
    }

    public void setUploadState(UploadState uploadState) {
        this.uploadState = uploadState;
    }

    public String getDisclaimer() {
        return this.disclaimer;
    }

    public Product disclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
        return this;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public Product delivered(Boolean delivered) {
        this.delivered = delivered;
        return this;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Instant getDeliveredDate() {
        return deliveredDate;
    }

    public Product deliveredDate(Instant deliveredDate) {
        this.deliveredDate = deliveredDate;
        return this;
    }

    public void setDeliveredDate(Instant deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getContact() {
        return contact;
    }

    public Product contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getComment() {
        return comment;
    }

    public Product comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPreviousProductId() {
        return previousProductId;
    }

    public Product previousProductId(Long previousProductId) {
        this.previousProductId = previousProductId;
        return this;
    }

    public void setPreviousProductId(Long previousProductId) {
        this.previousProductId = previousProductId;
    }

    public String getUploadFilter() {
        return uploadFilter;
    }

    public Product uploadFilter(String uploadFilter) {
        this.uploadFilter = uploadFilter;
        return this;
    }

    public void setUploadFilter(String uploadFilter) {
        this.uploadFilter = uploadFilter;
    }

    public Set<LibraryPerProduct> getLibraries() {
        return this.libraries;
    }

    public Product libraries(Set<LibraryPerProduct> libraryPerProducts) {
        this.setLibraries(libraryPerProducts);
        return this;
    }

    public Product addLibrary(LibraryPerProduct libraryPerProduct) {
        this.libraries.add(libraryPerProduct);
        libraryPerProduct.setProduct(this);
        return this;
    }

    public Product removeLibrary(LibraryPerProduct libraryPerProduct) {
        this.libraries.remove(libraryPerProduct);
        libraryPerProduct.setProduct(null);
        return this;
    }

    public void setLibraries(Set<LibraryPerProduct> libraryPerProducts) {
        if (this.libraries != null) {
            this.libraries.forEach(i -> i.setProduct(null));
        }
        if (libraryPerProducts != null) {
            libraryPerProducts.forEach(i -> i.setProduct(this));
        }
        this.libraries = libraryPerProducts;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", identifier='" + getIdentifier() + "'" +
            ", version='" + getVersion() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastUpdatedDate='" + getLastUpdatedDate() + "'" +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", uploadState='" + getUploadState() + "'" +
            ", disclaimer='" + StringUtils.abbreviate(getDisclaimer(), 20) + "'" +
            ", delivered='" + getDelivered() + "'" +
            ", deliveredDate='" + getDeliveredDate() + "'" +
            ", contact='" + StringUtils.abbreviate(getContact(), 20) + "'" +
            ", comment='" + StringUtils.abbreviate(getComment(), 20) + "'" +
            ", previousProductId='" + getPreviousProductId() + "'" +
            ", uploadFilter='" + StringUtils.abbreviate(getUploadFilter(), 20) + "'" +
            "}";
    }
}
