package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LicenseNamingMappingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LicenseNamingMapping.class);
        LicenseNamingMapping licenseNamingMapping1 = new LicenseNamingMapping();
        licenseNamingMapping1.setId(1L);
        LicenseNamingMapping licenseNamingMapping2 = new LicenseNamingMapping();
        licenseNamingMapping2.setId(licenseNamingMapping1.getId());
        assertThat(licenseNamingMapping1).isEqualTo(licenseNamingMapping2);
        licenseNamingMapping2.setId(2L);
        assertThat(licenseNamingMapping1).isNotEqualTo(licenseNamingMapping2);
        licenseNamingMapping1.setId(null);
        assertThat(licenseNamingMapping1).isNotEqualTo(licenseNamingMapping2);
    }
}
