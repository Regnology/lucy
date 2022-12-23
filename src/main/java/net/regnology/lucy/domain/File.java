package net.regnology.lucy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.InputStream;
import java.io.Serializable;

/**
 * A File.
 */
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] file;
    private String fileContentType;

    @JsonIgnore
    private InputStream filestream;

    public File() {}

    public File(String fileName, byte[] file, String fileContentType) {
        this.fileName = fileName;
        this.file = file;
        this.fileContentType = fileContentType;
    }

    public File(String fileName, InputStream filestream, String fileContentType) {
        this.fileName = fileName;
        this.filestream = filestream;
        this.fileContentType = fileContentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public InputStream getFilestream() {
        return filestream;
    }

    public void setFilestream(InputStream filestream) {
        this.filestream = filestream;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "File{" +
            "fileName='" + getFileName() + "'" +
            ", fileContentType='" + getFileContentType() + "'" +
            "}";
    }
}
