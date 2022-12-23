package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LicensePerLibraryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LicensePerLibrary.class);
        LicensePerLibrary licensePerLibrary1 = new LicensePerLibrary();
        licensePerLibrary1.setId(1L);
        LicensePerLibrary licensePerLibrary2 = new LicensePerLibrary();
        licensePerLibrary2.setId(licensePerLibrary1.getId());
        assertThat(licensePerLibrary1).isEqualTo(licensePerLibrary2);
        licensePerLibrary2.setId(2L);
        assertThat(licensePerLibrary1).isNotEqualTo(licensePerLibrary2);
        licensePerLibrary1.setId(null);
        assertThat(licensePerLibrary1).isNotEqualTo(licensePerLibrary2);
    }
}
