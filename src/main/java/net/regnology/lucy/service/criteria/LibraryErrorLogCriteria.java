package net.regnology.lucy.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.domain.enumeration.LogSeverity;
import net.regnology.lucy.domain.enumeration.LogStatus;
import net.regnology.lucy.web.rest.LibraryErrorLogResource;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link LibraryErrorLog} entity. This class is used
 * in {@link LibraryErrorLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /library-error-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class LibraryErrorLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LogSeverity
     */
    public static class LogSeverityFilter extends Filter<LogSeverity> {

        public LogSeverityFilter() {}

        public LogSeverityFilter(LogSeverityFilter filter) {
            super(filter);
        }

        @Override
        public LogSeverityFilter copy() {
            return new LogSeverityFilter(this);
        }
    }

    /**
     * Class for filtering LogStatus
     */
    public static class LogStatusFilter extends Filter<LogStatus> {

        public LogStatusFilter() {}

        public LogStatusFilter(LogStatusFilter filter) {
            super(filter);
        }

        @Override
        public LogStatusFilter copy() {
            return new LogStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter message;

    private LogSeverityFilter severity;

    private LogStatusFilter status;

    private InstantFilter timestamp;

    private LongFilter libraryId;

    private Boolean distinct;

    public LibraryErrorLogCriteria() {}

    public LibraryErrorLogCriteria(LibraryErrorLogCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.message = other.message == null ? null : other.message.copy();
        this.severity = other.severity == null ? null : other.severity.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.timestamp = other.timestamp == null ? null : other.timestamp.copy();
        this.libraryId = other.libraryId == null ? null : other.libraryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LibraryErrorLogCriteria copy() {
        return new LibraryErrorLogCriteria(this);
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

    public StringFilter getMessage() {
        return message;
    }

    public StringFilter message() {
        if (message == null) {
            message = new StringFilter();
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public LogSeverityFilter getSeverity() {
        return severity;
    }

    public LogSeverityFilter severity() {
        if (severity == null) {
            severity = new LogSeverityFilter();
        }
        return severity;
    }

    public void setSeverity(LogSeverityFilter severity) {
        this.severity = severity;
    }

    public LogStatusFilter getStatus() {
        return status;
    }

    public LogStatusFilter status() {
        if (status == null) {
            status = new LogStatusFilter();
        }
        return status;
    }

    public void setStatus(LogStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            timestamp = new InstantFilter();
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
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
        final LibraryErrorLogCriteria that = (LibraryErrorLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(message, that.message) &&
            Objects.equals(severity, that.severity) &&
            Objects.equals(status, that.status) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(libraryId, that.libraryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, severity, status, timestamp, libraryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryErrorLogCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (message != null ? "message=" + message + ", " : "") +
            (severity != null ? "severity=" + severity + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (timestamp != null ? "timestamp=" + timestamp + ", " : "") +
            (libraryId != null ? "libraryId=" + libraryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
