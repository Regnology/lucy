package net.regnology.lucy.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.web.rest.LibraryResource;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link Library} entity. This class is used
 * in {@link LibraryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /libraries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LibraryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LibraryType
     */
    public static class LibraryTypeFilter extends Filter<LibraryType> {

        public LibraryTypeFilter() {}

        public LibraryTypeFilter(LibraryTypeFilter filter) {
            super(filter);
        }

        @Override
        public LibraryTypeFilter copy() {
            return new LibraryTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter groupId;

    private StringFilter artifactId;

    private StringFilter version;

    private LibraryTypeFilter type;

    private StringFilter originalLicense;

    private StringFilter licenseUrl;

    private StringFilter sourceCodeUrl;

    private StringFilter pUrl;

    private StringFilter copyright;

    private StringFilter compliance;

    private StringFilter complianceComment;

    private StringFilter comment;

    private BooleanFilter reviewed;

    private BooleanFilter reviewedDeepScan;

    private LocalDateFilter lastReviewedDate;

    private LocalDateFilter lastReviewedDeepScanDate;

    private LocalDateFilter createdDate;

    private BooleanFilter hideForPublishing;

    private StringFilter md5;

    private StringFilter sha1;

    private LongFilter licenseId;

    private LongFilter errorLogId;

    private LongFilter lastReviewedById;

    private LongFilter lastReviewedDeepScanById;

    private LongFilter licenseToPublishId;

    private LongFilter licenseOfFilesId;

    private Boolean distinct;

    public LibraryCriteria() {}

    public LibraryCriteria(LibraryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
        this.artifactId = other.artifactId == null ? null : other.artifactId.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.originalLicense = other.originalLicense == null ? null : other.originalLicense.copy();
        this.licenseUrl = other.licenseUrl == null ? null : other.licenseUrl.copy();
        this.sourceCodeUrl = other.sourceCodeUrl == null ? null : other.sourceCodeUrl.copy();
        this.pUrl = other.pUrl == null ? null : other.pUrl.copy();
        this.copyright = other.copyright == null ? null : other.copyright.copy();
        this.compliance = other.compliance == null ? null : other.compliance.copy();
        this.complianceComment = other.complianceComment == null ? null : other.complianceComment.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.reviewed = other.reviewed == null ? null : other.reviewed.copy();
        this.reviewedDeepScan = other.reviewedDeepScan == null ? null : other.reviewedDeepScan.copy();
        this.lastReviewedDate = other.lastReviewedDate == null ? null : other.lastReviewedDate.copy();
        this.lastReviewedDeepScanDate = other.lastReviewedDeepScanDate == null ? null : other.lastReviewedDeepScanDate.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.hideForPublishing = other.hideForPublishing == null ? null : other.hideForPublishing.copy();
        this.md5 = other.md5 == null ? null : other.md5.copy();
        this.sha1 = other.sha1 == null ? null : other.sha1.copy();
        this.licenseId = other.licenseId == null ? null : other.licenseId.copy();
        this.errorLogId = other.errorLogId == null ? null : other.errorLogId.copy();
        this.lastReviewedById = other.lastReviewedById == null ? null : other.lastReviewedById.copy();
        this.lastReviewedDeepScanById = other.lastReviewedDeepScanById == null ? null : other.lastReviewedDeepScanById.copy();
        this.licenseToPublishId = other.licenseToPublishId == null ? null : other.licenseToPublishId.copy();
        this.licenseOfFilesId = other.licenseOfFilesId == null ? null : other.licenseOfFilesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LibraryCriteria copy() {
        return new LibraryCriteria(this);
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

    public StringFilter getGroupId() {
        return groupId;
    }

    public StringFilter groupId() {
        if (groupId == null) {
            groupId = new StringFilter();
        }
        return groupId;
    }

    public void setGroupId(StringFilter groupId) {
        this.groupId = groupId;
    }

    public StringFilter getArtifactId() {
        return artifactId;
    }

    public StringFilter artifactId() {
        if (artifactId == null) {
            artifactId = new StringFilter();
        }
        return artifactId;
    }

    public void setArtifactId(StringFilter artifactId) {
        this.artifactId = artifactId;
    }

    public StringFilter getVersion() {
        return version;
    }

    public StringFilter version() {
        if (version == null) {
            version = new StringFilter();
        }
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public LibraryTypeFilter getType() {
        return type;
    }

    public LibraryTypeFilter type() {
        if (type == null) {
            type = new LibraryTypeFilter();
        }
        return type;
    }

    public void setType(LibraryTypeFilter type) {
        this.type = type;
    }

    public StringFilter getOriginalLicense() {
        return originalLicense;
    }

    public StringFilter originalLicense() {
        if (originalLicense == null) {
            originalLicense = new StringFilter();
        }
        return originalLicense;
    }

    public void setOriginalLicense(StringFilter originalLicense) {
        this.originalLicense = originalLicense;
    }

    public StringFilter getLicenseUrl() {
        return licenseUrl;
    }

    public StringFilter licenseUrl() {
        if (licenseUrl == null) {
            licenseUrl = new StringFilter();
        }
        return licenseUrl;
    }

    public void setLicenseUrl(StringFilter licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public StringFilter getSourceCodeUrl() {
        return sourceCodeUrl;
    }

    public StringFilter sourceCodeUrl() {
        if (sourceCodeUrl == null) {
            sourceCodeUrl = new StringFilter();
        }
        return sourceCodeUrl;
    }

    public void setSourceCodeUrl(StringFilter sourceCodeUrl) {
        this.sourceCodeUrl = sourceCodeUrl;
    }

    public StringFilter getpUrl() {
        return pUrl;
    }

    public StringFilter pUrl() {
        if (pUrl == null) {
            pUrl = new StringFilter();
        }
        return pUrl;
    }

    public void setpUrl(StringFilter pUrl) {
        this.pUrl = pUrl;
    }

    public StringFilter getCopyright() {
        return copyright;
    }

    public StringFilter copyright() {
        if (copyright == null) {
            copyright = new StringFilter();
        }
        return copyright;
    }

    public void setCopyright(StringFilter copyright) {
        this.copyright = copyright;
    }

    public StringFilter getCompliance() {
        return compliance;
    }

    public StringFilter compliance() {
        if (compliance == null) {
            compliance = new StringFilter();
        }
        return compliance;
    }

    public void setCompliance(StringFilter compliance) {
        this.compliance = compliance;
    }

    public StringFilter getComplianceComment() {
        return complianceComment;
    }

    public StringFilter complianceComment() {
        if (complianceComment == null) {
            complianceComment = new StringFilter();
        }
        return complianceComment;
    }

    public void setComplianceComment(StringFilter complianceComment) {
        this.complianceComment = complianceComment;
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

    public BooleanFilter getReviewedDeepScan() {
        return reviewedDeepScan;
    }

    public BooleanFilter reviewedDeepScan() {
        if (reviewedDeepScan == null) {
            reviewedDeepScan = new BooleanFilter();
        }
        return reviewedDeepScan;
    }

    public void setReviewedDeepScan(BooleanFilter reviewedDeepScan) {
        this.reviewedDeepScan = reviewedDeepScan;
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

    public LocalDateFilter getLastReviewedDeepScanDate() {
        return lastReviewedDeepScanDate;
    }

    public LocalDateFilter lastReviewedDeepScanDate() {
        if (lastReviewedDeepScanDate == null) {
            lastReviewedDeepScanDate = new LocalDateFilter();
        }
        return lastReviewedDeepScanDate;
    }

    public void setLastReviewedDeepScanDate(LocalDateFilter lastReviewedDeepScanDate) {
        this.lastReviewedDeepScanDate = lastReviewedDeepScanDate;
    }

    public LocalDateFilter getCreatedDate() {
        return createdDate;
    }

    public LocalDateFilter createdDate() {
        if (createdDate == null) {
            createdDate = new LocalDateFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(LocalDateFilter createdDate) {
        this.createdDate = createdDate;
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

    public StringFilter getMd5() {
        return md5;
    }

    public StringFilter md5() {
        if (md5 == null) {
            md5 = new StringFilter();
        }
        return md5;
    }

    public void setMd5(StringFilter md5) {
        this.md5 = md5;
    }

    public StringFilter getSha1() {
        return sha1;
    }

    public StringFilter sha1() {
        if (sha1 == null) {
            sha1 = new StringFilter();
        }
        return sha1;
    }

    public void setSha1(StringFilter sha1) {
        this.sha1 = sha1;
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

    public LongFilter getErrorLogId() {
        return errorLogId;
    }

    public LongFilter errorLogId() {
        if (errorLogId == null) {
            errorLogId = new LongFilter();
        }
        return errorLogId;
    }

    public void setErrorLogId(LongFilter errorLogId) {
        this.errorLogId = errorLogId;
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

    public LongFilter getLastReviewedDeepScanById() {
        return lastReviewedDeepScanById;
    }

    public LongFilter lastReviewedDeepScanById() {
        if (lastReviewedDeepScanById == null) {
            lastReviewedDeepScanById = new LongFilter();
        }
        return lastReviewedDeepScanById;
    }

    public void setLastReviewedDeepScanById(LongFilter lastReviewedDeepScanById) {
        this.lastReviewedDeepScanById = lastReviewedDeepScanById;
    }

    public LongFilter getLicenseToPublishId() {
        return licenseToPublishId;
    }

    public LongFilter licenseToPublishId() {
        if (licenseToPublishId == null) {
            licenseToPublishId = new LongFilter();
        }
        return licenseToPublishId;
    }

    public void setLicenseToPublishId(LongFilter licenseToPublishId) {
        this.licenseToPublishId = licenseToPublishId;
    }

    public LongFilter getLicenseOfFilesId() {
        return licenseOfFilesId;
    }

    public LongFilter licenseOfFilesId() {
        if (licenseOfFilesId == null) {
            licenseOfFilesId = new LongFilter();
        }
        return licenseOfFilesId;
    }

    public void setLicenseOfFilesId(LongFilter licenseOfFilesId) {
        this.licenseOfFilesId = licenseOfFilesId;
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
        final LibraryCriteria that = (LibraryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(artifactId, that.artifactId) &&
            Objects.equals(version, that.version) &&
            Objects.equals(type, that.type) &&
            Objects.equals(originalLicense, that.originalLicense) &&
            Objects.equals(licenseUrl, that.licenseUrl) &&
            Objects.equals(sourceCodeUrl, that.sourceCodeUrl) &&
            Objects.equals(pUrl, that.pUrl) &&
            Objects.equals(copyright, that.copyright) &&
            Objects.equals(compliance, that.compliance) &&
            Objects.equals(complianceComment, that.complianceComment) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(reviewed, that.reviewed) &&
            Objects.equals(reviewedDeepScan, that.reviewedDeepScan) &&
            Objects.equals(lastReviewedDate, that.lastReviewedDate) &&
            Objects.equals(lastReviewedDeepScanDate, that.lastReviewedDeepScanDate) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(hideForPublishing, that.hideForPublishing) &&
            Objects.equals(md5, that.md5) &&
            Objects.equals(sha1, that.sha1) &&
            Objects.equals(licenseId, that.licenseId) &&
            Objects.equals(errorLogId, that.errorLogId) &&
            Objects.equals(lastReviewedById, that.lastReviewedById) &&
            Objects.equals(lastReviewedDeepScanById, that.lastReviewedDeepScanById) &&
            Objects.equals(licenseToPublishId, that.licenseToPublishId) &&
            Objects.equals(licenseOfFilesId, that.licenseOfFilesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            groupId,
            artifactId,
            version,
            type,
            originalLicense,
            licenseUrl,
            sourceCodeUrl,
            pUrl,
            copyright,
            compliance,
            complianceComment,
            comment,
            reviewed,
            reviewedDeepScan,
            lastReviewedDate,
            lastReviewedDeepScanDate,
            createdDate,
            hideForPublishing,
            md5,
            sha1,
            licenseId,
            errorLogId,
            lastReviewedById,
            lastReviewedDeepScanById,
            licenseToPublishId,
            licenseOfFilesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (artifactId != null ? "artifactId=" + artifactId + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (originalLicense != null ? "originalLicense=" + originalLicense + ", " : "") +
            (licenseUrl != null ? "licenseUrl=" + licenseUrl + ", " : "") +
            (sourceCodeUrl != null ? "sourceCodeUrl=" + sourceCodeUrl + ", " : "") +
            (pUrl != null ? "pUrl=" + pUrl + ", " : "") +
            (copyright != null ? "copyright=" + copyright + ", " : "") +
            (compliance != null ? "compliance=" + compliance + ", " : "") +
            (complianceComment != null ? "complianceComment=" + complianceComment + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (reviewed != null ? "reviewed=" + reviewed + ", " : "") +
            (reviewedDeepScan != null ? "reviewedDeepScan=" + reviewedDeepScan + ", " : "") +
            (lastReviewedDate != null ? "lastReviewedDate=" + lastReviewedDate + ", " : "") +
            (lastReviewedDeepScanDate != null ? "lastReviewedDeepScanDate=" + lastReviewedDeepScanDate + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (hideForPublishing != null ? "hideForPublishing=" + hideForPublishing + ", " : "") +
            (md5 != null ? "md5=" + md5 + ", " : "") +
            (sha1 != null ? "sha1=" + sha1 + ", " : "") +
            (licenseId != null ? "licenseId=" + licenseId + ", " : "") +
            (errorLogId != null ? "errorLogId=" + errorLogId + ", " : "") +
            (lastReviewedById != null ? "lastReviewedById=" + lastReviewedById + ", " : "") +
            (lastReviewedDeepScanById != null ? "lastReviewedDeepScanById=" + lastReviewedDeepScanById + ", " : "") +
            (licenseToPublishId != null ? "licenseToPublishId=" + licenseToPublishId + ", " : "") +
            (licenseOfFilesId != null ? "licenseOfFilesId=" + licenseOfFilesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
