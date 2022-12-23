package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibraryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Library.class);
        Library library1 = new Library();
        library1.setId(1L);
        Library library2 = new Library();
        library2.setId(library1.getId());
        assertThat(library1).isEqualTo(library2);
        library2.setId(2L);
        assertThat(library1).isNotEqualTo(library2);
        library1.setId(null);
        assertThat(library1).isNotEqualTo(library2);
    }
}
