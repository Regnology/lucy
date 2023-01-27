package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.web.rest.LibraryPerProductResource;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Custom criteria class for the {@link LibraryPerProduct} entity. This class is used
 * in {@link LibraryPerProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /library-per-products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LibraryPerProductCustomCriteria extends LibraryPerProductCriteria {

    private static final long serialVersionUID = 1L;

    private StringFilter artifactId;

    private StringFilter licensesShortIdentifier;

    private LongFilter libraryRiskId;

    private StringFilter errorLogMessage;

    private LibraryErrorLogCriteria.LogStatusFilter errorLogStatus;

    private LocalDateFilter libraryCreatedDate;

    public LibraryPerProductCustomCriteria() {
        super();
    }

    public LibraryPerProductCustomCriteria(LibraryPerProductCustomCriteria other) {
        super(other);
        this.artifactId = other.artifactId == null ? null : other.artifactId.copy();
        this.licensesShortIdentifier = other.licensesShortIdentifier == null ? null : other.licensesShortIdentifier.copy();
        this.libraryRiskId = other.libraryRiskId == null ? null : other.libraryRiskId.copy();
        this.errorLogMessage = other.errorLogMessage == null ? null : other.errorLogMessage.copy();
        this.errorLogStatus = other.errorLogStatus == null ? null : other.errorLogStatus.copy();
        this.libraryCreatedDate = other.libraryCreatedDate == null ? null : other.libraryCreatedDate.copy();
    }

    @Override
    public LibraryPerProductCustomCriteria copy() {
        return new LibraryPerProductCustomCriteria(this);
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

    public StringFilter getLicensesShortIdentifier() {
        return licensesShortIdentifier;
    }

    public StringFilter licensesShortIdentifier() {
        if (licensesShortIdentifier == null) {
            licensesShortIdentifier = new StringFilter();
        }
        return licensesShortIdentifier;
    }

    public void setLicensesShortIdentifier(StringFilter licensesShortIdentifier) {
        this.licensesShortIdentifier = licensesShortIdentifier;
    }

    public LongFilter getLibraryRiskId() {
        return libraryRiskId;
    }

    public LongFilter licenseRiskId() {
        if (libraryRiskId == null) {
            libraryRiskId = new LongFilter();
        }
        return libraryRiskId;
    }

    public void setLibraryRiskId(LongFilter libraryRiskId) {
        this.libraryRiskId = libraryRiskId;
    }

    public StringFilter getErrorLogMessage() {
        return errorLogMessage;
    }

    public StringFilter errorLogMessage() {
        if (errorLogMessage == null) {
            errorLogMessage = new StringFilter();
        }
        return errorLogMessage;
    }

    public void setErrorLogMessage(StringFilter errorLogMessage) {
        this.errorLogMessage = errorLogMessage;
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

    public LocalDateFilter getLibraryCreatedDate() {
        return libraryCreatedDate;
    }

    public LocalDateFilter libraryCreatedDate() {
        if (libraryCreatedDate == null) {
            libraryCreatedDate = new LocalDateFilter();
        }
        return libraryCreatedDate;
    }

    public void setLibraryCreatedDate(LocalDateFilter libraryCreatedDate) {
        this.libraryCreatedDate = libraryCreatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LibraryPerProductCustomCriteria that = (LibraryPerProductCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getAddedDate(), that.getAddedDate()) &&
            Objects.equals(getAddedManually(), that.getAddedManually()) &&
            Objects.equals(getHideForPublishing(), that.getHideForPublishing()) &&
            Objects.equals(getComment(), that.getComment()) &&
            Objects.equals(getLibraryId(), that.getLibraryId()) &&
            Objects.equals(getProductId(), that.getProductId()) &&
            Objects.equals(getArtifactId(), that.getArtifactId()) &&
            Objects.equals(getLicensesShortIdentifier(), that.getLicensesShortIdentifier()) &&
            Objects.equals(getLibraryRiskId(), that.getLibraryRiskId()) &&
            Objects.equals(getErrorLogMessage(), that.getErrorLogMessage()) &&
            Objects.equals(getErrorLogStatus(), that.getErrorLogStatus()) &&
            Objects.equals(getLibraryCreatedDate(), that.getLibraryCreatedDate()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getId(),
            getAddedDate(),
            getAddedManually(),
            getHideForPublishing(),
            getComment(),
            getLibraryId(),
            getProductId(),
            getArtifactId(),
            getLicensesShortIdentifier(),
            getLibraryRiskId(),
            getErrorLogMessage(),
            getErrorLogStatus(),
            getLibraryCreatedDate(),
            getDistinct()
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryPerProductCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getAddedDate() != null ? "addedDate=" + getAddedDate() + ", " : "") +
            (getAddedManually() != null ? "addedManually=" + getAddedManually() + ", " : "") +
            (getHideForPublishing() != null ? "hideForPublishing=" + getHideForPublishing() + ", " : "") +
            (getComment() != null ? "comment=" + getComment() + ", " : "") +
            (getLibraryId() != null ? "libraryId=" + getLibraryId() + ", " : "") +
            (getProductId() != null ? "productId=" + getProductId() + ", " : "") +
            (getArtifactId() != null ? "artifactId=" + getArtifactId() + ", " : "") +
            (getLicensesShortIdentifier() != null ? "licenseId=" + getLicensesShortIdentifier() + ", " : "") +
            (getLibraryRiskId() != null ? "libraryRiskId=" + getLibraryRiskId() + ", " : "") +
            (getErrorLogMessage() != null ? "errorLogMessage=" + getErrorLogMessage() + ", " : "") +
            (getErrorLogStatus() != null ? "errorLogStatus=" + getErrorLogStatus() + ", " : "") +
            (getLibraryCreatedDate() != null ? "libraryCreatedDate=" + getLibraryCreatedDate() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
