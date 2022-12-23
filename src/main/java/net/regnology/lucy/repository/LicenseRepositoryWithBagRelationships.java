package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.License;
import org.springframework.data.domain.Page;

public interface LicenseRepositoryWithBagRelationships {
    Optional<License> fetchBagRelationships(Optional<License> license);

    List<License> fetchBagRelationships(List<License> licenses);

    Page<License> fetchBagRelationships(Page<License> licenses);
}
