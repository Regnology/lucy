package net.regnology.lucy.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import net.regnology.lucy.domain.enumeration.FossologyStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Fossology.
 */
@Entity
@Table(name = "fossology")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Fossology implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class Config {

        private boolean enabled;

        private String url;

        public Config(boolean enabled, String url) {
            this.enabled = enabled;
            this.url = url;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FossologyStatus status = FossologyStatus.NOT_STARTED;

    @Size(max = 64)
    @Column(name = "upload_id", length = 64)
    private String uploadId;

    @Size(max = 64)
    @Column(name = "job_id", length = 64)
    private String jobId;

    @Column(name = "last_scan")
    private Instant lastScan = Instant.now();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fossology id(Long id) {
        this.id = id;
        return this;
    }

    public FossologyStatus getStatus() {
        return this.status;
    }

    public Fossology status(FossologyStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(FossologyStatus status) {
        this.status = status;
    }

    public String getUploadId() {
        return this.uploadId;
    }

    public Fossology uploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getJobId() {
        return this.jobId;
    }

    public Fossology jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Instant getLastScan() {
        return this.lastScan;
    }

    public Fossology lastScan(Instant lastScan) {
        this.lastScan = lastScan;
        return this;
    }

    public void setLastScan(Instant lastScan) {
        this.lastScan = lastScan;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fossology)) {
            return false;
        }
        return id != null && id.equals(((Fossology) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Fossology{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", uploadId='" + getUploadId() + "'" +
            ", jobId='" + getJobId() + "'" +
            ", lastScan='" + getLastScan() + "'" +
            "}";
    }
}
