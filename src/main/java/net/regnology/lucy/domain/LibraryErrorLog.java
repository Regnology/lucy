package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.enumeration.LogSeverity;
import net.regnology.lucy.domain.enumeration.LogStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LibraryErrorLog.
 */
@Entity
@Table(name = "library_error_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LibraryErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private LogSeverity severity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LogStatus status;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp = Instant.now();

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "fossology", "linkedLicenses", "errorLogs", "lastReviewedBy", "licenses", "licenseToPublishes", "licenseOfFiles" },
        allowSetters = true
    )
    private Library library;

    public LibraryErrorLog() {}

    public LibraryErrorLog(String message, LogSeverity severity, LogStatus status) {
        this.message = message;
        this.severity = severity;
        this.status = status;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LibraryErrorLog id(Long id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public LibraryErrorLog message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogSeverity getSeverity() {
        return this.severity;
    }

    public LibraryErrorLog severity(LogSeverity severity) {
        this.severity = severity;
        return this;
    }

    public void setSeverity(LogSeverity severity) {
        this.severity = severity;
    }

    public LogStatus getStatus() {
        return this.status;
    }

    public LibraryErrorLog status(LogStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public LibraryErrorLog timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Library getLibrary() {
        return this.library;
    }

    public LibraryErrorLog library(Library library) {
        this.setLibrary(library);
        return this;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LibraryErrorLog)) {
            return false;
        }
        return id != null && id.equals(((LibraryErrorLog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryErrorLog{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", status='" + getStatus() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }
}
