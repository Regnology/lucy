package net.regnology.lucy.service.dto;

import java.io.Serializable;
import net.regnology.lucy.domain.License;

/**
 * A DTO representing a {@link License} entity with the minimum of information.
 */
public class LicenseSimpleDTO implements Serializable {

    private Long id;

    private String fullName;

    private String shortIdentifier;

    public LicenseSimpleDTO(Long id, String fullName, String shortIdentifier) {
        this.id = id;
        this.fullName = fullName;
        this.shortIdentifier = shortIdentifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortIdentifier() {
        return shortIdentifier;
    }

    public void setShortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
    }

    @Override
    public String toString() {
        return "LicenseSimpleDTO{" + "id=" + id + ", fullName='" + fullName + '\'' + ", shortIdentifier='" + shortIdentifier + '\'' + '}';
    }
}
