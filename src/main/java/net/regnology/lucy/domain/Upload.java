package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import net.regnology.lucy.domain.enumeration.EntityUploadChoice;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Upload.
 */
@Entity
@Table(name = "upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Upload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Lob
    @Column(name = "file", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] file;

    @Column(name = "file_content_type", nullable = false)
    private String fileContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_to_upload")
    private EntityUploadChoice entityToUpload = EntityUploadChoice.LIBRARY;

    @Column(name = "record")
    private Integer record;

    @Column(name = "overwrite_data")
    private Boolean overwriteData = true;

    @Column(name = "uploaded_date")
    private LocalDate uploadedDate = LocalDate.now();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Upload id(Long id) {
        this.id = id;
        return this;
    }

    public byte[] getFile() {
        return this.file;
    }

    public Upload file(byte[] file) {
        this.file = file;
        return this;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return this.fileContentType;
    }

    public Upload fileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public EntityUploadChoice getEntityToUpload() {
        return this.entityToUpload;
    }

    public Upload entityToUpload(EntityUploadChoice entityToUpload) {
        this.entityToUpload = entityToUpload;
        return this;
    }

    public void setEntityToUpload(EntityUploadChoice entityToUpload) {
        this.entityToUpload = entityToUpload;
    }

    public Integer getRecord() {
        return this.record;
    }

    public Upload record(Integer record) {
        this.record = record;
        return this;
    }

    public void setRecord(Integer record) {
        this.record = record;
    }

    public Boolean getOverwriteData() {
        return this.overwriteData;
    }

    public Upload overwriteData(Boolean overwriteData) {
        this.overwriteData = overwriteData;
        return this;
    }

    public void setOverwriteData(Boolean overwriteData) {
        this.overwriteData = overwriteData;
    }

    public LocalDate getUploadedDate() {
        return this.uploadedDate;
    }

    public Upload uploadedDate(LocalDate uploadedDate) {
        this.uploadedDate = uploadedDate;
        return this;
    }

    public void setUploadedDate(LocalDate uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Upload)) {
            return false;
        }
        return id != null && id.equals(((Upload) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Upload{" +
            "id=" + getId() +
            ", fileContentType='" + getFileContentType() + "'" +
            ", entityToUpload='" + getEntityToUpload() + "'" +
            ", record=" + getRecord() +
            ", overwriteData='" + getOverwriteData() + "'" +
            ", uploadedDate='" + getUploadedDate() + "'" +
            "}";
    }
}
