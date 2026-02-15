package com.solar.ai.solar_ai_backend.summary.dto;

import lombok.*;
import java.time.Instant;

/**
 * What goes out on the "summary_results" topic.
 * Keep it small, self-describing, and versioned so it’s evolvable.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResult {
    private String version;          // e.g., "1"
    private String administrationId; // correlation key & Kafka key
    private String contextHash;      // so consumers can dedupe
    private String summaryText;      // the meat
    private Instant generatedAt;     // server clock
}
