package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.web.rest.LicenseResource;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Custom criteria class for the {@link License} entity. This class is used
 * in {@link LicenseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /licenses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LicenseCustomCriteria extends LicenseCriteria {

    private static final long serialVersionUID = 1L;

    private StringFilter requirementShortText;

    public LicenseCustomCriteria() {
        super();
    }

    public LicenseCustomCriteria(LicenseCustomCriteria other) {
        super(other);
        this.requirementShortText = other.requirementShortText == null ? null : other.requirementShortText.copy();
    }

    @Override
    public LicenseCustomCriteria copy() {
        return new LicenseCustomCriteria(this);
    }

    public StringFilter getRequirementShortText() {
        return requirementShortText;
    }

    public StringFilter requirementShortText() {
        if (requirementShortText == null) {
            requirementShortText = new StringFilter();
        }
        return requirementShortText;
    }

    public void setRequirementShortText(StringFilter requirementShortText) {
        this.requirementShortText = requirementShortText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LicenseCustomCriteria that = (LicenseCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getFullName(), that.getFullName()) &&
            Objects.equals(getShortIdentifier(), that.getShortIdentifier()) &&
            Objects.equals(getSpdxIdentifier(), that.getSpdxIdentifier()) &&
            Objects.equals(getUrl(), that.getUrl()) &&
            Objects.equals(getOther(), that.getOther()) &&
            Objects.equals(getReviewed(), that.getReviewed()) &&
            Objects.equals(getLastReviewedDate(), that.getLastReviewedDate()) &&
            Objects.equals(getLastReviewedById(), that.getLastReviewedById()) &&
            Objects.equals(getLicenseRiskId(), that.getLicenseRiskId()) &&
            Objects.equals(getRequirementShortText(), that.getRequirementShortText()) &&
            Objects.equals(getLibraryPublishId(), that.getLibraryPublishId()) &&
            Objects.equals(getLibraryFilesId(), that.getLibraryFilesId()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getId(),
            getFullName(),
            getShortIdentifier(),
            getSpdxIdentifier(),
            getUrl(),
            getOther(),
            getReviewed(),
            getLastReviewedDate(),
            getLastReviewedById(),
            getLicenseRiskId(),
            getRequirementShortText(),
            getLibraryPublishId(),
            getLibraryFilesId(),
            getDistinct()
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicenseCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getFullName() != null ? "fullName=" + getFullName() + ", " : "") +
            (getShortIdentifier() != null ? "shortIdentifier=" + getShortIdentifier() + ", " : "") +
            (getSpdxIdentifier() != null ? "spdxIdentifier=" + getSpdxIdentifier() + ", " : "") +
            (getUrl() != null ? "url=" + getUrl() + ", " : "") +
            (getOther() != null ? "other=" + getOther() + ", " : "") +
            (getReviewed() != null ? "reviewed=" + getReviewed() + ", " : "") +
            (getLastReviewedDate() != null ? "lastReviewedDate=" + getLastReviewedDate() + ", " : "") +
            (getLastReviewedById() != null ? "lastReviewedById=" + getLastReviewedById() + ", " : "") +
            (getLicenseRiskId() != null ? "licenseRiskId=" + getLicenseRiskId() + ", " : "") +
            (getRequirementShortText() != null ? "requirementShortText=" + getRequirementShortText() + ", " : "") +
            (getLibraryPublishId() != null ? "libraryPublishId=" + getLibraryPublishId() + ", " : "") +
            (getLibraryFilesId() != null ? "libraryFilesId=" + getLibraryFilesId() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
