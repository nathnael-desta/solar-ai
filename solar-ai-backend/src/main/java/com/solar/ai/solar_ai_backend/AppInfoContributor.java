package com.solar.ai.solar_ai_backend;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.data.mongodb.core.annotation.Collation;

import java.util.Map;

@Collation
public class AppInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", "solar-ai-builder",
                "purpose", "AI summaries for solar projects",
                "env", "dev"
        ));
    }


}
