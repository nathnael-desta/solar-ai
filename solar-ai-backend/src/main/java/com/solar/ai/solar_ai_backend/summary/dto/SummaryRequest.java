package com.solar.ai.solar_ai_backend.summary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRequest {
    /** The administration / project ID we’re summarizing. */
    private String administrationId;

    /** Plain text for now (later you’ll send structured context). */
    @NotBlank
    private String contextText;

    /** Optional – UI/language hints you might use later. */
    private String locale;   // e.g., "fr-FR", "en-US"
    private String audience; // e.g., "installer" | "admin"
}
