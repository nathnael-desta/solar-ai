package com.solar.ai.solar_ai_backend.repository;

import com.solar.ai.solar_ai_backend.model.AdministrationSummaryCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data creates the runtime implementation automatically.
 * We use String as the ID type because @Id is a String (administrationId).
 */
@Repository
public interface AdministrationSummaryRepository
        extends MongoRepository<AdministrationSummaryCache, String> {

    // NOTE: findById(...) already exists via MongoRepository.

    // Look up by both administrationId AND contextHash (most common cache check).
    Optional<AdministrationSummaryCache> findByAdministrationIdAndContextHash(String administrationId, String contextHash);

    // Sometimes you may want the latest by hash (if multiple docs share identical context).
    List<AdministrationSummaryCache> findAllByContextHashOrderByLastUpdatedDesc(String contextHash);

    // Simple existence checks that are efficient when combined with the index
    boolean existsByAdministrationIdAndContextHash(String administrationId, String contextHash);

    // Housekeeping helpers (useful later)
    List<AdministrationSummaryCache> findAllByLastUpdatedBefore(Instant cutoff);
}
