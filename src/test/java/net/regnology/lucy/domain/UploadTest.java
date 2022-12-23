package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UploadTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Upload.class);
        Upload upload1 = new Upload();
        upload1.setId(1L);
        Upload upload2 = new Upload();
        upload2.setId(upload1.getId());
        assertThat(upload1).isEqualTo(upload2);
        upload2.setId(2L);
        assertThat(upload1).isNotEqualTo(upload2);
        upload1.setId(null);
        assertThat(upload1).isNotEqualTo(upload2);
    }
}
