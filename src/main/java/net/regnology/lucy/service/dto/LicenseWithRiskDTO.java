package net.regnology.lucy.service.dto;

import java.io.Serializable;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.LicenseRisk;

/**
 * A DTO representing a {@link License} entity with the minimum of information and license risk.
 */
public class LicenseWithRiskDTO extends LicenseSimpleDTO implements Serializable {

    private LicenseRisk licenseRisk;

    public LicenseWithRiskDTO(Long id, String fullName, String shortIdentifier, LicenseRisk licenseRisk) {
        super(id, fullName, shortIdentifier);
        this.licenseRisk = licenseRisk;
    }

    public LicenseRisk getLicenseRisk() {
        return licenseRisk;
    }

    public void setLicenseRisk(LicenseRisk licenseRisk) {
        this.licenseRisk = licenseRisk;
    }

    @Override
    public String toString() {
        return (
            "LicenseSimpleDTO{" +
            "id=" +
            getId() +
            ", fullName='" +
            getFullName() +
            '\'' +
            ", shortIdentifier='" +
            getShortIdentifier() +
            '\'' +
            '}'
        );
    }
}
