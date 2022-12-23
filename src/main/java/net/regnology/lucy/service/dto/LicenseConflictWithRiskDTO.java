package net.regnology.lucy.service.dto;

import java.io.Serializable;
import net.regnology.lucy.domain.LicenseConflict;
import net.regnology.lucy.domain.LicenseRisk;
import net.regnology.lucy.domain.enumeration.CompatibilityState;

/**
 * A DTO representing a {@link LicenseConflict} entity with the minimum of information and the license risk.
 */
public class LicenseConflictWithRiskDTO extends LicenseConflictSimpleDTO implements Serializable {

    public LicenseConflictWithRiskDTO(
        Long id,
        Long licenseId,
        String fullName,
        String shortIdentifier,
        CompatibilityState compatibilityState,
        String comment,
        LicenseRisk licenseRisk
    ) {
        super(id, licenseId, fullName, shortIdentifier, compatibilityState, comment);
        this.setSecondLicenseConflict(new LicenseWithRiskDTO(licenseId, fullName, shortIdentifier, licenseRisk));
    }

    @Override
    public String toString() {
        return (
            "LicenseConflictSimpleDTO{" +
            "id=" +
            getId() +
            ", secondLicenseConflictId=" +
            getSecondLicenseConflict().getId() +
            ", compatibility=" +
            getCompatibility() +
            ", comment='" +
            getComment() +
            '\'' +
            '}'
        );
    }
}
