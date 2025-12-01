package org.example.mcpspringserver.tools;

import org.example.mcpspringserver.entities.FragenHistory;
import org.example.mcpspringserver.repository.IngredientPriceRepository;
import org.example.mcpspringserver.entities.IngrediantPrice;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class ToolsInfos {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String endpoint = "http://108.143.216.135/repositories/testVF";

    private final IngredientPriceRepository repository;

    @Autowired
    private FragenHistoryService fragenHistoryService;

    public ToolsInfos(IngredientPriceRepository repository) {
        this.repository = repository;
    }

    // -------------------------------------------------------
    // 1) SPARQL direkt ausführen (LLM erstellt Query im Client)
    // -------------------------------------------------------

    @Tool(description = "Executes a SPARQL query on the recipe RDF store")
    public ResponseEntity<String> response(String sparqlQuery) {

        if (sparqlQuery == null || sparqlQuery.trim().isEmpty()) {
            throw new IllegalStateException("SPARQL query is empty!");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String body = "query=" + sparqlQuery;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        // speichern in History
        fragenHistoryService.save(sparqlQuery, response.getBody());

        return ResponseEntity.status(response.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }

    // -------------------------------------------------------
    // 2) Zutatenpreise lesen
    // -------------------------------------------------------
    @Tool(description = "Get all ingredients with their prices from the database.")
    public List<IngrediantPrice> getAllIngredients() {
        return repository.findAll();
    }

    // -------------------------------------------------------
    // 3) History Tools
    // -------------------------------------------------------
    @Tool(description = "Returns the last saved question and answer")
    public FragenHistory getLastQuestion() {
        return fragenHistoryService.getLast1();
    }

    @Tool(description = "Returns the last three saved questions and answers")
    public List<FragenHistory> getLast3Questions() {
        return fragenHistoryService.getLast3();
    }
}
