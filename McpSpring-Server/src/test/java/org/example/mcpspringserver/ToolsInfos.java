package org.example.mcpspringserver.tools;

import org.example.mcpspringserver.repository.IngredientPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolsInfosTest {

    @Mock
    private IngredientPriceRepository repository;

    @Mock
    private ServerQueryAgent queryAgent;

    @Mock
    private FragenHistoryService fragenHistoryService;

    @Mock
    private RestTemplate restTemplate;

    private ToolsInfos toolsInfos;

    @BeforeEach
    void setup() {
        toolsInfos = new ToolsInfos(repository);
        toolsInfos.setQueryAgent(queryAgent);

        ReflectionTestUtils.setField(toolsInfos, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(toolsInfos, "fragenHistoryService", fragenHistoryService);
    }

    @Test
    void shouldReturnGraphDBResponse() {

        String sparql = "SELECT * WHERE {?s ?p ?o}";
        String json = "{\"results\":[]}";

        when(queryAgent.generateSparql(anyString())).thenReturn(sparql);

        ResponseEntity<String> response =
                new ResponseEntity<>(json, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        String result = toolsInfos.response("recipes");

        assertEquals(json, result);
        verify(fragenHistoryService).save(anyString(), eq(json));
    }

    @Test
    void shouldReturnEmptyJsonWhenBodyIsNull() {

        when(queryAgent.generateSparql(anyString())).thenReturn("SELECT");

        ResponseEntity<String> response =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(response);

        String result = toolsInfos.response("test");

        assertEquals("{}", result);
    }

    @Test
    void shouldHandleException() {

        when(queryAgent.generateSparql(anyString())).thenReturn("SELECT");

        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection error"));

        String result = toolsInfos.response("test");

        assertTrue(result.contains("Database connection failed"));
    }
}
