package org.example.mcpspringserver;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.okhttp.OkHttpAsyncHttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureOpenAIConfig {

    @Bean
    public HttpClient azureHttpClient() {
        // OkHttp statt Netty
        return new OkHttpAsyncHttpClientBuilder().build();
    }

    @Bean
    public OpenAIClient openAIClient(
            @Value("${spring.ai.azure.openai.endpoint}") String endpoint,
            @Value("${spring.ai.azure.openai.api-key}") String apiKey,
            HttpClient azureHttpClient
    ) {
        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .httpClient(azureHttpClient)   // <-- OkHttp aktivieren
                .buildClient();
    }
}
