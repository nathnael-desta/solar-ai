package com.solar.ai.solar_ai_backend.summary.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResult {
    private String administrationId;
    private String contextHash;
    private String summaryText;
}
