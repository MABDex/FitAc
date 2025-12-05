package org.example.mcpspringserver.tools;

import org.example.mcpspringserver.entities.FragenHistory;
import org.example.mcpspringserver.repository.IngredientPriceRepository;
import org.example.mcpspringserver.entities.IngrediantPrice;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class ToolsInfos {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String endpoint = "http://108.143.216.135/repositories/testVF";

    private final IngredientPriceRepository repository;
    private ServerQueryAgent queryAgent;

    @Autowired
    private FragenHistoryService fragenHistoryService;

    public ToolsInfos(IngredientPriceRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setQueryAgent(@Lazy ServerQueryAgent queryAgent) {
        this.queryAgent = queryAgent;
    }

    @Tool(description = "Generates a SPARQL query from the user's question, sends it to GraphDB, and returns the JSON response.")
    public ResponseEntity<String> response(String userQuestion) {
        String generatedQuery = queryAgent.generateSparql(userQuestion);

        if (generatedQuery == null || generatedQuery.isEmpty()) {
            throw new IllegalStateException("SPARQL query is empty!");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // WICHTIG: URL-codiert
        String body = "query=" + URLEncoder.encode(generatedQuery, StandardCharsets.UTF_8);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);

        // Speichert Frage + Antwort in History
        fragenHistoryService.save(userQuestion, response.getBody());

        return ResponseEntity.status(response.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }

    @Tool(description = "Get all ingredients with their prices from the database.")
    public List<IngrediantPrice> getAllIngredients() {
        return repository.findAll();
    }

    @Tool(description = "Returns the last saved question and answer.")
    public FragenHistory getLastQuestion() {
        return fragenHistoryService.getLast1();
    }

    @Tool(description = "Returns the last three saved questions and answers.")
    public List<FragenHistory> getLast3Questions() {
        return fragenHistoryService.getLast3();
    }
}
