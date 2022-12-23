package net.regnology.lucy.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.web.rest.LicenseResource;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link License} entity. This class is used
 * in {@link LicenseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /licenses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LicenseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private StringFilter shortIdentifier;

    private StringFilter spdxIdentifier;

    private StringFilter url;

    private StringFilter other;

    private BooleanFilter reviewed;

    private LocalDateFilter lastReviewedDate;

    private LongFilter licenseConflictId;

    private LongFilter lastReviewedById;

    private LongFilter licenseRiskId;

    private LongFilter requirementId;

    private LongFilter libraryPublishId;

    private LongFilter libraryFilesId;

    private Boolean distinct;

    public LicenseCriteria() {}

    public LicenseCriteria(LicenseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.shortIdentifier = other.shortIdentifier == null ? null : other.shortIdentifier.copy();
        this.spdxIdentifier = other.spdxIdentifier == null ? null : other.spdxIdentifier.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.other = other.other == null ? null : other.other.copy();
        this.reviewed = other.reviewed == null ? null : other.reviewed.copy();
        this.lastReviewedDate = other.lastReviewedDate == null ? null : other.lastReviewedDate.copy();
        this.licenseConflictId = other.licenseConflictId == null ? null : other.licenseConflictId.copy();
        this.lastReviewedById = other.lastReviewedById == null ? null : other.lastReviewedById.copy();
        this.licenseRiskId = other.licenseRiskId == null ? null : other.licenseRiskId.copy();
        this.requirementId = other.requirementId == null ? null : other.requirementId.copy();
        this.libraryPublishId = other.libraryPublishId == null ? null : other.libraryPublishId.copy();
        this.libraryFilesId = other.libraryFilesId == null ? null : other.libraryFilesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LicenseCriteria copy() {
        return new LicenseCriteria(this);
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

    public StringFilter getFullName() {
        return fullName;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getShortIdentifier() {
        return shortIdentifier;
    }

    public StringFilter shortIdentifier() {
        if (shortIdentifier == null) {
            shortIdentifier = new StringFilter();
        }
        return shortIdentifier;
    }

    public void setShortIdentifier(StringFilter shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
    }

    public StringFilter getSpdxIdentifier() {
        return spdxIdentifier;
    }

    public StringFilter spdxIdentifier() {
        if (spdxIdentifier == null) {
            spdxIdentifier = new StringFilter();
        }
        return spdxIdentifier;
    }

    public void setSpdxIdentifier(StringFilter spdxIdentifier) {
        this.spdxIdentifier = spdxIdentifier;
    }

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getOther() {
        return other;
    }

    public StringFilter other() {
        if (other == null) {
            other = new StringFilter();
        }
        return other;
    }

    public void setOther(StringFilter other) {
        this.other = other;
    }

    public BooleanFilter getReviewed() {
        return reviewed;
    }

    public BooleanFilter reviewed() {
        if (reviewed == null) {
            reviewed = new BooleanFilter();
        }
        return reviewed;
    }

    public void setReviewed(BooleanFilter reviewed) {
        this.reviewed = reviewed;
    }

    public LocalDateFilter getLastReviewedDate() {
        return lastReviewedDate;
    }

    public LocalDateFilter lastReviewedDate() {
        if (lastReviewedDate == null) {
            lastReviewedDate = new LocalDateFilter();
        }
        return lastReviewedDate;
    }

    public void setLastReviewedDate(LocalDateFilter lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
    }

    public LongFilter getLicenseConflictId() {
        return licenseConflictId;
    }

    public LongFilter licenseConflictId() {
        if (licenseConflictId == null) {
            licenseConflictId = new LongFilter();
        }
        return licenseConflictId;
    }

    public void setLicenseConflictId(LongFilter licenseConflictId) {
        this.licenseConflictId = licenseConflictId;
    }

    public LongFilter getLastReviewedById() {
        return lastReviewedById;
    }

    public LongFilter lastReviewedById() {
        if (lastReviewedById == null) {
            lastReviewedById = new LongFilter();
        }
        return lastReviewedById;
    }

    public void setLastReviewedById(LongFilter lastReviewedById) {
        this.lastReviewedById = lastReviewedById;
    }

    public LongFilter getLicenseRiskId() {
        return licenseRiskId;
    }

    public LongFilter licenseRiskId() {
        if (licenseRiskId == null) {
            licenseRiskId = new LongFilter();
        }
        return licenseRiskId;
    }

    public void setLicenseRiskId(LongFilter licenseRiskId) {
        this.licenseRiskId = licenseRiskId;
    }

    public LongFilter getRequirementId() {
        return requirementId;
    }

    public LongFilter requirementId() {
        if (requirementId == null) {
            requirementId = new LongFilter();
        }
        return requirementId;
    }

    public void setRequirementId(LongFilter requirementId) {
        this.requirementId = requirementId;
    }

    public LongFilter getLibraryPublishId() {
        return libraryPublishId;
    }

    public LongFilter libraryPublishId() {
        if (libraryPublishId == null) {
            libraryPublishId = new LongFilter();
        }
        return libraryPublishId;
    }

    public void setLibraryPublishId(LongFilter libraryPublishId) {
        this.libraryPublishId = libraryPublishId;
    }

    public LongFilter getLibraryFilesId() {
        return libraryFilesId;
    }

    public LongFilter libraryFilesId() {
        if (libraryFilesId == null) {
            libraryFilesId = new LongFilter();
        }
        return libraryFilesId;
    }

    public void setLibraryFilesId(LongFilter libraryFilesId) {
        this.libraryFilesId = libraryFilesId;
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
        final LicenseCriteria that = (LicenseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(shortIdentifier, that.shortIdentifier) &&
            Objects.equals(spdxIdentifier, that.spdxIdentifier) &&
            Objects.equals(url, that.url) &&
            Objects.equals(other, that.other) &&
            Objects.equals(reviewed, that.reviewed) &&
            Objects.equals(lastReviewedDate, that.lastReviewedDate) &&
            Objects.equals(licenseConflictId, that.licenseConflictId) &&
            Objects.equals(lastReviewedById, that.lastReviewedById) &&
            Objects.equals(licenseRiskId, that.licenseRiskId) &&
            Objects.equals(requirementId, that.requirementId) &&
            Objects.equals(libraryPublishId, that.libraryPublishId) &&
            Objects.equals(libraryFilesId, that.libraryFilesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            fullName,
            shortIdentifier,
            spdxIdentifier,
            url,
            other,
            reviewed,
            lastReviewedDate,
            licenseConflictId,
            lastReviewedById,
            licenseRiskId,
            requirementId,
            libraryPublishId,
            libraryFilesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicenseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fullName != null ? "fullName=" + fullName + ", " : "") +
            (shortIdentifier != null ? "shortIdentifier=" + shortIdentifier + ", " : "") +
            (spdxIdentifier != null ? "spdxIdentifier=" + spdxIdentifier + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (other != null ? "other=" + other + ", " : "") +
            (reviewed != null ? "reviewed=" + reviewed + ", " : "") +
            (lastReviewedDate != null ? "lastReviewedDate=" + lastReviewedDate + ", " : "") +
            (licenseConflictId != null ? "licenseConflictId=" + licenseConflictId + ", " : "") +
            (lastReviewedById != null ? "lastReviewedById=" + lastReviewedById + ", " : "") +
            (licenseRiskId != null ? "licenseRiskId=" + licenseRiskId + ", " : "") +
            (requirementId != null ? "requirementId=" + requirementId + ", " : "") +
            (libraryPublishId != null ? "libraryPublishId=" + libraryPublishId + ", " : "") +
            (libraryFilesId != null ? "libraryFilesId=" + libraryFilesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
