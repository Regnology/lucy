package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LicenseConflictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LicenseConflict.class);
        LicenseConflict licenseConflict1 = new LicenseConflict();
        licenseConflict1.setId(1L);
        LicenseConflict licenseConflict2 = new LicenseConflict();
        licenseConflict2.setId(licenseConflict1.getId());
        assertThat(licenseConflict1).isEqualTo(licenseConflict2);
        licenseConflict2.setId(2L);
        assertThat(licenseConflict1).isNotEqualTo(licenseConflict2);
        licenseConflict1.setId(null);
        assertThat(licenseConflict1).isNotEqualTo(licenseConflict2);
    }
}
