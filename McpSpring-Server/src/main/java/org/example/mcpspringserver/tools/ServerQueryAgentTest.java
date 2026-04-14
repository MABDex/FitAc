package org.example.mcpspringserver.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerQueryAgentTest {

    @Mock
    private ChatClient.Builder builder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private ServerQueryAgent agent;

    @BeforeEach
    void setup() {
        when(builder.defaultSystem(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);

        when(responseSpec.content()).thenReturn("```sparql SELECT * WHERE {?s ?p ?o}```");

        agent = new ServerQueryAgent(builder);
    }

    @Test
    void shouldGenerateCleanSparqlQuery() {
        String result = agent.generateSparql("recipes with tomato");

        assertNotNull(result);
        assertEquals("SELECT * WHERE {?s ?p ?o}", result);
    }
}
