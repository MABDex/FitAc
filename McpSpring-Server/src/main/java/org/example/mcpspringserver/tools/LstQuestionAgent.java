package org.example.mcpspringserver.tools;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

@Service
public class LstQuestionAgent {
    private final ChatClient chatClient;

    public LstQuestionAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are an AI Assistant that ONLY translates text from UserLanguage to English.
                        Respond only with the translated text, no extra commentary.
                        """)
                .build();
    }

    /**
     * Übersetzt einen Text aus der User-Sprache ins Englische
     */
    public String translateToEnglish(String text) {
        try {
            String translated = chatClient.prompt()
                    .user(text)
                    .call()
                    .content();

            return translated != null ? translated.trim() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
