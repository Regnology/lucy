package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.web.rest.ProductResource;
import tech.jhipster.service.filter.Filter;

/**
 * Custom criteria class for the {@link Product} entity. This class is used
 * in {@link ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCustomCriteria extends ProductCriteria {

    private static final long serialVersionUID = 1L;

    public ProductCustomCriteria() {
        super();
    }

    public ProductCustomCriteria(ProductCustomCriteria other) {
        super(other);
    }

    @Override
    public ProductCustomCriteria copy() {
        return new ProductCustomCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCustomCriteria that = (ProductCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getName(), that.getName()) &&
            Objects.equals(getIdentifier(), that.getIdentifier()) &&
            Objects.equals(getVersion(), that.getVersion()) &&
            Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
            Objects.equals(getLastUpdatedDate(), that.getLastUpdatedDate()) &&
            Objects.equals(getTargetUrl(), that.getTargetUrl()) &&
            Objects.equals(getUploadState(), that.getUploadState()) &&
            Objects.equals(getDelivered(), that.getDelivered()) &&
            Objects.equals(getDeliveredDate(), that.getDeliveredDate()) &&
            Objects.equals(getContact(), that.getContact()) &&
            Objects.equals(getComment(), that.getComment()) &&
            Objects.equals(getPreviousProductId(), that.getPreviousProductId()) &&
            Objects.equals(getUploadFilter(), that.getUploadFilter()) &&
            Objects.equals(getLibraryId(), that.getLibraryId()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getId(),
            getName(),
            getIdentifier(),
            getVersion(),
            getCreatedDate(),
            getLastUpdatedDate(),
            getTargetUrl(),
            getUploadState(),
            getDelivered(),
            getDeliveredDate(),
            getContact(),
            getComment(),
            getPreviousProductId(),
            getUploadFilter(),
            getLibraryId(),
            getDistinct()
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getName() != null ? "name=" + getName() + ", " : "") +
            (getIdentifier() != null ? "identifier=" + getIdentifier() + ", " : "") +
            (getVersion() != null ? "version=" + getVersion() + ", " : "") +
            (getCreatedDate() != null ? "createdDate=" + getCreatedDate() + ", " : "") +
            (getLastUpdatedDate() != null ? "lastUpdatedDate=" + getLastUpdatedDate() + ", " : "") +
            (getTargetUrl() != null ? "uploadUrl=" + getTargetUrl() + ", " : "") +
            (getUploadState() != null ? "uploadState=" + getUploadState() + ", " : "") +
            (getDelivered() != null ? "delivered=" + getDelivered() + ", " : "") +
            (getDeliveredDate() != null ? "deliveredDate=" + getDeliveredDate() + ", " : "") +
            (getContact() != null ? "contact=" + getContact() + ", " : "") +
            (getComment() != null ? "comment=" + getComment() + ", " : "") +
            (getPreviousProductId() != null ? "previousProductId=" + getPreviousProductId() + ", " : "") +
            (getUploadFilter() != null ? "uploadFilter=" + getUploadFilter() + ", " : "") +
            (getLibraryId() != null ? "libraryId=" + getLibraryId() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
