package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LicenseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(License.class);
        License license1 = new License();
        license1.setId(1L);
        License license2 = new License();
        license2.setId(license1.getId());
        assertThat(license1).isEqualTo(license2);
        license2.setId(2L);
        assertThat(license1).isNotEqualTo(license2);
        license1.setId(null);
        assertThat(license1).isNotEqualTo(license2);
    }
}
