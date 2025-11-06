package com.solar.ai.solar_ai_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "administration_summaries")
public class AdministrationSummaryCache {
    @Id
    private String administrationId;
    private String summaryText;
    @Indexed
    private String contextHash;
    private Instant lastUpdated;
}
