package com.solar.ai.solar_ai_backend.summary.api;

import com.solar.ai.solar_ai_backend.model.AdministrationSummaryCache;
import com.solar.ai.solar_ai_backend.repository.AdministrationSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/administrations")
public class SummaryQueryController {

    private final AdministrationSummaryRepository repo;

    @GetMapping("/{id}/summary")
    public ResponseEntity<?> getCachedSummary(@PathVariable String id) {
        return repo.findById(id)
                .map(doc -> ResponseEntity.ok(doc.getSummaryText()))
                .orElse(ResponseEntity.notFound().build());
    }

    // If you want full cache doc for debugging:
    @GetMapping("/{id}/summary/cache")
    public ResponseEntity<?> getCacheDoc(@PathVariable String id) {
        return repo.findById(id)
                .map(AdministrationSummaryCache.class::cast)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
