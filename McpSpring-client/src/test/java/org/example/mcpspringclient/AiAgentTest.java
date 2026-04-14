package org.example.mcpspringclient.Agents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAgentTest {

    @Mock
    private ChatClient.Builder builder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ToolCallbackProvider toolCallbackProvider;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private AiAgent aiAgent;

    @BeforeEach
    void setup() {
        // Wir nutzen explizite Typen für die Matcher, um die "ambiguous reference" Fehler zu beheben
        
        // 1. Stubbing für defaultToolCallbacks (Varargs Version)
        when(builder.defaultToolCallbacks(any(ToolCallback[].class))).thenReturn(builder);
        
        // 2. Stubbing für defaultAdvisors (Varargs Version)
        when(builder.defaultAdvisors(any(Advisor[].class))).thenReturn(builder);
        
        when(builder.defaultSystem(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        
        // 3. Stubbing für advisors im RequestSpec (Varargs Version)
        when(requestSpec.advisors(any(Advisor[].class))).thenReturn(requestSpec);
        
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("AI Answer");

        aiAgent = new AiAgent(builder, toolCallbackProvider, chatMemory);
    }

    @Test
    void shouldReturnAnswer() {
        String result = aiAgent.askLLM("1", "recipes");

        assertEquals("AI Answer", result);
    }
}
