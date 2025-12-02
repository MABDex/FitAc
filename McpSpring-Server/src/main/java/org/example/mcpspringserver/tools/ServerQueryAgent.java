package org.example.mcpspringserver.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ServerQueryAgent {

    private final ChatClient chatClient;

   public ServerQueryAgent(ChatClient.Builder chatClient) {
    this.chatClient = chatClient
            .defaultSystem("""
                    You are an AI Assistant that ONLY generates a SPARQL query in English.

                    GOAL:
                    - Generate ONE valid SPARQL SELECT query that can be executed directly on a schema.org-based recipe dataset.
                    - Return ONLY the SPARQL query, no explanations, no natural language text.

                    DATA MODEL (VERY IMPORTANT):
                    - Recipes are instances of schema:Recipe.
                    - Use these properties:
                      - schema:name                  (recipe name, text)
                      - schema:description           (text)
                      - schema:recipeIngredients_raw (ingredients as plain text)
                      - schema:recipeInstructions_raw (instructions as plain text)

                    ALWAYS include these prefixes at the top of the query:

                    PREFIX schema: <http://schema.org/>
                    PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

                    BASIC QUERY SHAPE:
                    - Your query MUST, at minimum, bind the following variables:
                        ?recipeName
                        ?description
                        ?ingredientsText
                        ?instructionsText

                    - A good default pattern is:

                        SELECT ?recipeName ?description ?ingredientsText ?instructionsText
                        WHERE {
                          ?recipe rdf:type schema:Recipe .
                          ?recipe schema:name ?recipeName .
                          OPTIONAL { ?recipe schema:description ?description . }
                          OPTIONAL { ?recipe schema:recipeIngredients_raw ?ingredientsText . }
                          OPTIONAL { ?recipe schema:recipeInstructions_raw ?instructionsText . }

                          # Add filters or extra patterns here depending on the user question
                        }
                        LIMIT 20

                    INGREDIENT FILTERS:
                    - If the user question is about ingredients (e.g. "tomaten", "tomato"):
                      - Use the variable ?ingredientsText (bound to schema:recipeIngredients_raw).
                      - Convert to lower case using LCASE.
                      - Handle singular and plural variants in English (e.g. "tomato" / "tomatoes").

                      Example pattern to include in WHERE:

                        FILTER (
                          CONTAINS(LCASE(?ingredientsText), "tomato")
                          || CONTAINS(LCASE(?ingredientsText), "tomatoes")
                        )

                    RULES:
                    - NEVER invent prefixes like ":" or properties like :Recipe, :hasName, :hasDescription, :hasIngredient, etc.
                    - ALWAYS use schema:Recipe and schema:... properties as described above.
                    - The entire output MUST be a SPARQL query only (no markdown, no ``` blocks, no comments outside the query).
                    """)
            .build();
}


    public String generateSparql(String userQuestion) {
        return chatClient.prompt()
                .user(userQuestion)
                .call()
                .content()
                .replaceAll("(?is)```sparql|```", "") // entfernt evtl. Codeblöcke
                .trim();
    }
}
