package org.example.mcpspringclient.config;

import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class McpTimeoutConfig {

    @Bean
    public McpSyncClientCustomizer mcpSyncClientCustomizer() {
        return (name, spec) -> spec.requestTimeout(Duration.ofSeconds(60));
    }
}
