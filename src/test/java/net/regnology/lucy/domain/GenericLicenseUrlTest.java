package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenericLicenseUrlTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenericLicenseUrl.class);
        GenericLicenseUrl genericLicenseUrl1 = new GenericLicenseUrl();
        genericLicenseUrl1.setId(1L);
        GenericLicenseUrl genericLicenseUrl2 = new GenericLicenseUrl();
        genericLicenseUrl2.setId(genericLicenseUrl1.getId());
        assertThat(genericLicenseUrl1).isEqualTo(genericLicenseUrl2);
        genericLicenseUrl2.setId(2L);
        assertThat(genericLicenseUrl1).isNotEqualTo(genericLicenseUrl2);
        genericLicenseUrl1.setId(null);
        assertThat(genericLicenseUrl1).isNotEqualTo(genericLicenseUrl2);
    }
}
