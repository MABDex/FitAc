package org.example.mcpspringclient.Agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AiAgent {

    private final ChatClient chatClient;

    public AiAgent(ChatClient.Builder chatClient, ToolCallbackProvider toolCallbackProvider) {

        this.chatClient = chatClient
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem("""
                        You are an AI assistant that answers recipe questions using the MCP server.

                        WORKFLOW:

                        1) Translate the user's question into English if needed.
                        2) Generate a SPARQL query (English only). DO NOT EXECUTE IT YOURSELF.
                        3) ALWAYS call the tool "response" with that SPARQL query.
                        4) After receiving the JSON results from the tool:
                           - Read the JSON  
                           - Explain the recipe in the user's language  
                           - Always include:
                               • recipe name  
                               • description  
                               • ingredients  
                               • recipeIngredients  
                               • recipeInstructions (text only)  
                        5) If the user mentions price/cost:
                           - Call getAllIngredients() tool  
                           - Add prices to the answer
                        6) If no results are found:
                           - Answer politely that no recipe was found

                        RULES:
                        - NEVER invent data, only use tool results.
                        - NEVER generate explanations unless after the tool returns JSON.
                        - NEVER call tools repeatedly.
                        - ALWAYS call "response" exactly once per question.
                        """)
                .build();
    }

    public String askLLM(String query) {
        return chatClient
                .prompt()
                .user(query)
                .call()
                .content();
    }
}
