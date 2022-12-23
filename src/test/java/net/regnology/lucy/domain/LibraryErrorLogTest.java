package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibraryErrorLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibraryErrorLog.class);
        LibraryErrorLog libraryErrorLog1 = new LibraryErrorLog();
        libraryErrorLog1.setId(1L);
        LibraryErrorLog libraryErrorLog2 = new LibraryErrorLog();
        libraryErrorLog2.setId(libraryErrorLog1.getId());
        assertThat(libraryErrorLog1).isEqualTo(libraryErrorLog2);
        libraryErrorLog2.setId(2L);
        assertThat(libraryErrorLog1).isNotEqualTo(libraryErrorLog2);
        libraryErrorLog1.setId(null);
        assertThat(libraryErrorLog1).isNotEqualTo(libraryErrorLog2);
    }
}
