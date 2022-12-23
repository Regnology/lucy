package net.regnology.lucy.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.domain.enumeration.LinkType;
import net.regnology.lucy.web.rest.LicensePerLibraryResource;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;

/**
 * Criteria class for the {@link LicensePerLibrary} entity. This class is used
 * in {@link LicensePerLibraryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /license-per-libraries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LicensePerLibraryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LinkType
     */
    public static class LinkTypeFilter extends Filter<LinkType> {

        public LinkTypeFilter() {}

        public LinkTypeFilter(LinkTypeFilter filter) {
            super(filter);
        }

        @Override
        public LinkTypeFilter copy() {
            return new LinkTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter orderId;

    private LinkTypeFilter linkType;

    private LongFilter licenseId;

    private LongFilter libraryId;

    private Boolean distinct;

    public LicensePerLibraryCriteria() {}

    public LicensePerLibraryCriteria(LicensePerLibraryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.linkType = other.linkType == null ? null : other.linkType.copy();
        this.licenseId = other.licenseId == null ? null : other.licenseId.copy();
        this.libraryId = other.libraryId == null ? null : other.libraryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LicensePerLibraryCriteria copy() {
        return new LicensePerLibraryCriteria(this);
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

    public IntegerFilter getOrderId() {
        return orderId;
    }

    public IntegerFilter orderId() {
        if (orderId == null) {
            orderId = new IntegerFilter();
        }
        return orderId;
    }

    public void setOrderId(IntegerFilter orderId) {
        this.orderId = orderId;
    }

    public LinkTypeFilter getLinkType() {
        return linkType;
    }

    public LinkTypeFilter linkType() {
        if (linkType == null) {
            linkType = new LinkTypeFilter();
        }
        return linkType;
    }

    public void setLinkType(LinkTypeFilter linkType) {
        this.linkType = linkType;
    }

    public LongFilter getLicenseId() {
        return licenseId;
    }

    public LongFilter licenseId() {
        if (licenseId == null) {
            licenseId = new LongFilter();
        }
        return licenseId;
    }

    public void setLicenseId(LongFilter licenseId) {
        this.licenseId = licenseId;
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
        final LicensePerLibraryCriteria that = (LicensePerLibraryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(linkType, that.linkType) &&
            Objects.equals(licenseId, that.licenseId) &&
            Objects.equals(libraryId, that.libraryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, linkType, licenseId, libraryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicensePerLibraryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (linkType != null ? "linkType=" + linkType + ", " : "") +
            (licenseId != null ? "licenseId=" + licenseId + ", " : "") +
            (libraryId != null ? "libraryId=" + libraryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
