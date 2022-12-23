package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequirementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Requirement.class);
        Requirement requirement1 = new Requirement();
        requirement1.setId(1L);
        Requirement requirement2 = new Requirement();
        requirement2.setId(requirement1.getId());
        assertThat(requirement1).isEqualTo(requirement2);
        requirement2.setId(2L);
        assertThat(requirement1).isNotEqualTo(requirement2);
        requirement1.setId(null);
        assertThat(requirement1).isNotEqualTo(requirement2);
    }
}
