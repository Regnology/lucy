package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FossologyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Fossology.class);
        Fossology fossology1 = new Fossology();
        fossology1.setId(1L);
        Fossology fossology2 = new Fossology();
        fossology2.setId(fossology1.getId());
        assertThat(fossology1).isEqualTo(fossology2);
        fossology2.setId(2L);
        assertThat(fossology1).isNotEqualTo(fossology2);
        fossology1.setId(null);
        assertThat(fossology1).isNotEqualTo(fossology2);
    }
}
