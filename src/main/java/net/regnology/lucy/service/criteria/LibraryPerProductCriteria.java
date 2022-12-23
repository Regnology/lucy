package net.regnology.lucy.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.web.rest.LibraryPerProductResource;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link LibraryPerProduct} entity. This class is used
 * in {@link LibraryPerProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /library-per-products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LibraryPerProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter addedDate;

    private BooleanFilter addedManually;

    private BooleanFilter hideForPublishing;

    private StringFilter comment;

    private LongFilter libraryId;

    private LongFilter productId;

    private Boolean distinct;

    public LibraryPerProductCriteria() {}

    public LibraryPerProductCriteria(LibraryPerProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.addedDate = other.addedDate == null ? null : other.addedDate.copy();
        this.addedManually = other.addedManually == null ? null : other.addedManually.copy();
        this.hideForPublishing = other.hideForPublishing == null ? null : other.hideForPublishing.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.libraryId = other.libraryId == null ? null : other.libraryId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LibraryPerProductCriteria copy() {
        return new LibraryPerProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getAddedDate() {
        return addedDate;
    }

    public LocalDateFilter addedDate() {
        if (addedDate == null) {
            addedDate = new LocalDateFilter();
        }
        return addedDate;
    }

    public void setAddedDate(LocalDateFilter addedDate) {
        this.addedDate = addedDate;
    }

    public BooleanFilter getAddedManually() {
        return addedManually;
    }

    public BooleanFilter addedManually() {
        if (addedManually == null) {
            addedManually = new BooleanFilter();
        }
        return addedManually;
    }

    public void setAddedManually(BooleanFilter addedManually) {
        this.addedManually = addedManually;
    }

    public BooleanFilter getHideForPublishing() {
        return hideForPublishing;
    }

    public BooleanFilter hideForPublishing() {
        if (hideForPublishing == null) {
            hideForPublishing = new BooleanFilter();
        }
        return hideForPublishing;
    }

    public void setHideForPublishing(BooleanFilter hideForPublishing) {
        this.hideForPublishing = hideForPublishing;
    }

    public StringFilter getComment() {
        return comment;
    }

    public StringFilter comment() {
        if (comment == null) {
            comment = new StringFilter();
        }
        return comment;
    }

    public void setComment(StringFilter comment) {
        this.comment = comment;
    }

    public LongFilter getLibraryId() {
        return libraryId;
    }

    public LongFilter libraryId() {
        if (libraryId == null) {
            libraryId = new LongFilter();
        }
        return libraryId;
    }

    public void setLibraryId(LongFilter libraryId) {
        this.libraryId = libraryId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LibraryPerProductCriteria that = (LibraryPerProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(addedDate, that.addedDate) &&
            Objects.equals(addedManually, that.addedManually) &&
            Objects.equals(hideForPublishing, that.hideForPublishing) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(libraryId, that.libraryId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addedDate, addedManually, hideForPublishing, comment, libraryId, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryPerProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (addedDate != null ? "addedDate=" + addedDate + ", " : "") +
            (addedManually != null ? "addedManually=" + addedManually + ", " : "") +
            (hideForPublishing != null ? "hideForPublishing=" + hideForPublishing + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (libraryId != null ? "libraryId=" + libraryId + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
