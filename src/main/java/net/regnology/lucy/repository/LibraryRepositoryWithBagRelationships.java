package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.Library;
import org.springframework.data.domain.Page;

public interface LibraryRepositoryWithBagRelationships {
    Optional<Library> fetchBagRelationships(Optional<Library> library);

    List<Library> fetchBagRelationships(List<Library> libraries);

    Page<Library> fetchBagRelationships(Page<Library> libraries);
}
