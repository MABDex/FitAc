package org.example.mcpspringserver.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mcpspringserver.IngredientMatch;
import org.example.mcpspringserver.entities.IngrediantPrice;
import org.example.mcpspringserver.repository.IngredientPriceRepository;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LstQuestionAgent {

    private final ChatClient chatClient;
    private final IngredientPriceRepository repository;
    private final ObjectMapper objectMapper;

    public LstQuestionAgent(ChatClient.Builder chatClientBuilder, IngredientPriceRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are an AI Assistant that:
                        1. Translates the user's text into English.
                        2. Extracts all ingredients mentioned.
                        3. Returns ONLY a JSON object with two fields:
                           - "translatedText": The full translation.
                           - "ingredients": A simple string list of ingredients in singular form (e.g., "Tomato" not "Tomatoes").
                        
                        Example Output:
                        {
                          "translatedText": A savory and aromatic dish featuring the earthy sweetness of carrots and onions, perfectly balanced by the salty tang of green olives.
                          "ingredients": ["Green Olives", "Onion", "Carrot"]
                        }
                        """)
                .build();
    }

    public List<IngredientMatch> processAndMatch(String userInput) {
        List<IngredientMatch> results = new ArrayList<>();

        try {
            // 1. KI-Anfrage: Extrahiere Zutaten als JSON
            String jsonResponse = chatClient.prompt()
                    .user(userInput)
                    .call()
                    .content();

            // 2. JSON parsen
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode ingredientsNode = rootNode.get("ingredients");

            if (ingredientsNode != null && ingredientsNode.isArray()) {
                for (JsonNode node : ingredientsNode) {
                    String ingredientName = node.asText();

                    // 3. Datenbank-Match in MySQL
                    Optional<IngrediantPrice> dbEntry = repository.findByNameIgnoreCase(ingredientName);

                    if (dbEntry.isPresent()) {
                        results.add(new IngredientMatch(ingredientName, dbEntry.get().getPrice(), true));
                    } else {
                        // Zutat in DB nicht gefunden -> Preis 0.0
                        results.add(new IngredientMatch(ingredientName, 0.0, false));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Processing: " + e.getMessage());
        }

        return results;
    }
}
