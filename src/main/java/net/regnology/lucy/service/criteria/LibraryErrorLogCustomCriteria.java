package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.web.rest.LibraryErrorLogResource;
import tech.jhipster.service.filter.Filter;

/**
 * Custom criteria class for the {@link LibraryErrorLog} entity. This class is used
 * in {@link LibraryErrorLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /library-error-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LibraryErrorLogCustomCriteria extends LibraryErrorLogCriteria {

    private static final long serialVersionUID = 1L;

    public LibraryErrorLogCustomCriteria() {
        super();
    }

    public LibraryErrorLogCustomCriteria(LibraryErrorLogCustomCriteria other) {
        super(other);
    }

    @Override
    public LibraryErrorLogCustomCriteria copy() {
        return new LibraryErrorLogCustomCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LibraryErrorLogCustomCriteria that = (LibraryErrorLogCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getMessage(), that.getMessage()) &&
            Objects.equals(getSeverity(), that.getSeverity()) &&
            Objects.equals(getStatus(), that.getStatus()) &&
            Objects.equals(getTimestamp(), that.getTimestamp()) &&
            Objects.equals(getLibraryId(), that.getLibraryId()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMessage(), getSeverity(), getStatus(), getTimestamp(), getLibraryId(), getDistinct());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryErrorLogCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getMessage() != null ? "message=" + getMessage() + ", " : "") +
            (getSeverity() != null ? "severity=" + getSeverity() + ", " : "") +
            (getStatus() != null ? "status=" + getStatus() + ", " : "") +
            (getTimestamp() != null ? "timestamp=" + getTimestamp() + ", " : "") +
            (getLibraryId() != null ? "libraryId=" + getLibraryId() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
