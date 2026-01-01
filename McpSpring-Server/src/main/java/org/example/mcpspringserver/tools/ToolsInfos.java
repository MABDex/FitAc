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

   

   // --- HIER IST DIE KORRIGIERTE METHODE ---
    @Tool(description = "Generates a SPARQL query from the user's question, sends it to GraphDB, and returns the JSON response.")
    public String response(String userQuestion) {

        try {
            // 1. SPARQL Query generieren
            String generatedQuery = queryAgent.generateSparql(userQuestion);

            if (generatedQuery == null || generatedQuery.isEmpty()) {
                return "{\"error\": \"SPARQL query could not be generated.\"}";
            }

            // 2. HTTP Header vorbereiten
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Body setzen
            String body = "query=" + generatedQuery;
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // 3. Anfrage an GraphDB senden
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 4. Antwort holen und prüfen (WICHTIG!)
            String responseBody = response.getBody();

            if (responseBody == null) {
                // Falls GraphDB nichts liefert, leeres JSON zurückgeben statt null
                return "{}";
            }

            // 5. In History speichern
            fragenHistoryService.save(userQuestion, responseBody);

            // 6. Nur den Text zurückgeben (kein ResponseEntity)
            return responseBody;

        } catch (Exception e) {
            // Fehler loggen, damit du ihn in Azure siehst
            e.printStackTrace();
            // Dem Chatbot sagen, was passiert ist, statt abzustürzen
            return "{\"error\": \"Database connection failed: " + e.getMessage() + "\"}";
        }
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
