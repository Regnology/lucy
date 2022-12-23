package net.regnology.lucy.service.dto;

import java.io.Serializable;
import net.regnology.lucy.domain.LicenseConflict;
import net.regnology.lucy.domain.enumeration.CompatibilityState;

/**
 * A DTO representing a {@link LicenseConflict} entity with the minimum of information.
 */
public class LicenseConflictSimpleDTO implements Serializable {

    private Long id;

    private LicenseSimpleDTO secondLicenseConflict;

    private CompatibilityState compatibility;

    private String comment;

    public LicenseConflictSimpleDTO(
        Long id,
        Long licenseId,
        String fullName,
        String shortIdentifier,
        CompatibilityState compatibilityState,
        String comment
    ) {
        this.id = id;
        this.secondLicenseConflict = new LicenseSimpleDTO(licenseId, fullName, shortIdentifier);
        this.compatibility = compatibilityState;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LicenseSimpleDTO getSecondLicenseConflict() {
        return secondLicenseConflict;
    }

    public void setSecondLicenseConflict(LicenseSimpleDTO secondLicenseConflict) {
        this.secondLicenseConflict = secondLicenseConflict;
    }

    public CompatibilityState getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(CompatibilityState compatibility) {
        this.compatibility = compatibility;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return (
            "LicenseConflictSimpleDTO{" +
            "id=" +
            id +
            ", secondLicenseConflictId=" +
            secondLicenseConflict.getId() +
            ", compatibility=" +
            compatibility +
            ", comment='" +
            comment +
            '\'' +
            '}'
        );
    }
}
