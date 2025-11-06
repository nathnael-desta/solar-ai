package com.solar.ai.solar_ai_backend.bootstrap;

import com.solar.ai.solar_ai_backend.model.AdministrationSummaryCache;
import com.solar.ai.solar_ai_backend.repository.AdministrationSummaryRepository;
import com.solar.ai.solar_ai_backend.util.ContextHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class MongoSmokeRunner implements CommandLineRunner {

    private final AdministrationSummaryRepository repo;

    @Override
    public void run(String... args) {
        // Pick any demo admin id for the smoke test
        String adminId = "ADM-12345";

        // Fake “combined” context text (later you’ll generate this from the real DTO)
        String combinedText = """
                Administration: ADM-12345
                StatusDates: SUBMITTED=2025-10-27, REVIEW=2025-10-30
                MissingFields: site_photos
                Issues: none
                Comments: "initial test"
                """;

        String ctxHash = ContextHasher.sha256Base64(combinedText);

        // Remove any prior doc for a clean run
        repo.findById(adminId).ifPresent(existing -> {
            log.info("Deleting existing cache for {} (lastUpdated={})", adminId, existing.getLastUpdated());
            repo.deleteById(adminId);
        });

        // Build and save a simple cache document
        AdministrationSummaryCache cache = AdministrationSummaryCache.builder()
                .administrationId(adminId)
                .summaryText("This is a B3 smoke-test summary for ADM-12345. ✅")
                .contextHash(ctxHash)
                .lastUpdated(Instant.now())
                .build();

        repo.save(cache);
        log.info("Saved AdministrationSummaryCache: id={}, contextHash={}", adminId, ctxHash);

        // Read it back by id
        repo.findById(adminId).ifPresentOrElse(found ->
                        log.info("Read-back OK: id={}, ctxHash={}, summary='{}'",
                                found.getAdministrationId(), found.getContextHash(), found.getSummaryText()),
                () -> log.error("Read-back FAILED for id={}", adminId));

        // Extra: existence check using id + hash
        boolean fresh = repo.existsByAdministrationIdAndContextHash(adminId, ctxHash);
        log.info("Freshness check existsByAdministrationIdAndContextHash(id,hash) = {}", fresh);

        log.info("""
                Mongo smoke test complete.
                Next: verify in mongosh:
                  docker exec -it mongo mongosh
                  > use solar_ai
                  > db.administration_summaries.find({ _id: "ADM-12345" }).pretty()
                  > db.administration_summaries.getIndexes()
                """);
    }
}
