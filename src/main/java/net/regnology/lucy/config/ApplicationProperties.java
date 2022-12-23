package net.regnology.lucy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Lucy.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

    private final ApplicationProperties.Lucy lucy = new ApplicationProperties.Lucy();
    private final ApplicationProperties.Logging logging = new ApplicationProperties.Logging();
    private final ApplicationProperties.Mail mail = new ApplicationProperties.Mail();
    private final ApplicationProperties.SourceCodeArchive sourceCodeArchive = new ApplicationProperties.SourceCodeArchive();
    private final ApplicationProperties.Fossology fossology = new ApplicationProperties.Fossology();

    private final ApplicationProperties.Upload upload = new ApplicationProperties.Upload();

    public ApplicationProperties() {}

    public Lucy getLucy() {
        return lucy;
    }

    public Logging getLogging() {
        return logging;
    }

    public Mail getMail() {
        return mail;
    }

    public SourceCodeArchive getSourceCodeArchive() {
        return sourceCodeArchive;
    }

    public Fossology getFossology() {
        return fossology;
    }

    public Upload getUpload() {
        return upload;
    }

    public static class Lucy {

        private String domain;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }

    public static class Logging {

        private String path;
        private String file;
        private String archiveFile;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getArchiveFile() {
            return archiveFile;
        }

        public void setArchiveFile(String archiveFile) {
            this.archiveFile = archiveFile;
        }
    }

    public static class Mail {

        private boolean restrictToDomain = false;
        private String allowedDomain;

        public boolean isRestrictToDomain() {
            return restrictToDomain;
        }

        public void setRestrictToDomain(boolean restrictToDomain) {
            this.restrictToDomain = restrictToDomain;
        }

        public String getAllowedDomain() {
            return allowedDomain;
        }

        public void setAllowedDomain(String allowedDomain) {
            this.allowedDomain = allowedDomain;
        }
    }

    public static class SourceCodeArchive {

        private boolean active;
        private String remoteIndex;
        private String remoteArchive;
        private String uploadPlatform;
        private String uploadUser;
        private String uploadPassword;

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getRemoteIndex() {
            return remoteIndex;
        }

        public void setRemoteIndex(String remoteIndex) {
            this.remoteIndex = remoteIndex;
        }

        public String getRemoteArchive() {
            return remoteArchive;
        }

        public void setRemoteArchive(String remoteArchive) {
            this.remoteArchive = remoteArchive;
        }

        public String getUploadPlatform() {
            return uploadPlatform;
        }

        public void setUploadPlatform(String uploadPlatform) {
            this.uploadPlatform = uploadPlatform;
        }

        public String getUploadUser() {
            return uploadUser;
        }

        public void setUploadUser(String uploadUser) {
            this.uploadUser = uploadUser;
        }

        public String getUploadPassword() {
            return uploadPassword;
        }

        public void setUploadPassword(String uploadPassword) {
            this.uploadPassword = uploadPassword;
        }
    }

    public static class Fossology {

        private boolean enabled;
        private String url;
        private String username;
        private String token;
        private String folder;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }
    }

    public static class Upload {

        private int limit;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }
}
