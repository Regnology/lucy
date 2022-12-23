package net.regnology.lucy.domain.helper;

import java.util.Objects;

public class PackageInfo {

    private String fileName;
    private String md5Hash;
    private String sha1Hash;

    public PackageInfo(String fileName, String md5Hash, String sha1Hash) {
        this.fileName = fileName;
        this.md5Hash = md5Hash;
        this.sha1Hash = sha1Hash;
    }

    @Override
    public String toString() {
        return "PackageInfo{" + "fileName='" + fileName + "'" + ", md5='" + md5Hash + "'" + ", sha1='" + sha1Hash + "'" + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageInfo that = (PackageInfo) o;
        return md5Hash.equals(that.getMd5Hash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(md5Hash);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public String getSha1Hash() {
        return sha1Hash;
    }

    public void setSha1Hash(String sha1Hash) {
        this.sha1Hash = sha1Hash;
    }
}
