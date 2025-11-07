package com.solar.ai.solar_ai_backend.summary;

import com.solar.ai.solar_ai_backend.summary.dto.SummaryRequest;
import org.springframework.stereotype.Component;

@Component
public class SummarizationWorker {

    /**
     * Fake LLM: simple, deterministic summarizer for learning.
     * Later, you’ll swap this with a call to your hosted model.
     */
    public String generateSummary(SummaryRequest req) {
        String text = req.getContextText();

        // Tiny heuristic examples
        boolean mentionsPrescription = text.toLowerCase().contains("prescription");
        boolean mentionsMissing = text.toLowerCase().contains("missing");

        StringBuilder sb = new StringBuilder();
        sb.append("AI Summary (STUB)\n");
        sb.append("- Administration: ").append(req.getAdministrationId()).append("\n");
        if (req.getAudience() != null) sb.append("- Audience: ").append(req.getAudience()).append("\n");
        if (req.getLocale() != null) sb.append("- Locale: ").append(req.getLocale()).append("\n");

        sb.append("\nKey points:\n");
        if (mentionsPrescription) sb.append("• Possible prescription detected in the document.\n");
        if (mentionsMissing) sb.append("• Some items are noted as missing.\n");

        // Always include a short excerpt
        String excerpt = text.length() > 240 ? text.substring(0, 240) + "…" : text;
        sb.append("• Excerpt: ").append(excerpt).append("\n");

        sb.append("\nNext steps:\n");
        if (mentionsPrescription) {
            sb.append("• Verify the exact prescription and communicate it in the dashboard (with disclaimer).\n");
        } else {
            sb.append("• No explicit prescription detected; manual review recommended if uncertain.\n");
        }
        if (mentionsMissing) {
            sb.append("• Request the missing items (e.g., site photos) from the client.\n");
        }

        return sb.toString();
    }
}
