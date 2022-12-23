package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LicenseRiskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LicenseRisk.class);
        LicenseRisk licenseRisk1 = new LicenseRisk();
        licenseRisk1.setId(1L);
        LicenseRisk licenseRisk2 = new LicenseRisk();
        licenseRisk2.setId(licenseRisk1.getId());
        assertThat(licenseRisk1).isEqualTo(licenseRisk2);
        licenseRisk2.setId(2L);
        assertThat(licenseRisk1).isNotEqualTo(licenseRisk2);
        licenseRisk1.setId(null);
        assertThat(licenseRisk1).isNotEqualTo(licenseRisk2);
    }
}
