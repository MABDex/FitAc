package org.example.mcpspringclient.RestController;

import org.example.mcpspringclient.Agents.AiAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiRestControllerTest {

    @Mock
    private AiAgent aiAgent;

    private AiRestController controller;

    @BeforeEach
    void setup() {
        controller = new AiRestController(aiAgent);
    }

    @Test
    void shouldReturnSuccessResponse() {

        when(aiAgent.askLLM(anyString(), anyString()))
                .thenReturn("Recipe result");

        ResponseEntity<String> response =
                controller.chat("recipe", "1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Recipe result", response.getBody());
    }

    @Test
    void shouldReturnErrorWhenEmpty() {

        when(aiAgent.askLLM(anyString(), anyString()))
                .thenReturn("");

        ResponseEntity<String> response =
                controller.chat("recipe", "1");

        assertTrue(response.getBody().contains("error"));
    }
}
