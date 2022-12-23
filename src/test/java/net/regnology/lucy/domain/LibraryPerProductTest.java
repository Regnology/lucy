package net.regnology.lucy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import net.regnology.lucy.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibraryPerProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibraryPerProduct.class);
        LibraryPerProduct libraryPerProduct1 = new LibraryPerProduct();
        libraryPerProduct1.setId(1L);
        LibraryPerProduct libraryPerProduct2 = new LibraryPerProduct();
        libraryPerProduct2.setId(libraryPerProduct1.getId());
        assertThat(libraryPerProduct1).isEqualTo(libraryPerProduct2);
        libraryPerProduct2.setId(2L);
        assertThat(libraryPerProduct1).isNotEqualTo(libraryPerProduct2);
        libraryPerProduct1.setId(null);
        assertThat(libraryPerProduct1).isNotEqualTo(libraryPerProduct2);
    }
}
