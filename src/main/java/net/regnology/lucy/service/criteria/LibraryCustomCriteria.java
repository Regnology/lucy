package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.web.rest.LibraryResource;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Custom criteria class for the {@link Library} entity. This class is used
 * in {@link LibraryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /libraries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LibraryCustomCriteria extends LibraryCriteria {

    private static final long serialVersionUID = 1L;

    private StringFilter linkedLicenseShortIdentifier;

    private LibraryErrorLogCriteria.LogStatusFilter errorLogStatus;

    private StringFilter licenseToPublishShortIdentifier;

    public LibraryCustomCriteria() {
        super();
    }

    public LibraryCustomCriteria(LibraryCustomCriteria other) {
        super(other);
        this.linkedLicenseShortIdentifier = other.linkedLicenseShortIdentifier == null ? null : other.linkedLicenseShortIdentifier.copy();
        this.errorLogStatus = other.errorLogStatus == null ? null : other.errorLogStatus.copy();
        this.licenseToPublishShortIdentifier =
            other.licenseToPublishShortIdentifier == null ? null : other.licenseToPublishShortIdentifier.copy();
    }

    @Override
    public LibraryCustomCriteria copy() {
        return new LibraryCustomCriteria(this);
    }

    public StringFilter getLinkedLicenseShortIdentifier() {
        return linkedLicenseShortIdentifier;
    }

    public StringFilter linkedLicenseShortIdentifier() {
        if (linkedLicenseShortIdentifier == null) {
            linkedLicenseShortIdentifier = new StringFilter();
        }
        return linkedLicenseShortIdentifier;
    }

    public void setLinkedLicenseShortIdentifier(StringFilter linkedLicenseShortIdentifier) {
        this.linkedLicenseShortIdentifier = linkedLicenseShortIdentifier;
    }

    public LibraryErrorLogCriteria.LogStatusFilter getErrorLogStatus() {
        return errorLogStatus;
    }

    public LibraryErrorLogCriteria.LogStatusFilter errorLogStatus() {
        if (errorLogStatus == null) {
            errorLogStatus = new LibraryErrorLogCriteria.LogStatusFilter();
        }
        return errorLogStatus;
    }

    public void setErrorLogStatus(LibraryErrorLogCriteria.LogStatusFilter errorLogStatus) {
        this.errorLogStatus = errorLogStatus;
    }

    public StringFilter getLicenseToPublishShortIdentifier() {
        return licenseToPublishShortIdentifier;
    }

    public StringFilter licenseToPublishShortIdentifier() {
        if (licenseToPublishShortIdentifier == null) {
            licenseToPublishShortIdentifier = new StringFilter();
        }
        return licenseToPublishShortIdentifier;
    }

    public void setLicenseToPublishShortIdentifier(StringFilter licenseToPublishShortIdentifier) {
        this.licenseToPublishShortIdentifier = licenseToPublishShortIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LibraryCustomCriteria that = (LibraryCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getGroupId(), that.getGroupId()) &&
            Objects.equals(getArtifactId(), that.getArtifactId()) &&
            Objects.equals(getVersion(), that.getVersion()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(getOriginalLicense(), that.getOriginalLicense()) &&
            Objects.equals(getLicenseUrl(), that.getLicenseUrl()) &&
            Objects.equals(getSourceCodeUrl(), that.getSourceCodeUrl()) &&
            Objects.equals(getpUrl(), that.getpUrl()) &&
            Objects.equals(getCopyright(), that.getCopyright()) &&
            Objects.equals(getCompliance(), that.getCompliance()) &&
            Objects.equals(getComplianceComment(), that.getComplianceComment()) &&
            Objects.equals(getComment(), that.getComment()) &&
            Objects.equals(getReviewed(), that.getReviewed()) &&
            Objects.equals(getReviewedDeepScan(), that.getReviewedDeepScan()) &&
            Objects.equals(getLastReviewedDate(), that.getLastReviewedDate()) &&
            Objects.equals(getLastReviewedDeepScanDate(), that.getLastReviewedDeepScanDate()) &&
            Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
            Objects.equals(getHideForPublishing(), that.getHideForPublishing()) &&
            Objects.equals(getMd5(), that.getMd5()) &&
            Objects.equals(getSha1(), that.getSha1()) &&
            Objects.equals(getLinkedLicenseShortIdentifier(), that.getLinkedLicenseShortIdentifier()) &&
            Objects.equals(getErrorLogId(), that.getErrorLogId()) &&
            Objects.equals(getLastReviewedById(), that.getLastReviewedById()) &&
            Objects.equals(getLastReviewedDeepScanById(), that.getLastReviewedDeepScanById()) &&
            Objects.equals(getLicenseToPublishShortIdentifier(), that.getLicenseToPublishShortIdentifier()) &&
            Objects.equals(getLicenseOfFilesId(), that.getLicenseOfFilesId()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getId(),
            getGroupId(),
            getArtifactId(),
            getVersion(),
            getType(),
            getOriginalLicense(),
            getLicenseUrl(),
            getSourceCodeUrl(),
            getpUrl(),
            getCopyright(),
            getCompliance(),
            getComplianceComment(),
            getComment(),
            getReviewed(),
            getReviewedDeepScan(),
            getLastReviewedDate(),
            getLastReviewedDeepScanDate(),
            getCreatedDate(),
            getHideForPublishing(),
            getMd5(),
            getSha1(),
            getLinkedLicenseShortIdentifier(),
            getErrorLogId(),
            getErrorLogStatus(),
            getLastReviewedById(),
            getLastReviewedDeepScanById(),
            getLicenseToPublishShortIdentifier(),
            getLicenseOfFilesId(),
            getDistinct()
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getGroupId() != null ? "groupId=" + getGroupId() + ", " : "") +
            (getArtifactId() != null ? "artifactId=" + getArtifactId() + ", " : "") +
            (getVersion() != null ? "version=" + getVersion() + ", " : "") +
            (getType() != null ? "type=" + getType() + ", " : "") +
            (getOriginalLicense() != null ? "originalLicense=" + getOriginalLicense() + ", " : "") +
            (getLicenseUrl() != null ? "licenseUrl=" + getLicenseUrl() + ", " : "") +
            (getSourceCodeUrl() != null ? "sourceCodeUrl=" + getSourceCodeUrl() + ", " : "") +
            (getpUrl() != null ? "pUrl=" + getpUrl() + ", " : "") +
            (getCopyright() != null ? "copyright=" + getCopyright() + ", " : "") +
            (getCompliance() != null ? "compliance=" + getCompliance() + ", " : "") +
            (getComplianceComment() != null ? "complianceComment=" + getComplianceComment() + ", " : "") +
            (getComment() != null ? "comment=" + getComment() + ", " : "") +
            (getReviewed() != null ? "reviewed=" + getReviewed() + ", " : "") +
            (getReviewedDeepScan() != null ? "reviewedDeepScan=" + getReviewedDeepScan() + ", " : "") +
            (getLastReviewedDate() != null ? "lastReviewedDate=" + getLastReviewedDate() + ", " : "") +
            (getLastReviewedDeepScanDate() != null ? "lastReviewedDeepScanDate=" + getLastReviewedDeepScanDate() + ", " : "") +
            (getCreatedDate() != null ? "createdDate=" + getCreatedDate() + ", " : "") +
            (getHideForPublishing() != null ? "hideForPublishing=" + getHideForPublishing() + ", " : "") +
            (getMd5() != null ? "md5=" + getMd5() + ", " : "") +
            (getSha1() != null ? "sha1=" + getSha1() + ", " : "") +
            (getLinkedLicenseShortIdentifier() != null ? "licenseId=" + getLinkedLicenseShortIdentifier() + ", " : "") +
            (getErrorLogId() != null ? "errorLogId=" + getErrorLogId() + ", " : "") +
            (getErrorLogStatus() != null ? "errorLogStatus=" + getErrorLogStatus() + ", " : "") +
            (getLastReviewedById() != null ? "lastReviewedById=" + getLastReviewedById() + ", " : "") +
            (getLastReviewedDeepScanById() != null ? "lastReviewedDeepScanById=" + getLastReviewedDeepScanById() + ", " : "") +
            (getLicenseToPublishShortIdentifier() != null ? "licenseToPublishId=" + getLicenseToPublishShortIdentifier() + ", " : "") +
            (getLicenseOfFilesId() != null ? "licenseOfFilesId=" + getLicenseOfFilesId() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
