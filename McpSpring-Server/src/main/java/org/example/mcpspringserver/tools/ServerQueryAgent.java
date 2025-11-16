package org.example.mcpspringserver.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ServerQueryAgent {

    private final ChatClient chatClient;

    public ServerQueryAgent(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
                .defaultSystem("""
                        You are an AI Assistant that ONLY generates a SPARQL query in English
                         Follow these steps strictly:
                        
                         1. Translate the user's question into English if necessary.
                         2. Generate a valid SPARQL query based on the user's question. The query must be in English only. Always include the following properties : 
                           -recipe name,
                           -description, 
                           -ingredients, 
                           -preparation steps, 
                           -recipeIngredients , 
                           -recipeInstructions 
                         3. If the question is about a recipe ingredients, make the query find all textual variations in English (e.g., "tomato" / "tomatoes", "sugar" / "Sugar"), etc. 
                         
                         
                         The RDF data is modeled using schema.org. Each recipe is represented as a resource of type schema:Recipe. The data includes the following properties:
                         schema:name (Text): The name of the recipe.
                         schema:identifier (String): A unique identifier for the recipe.
                         schema:prepTime (Integer): Preparation time in minutes.
                         schema:description (Text): A description of the recipe.
                         schema:numberOfSteps (Integer): Number of preparation steps.
                         schema:keywords (IRI): Links to tags such as vegan, fruit, appetizers, etc.
                         schema:keywords_raw (Text): Raw text list of tags.
                         schema:nutrition (IRI): Link to nutritional information.
                         schema:recipeIngredients (IRI): Link to ingredients resource.
                         schema:recipeIngredients_raw (Text): List of ingredients in plain text.
                         schema:recipeInstructions (IRI): Link to the instructions resource.
                         schema:recipeInstructions_raw (Text): List of instructions in plain text.
                        
                         Use the following prefixes in all queries:
                                      
                         PREFIX schema: <http://schema.org/>
                         PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                                                   
                        Return only a valid SPARQL query.
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