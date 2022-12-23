package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LibraryPerProduct.
 */
@Entity
@Table(name = "library_per_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LibraryPerProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "added_date")
    private LocalDate addedDate = LocalDate.now();

    @Column(name = "added_manually")
    private Boolean addedManually = false;

    @Column(name = "hide_for_publishing")
    private Boolean hideForPublishing;

    @Size(max = 4096)
    @Column(name = "comment", length = 4096)
    private String comment;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lastReviewedBy", "licenseOfFiles" }, allowSetters = true)
    private Library library;

    @ManyToOne
    @JsonIgnore
    private Product product;

    /* Methods */

    /**
     * Checks if the fields that marks a Library / LibraryPerProduct as hidden are true of false.
     *
     * @return true if one of the fields is true, otherwise false.
     */
    public boolean isHidden() {
        boolean isHidden = getHideForPublishing() != null && getHideForPublishing();
        if (getLibrary().getHideForPublishing() != null && getLibrary().getHideForPublishing()) {
            isHidden = true;
        }

        return !isHidden;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LibraryPerProduct id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getAddedDate() {
        return this.addedDate;
    }

    public LibraryPerProduct addedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
        return this;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getAddedManually() {
        return this.addedManually;
    }

    public LibraryPerProduct addedManually(Boolean addedManually) {
        this.addedManually = addedManually;
        return this;
    }

    public void setAddedManually(Boolean addedManually) {
        this.addedManually = addedManually;
    }

    public Boolean getHideForPublishing() {
        return this.hideForPublishing;
    }

    public LibraryPerProduct hideForPublishing(Boolean hideForPublishing) {
        this.hideForPublishing = hideForPublishing;
        return this;
    }

    public void setHideForPublishing(Boolean hideForPublishing) {
        this.hideForPublishing = hideForPublishing;
    }

    public String getComment() {
        return this.comment;
    }

    public LibraryPerProduct comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Library getLibrary() {
        return this.library;
    }

    public LibraryPerProduct library(Library library) {
        this.setLibrary(library);
        return this;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Product getProduct() {
        return this.product;
    }

    public LibraryPerProduct product(Product product) {
        this.setProduct(product);
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LibraryPerProduct)) {
            return false;
        }
        return id != null && id.equals(((LibraryPerProduct) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryPerProduct{" +
            "id=" + getId() +
            ", addedDate='" + getAddedDate() + "'" +
            ", addedManually='" + getAddedManually() + "'" +
            ", hideForPublishing='" + getHideForPublishing() + "'" +
            ", comment='" + StringUtils.abbreviate(getComment(), 14) + "'" +
            "}";
    }
}
