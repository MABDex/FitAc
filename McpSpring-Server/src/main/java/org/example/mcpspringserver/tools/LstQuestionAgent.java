package org.example.mcpspringserver.tools;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

@Service
public class LstQuestionAgent {

    private final ChatClient chatClient;

    public LstQuestionAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are an AI Assistant that:
                        1. ONLY translates text from UserLanguage to English.
                        2. Extracts all ingredients mentioned in the text and returns them as a simple list.
                        3. Respond only with:
                           - the translated text
                           - a JSON array called "ingredients" containing ingredient names in English
                        4. Do NOT add extra commentary or explanations.
                        Example response format:
                        {
                          "ingredients": ["Tomato", "Sugar", "Butter"]
                        }
                        """)
                .build();
    }

    /**
     * Übersetzt den Text und extrahiert Zutaten
     */
    public String translateAndExtractIngredients(String text) {
        try {
            String response = chatClient.prompt()
                    .user(text)
                    .call()
                    .content();

            return response != null ? response.trim() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
