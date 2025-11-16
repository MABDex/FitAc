package org.example.mcpspringserver.tools;



import org.example.mcpspringserver.entities.FragenHistory;
import org.example.mcpspringserver.repository.IngredientPriceRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.example.mcpspringserver.entities.IngrediantPrice;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ToolsInfos {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String endpoint = "http://108.143.216.135/repositories/testVF";

    private final IngredientPriceRepository repository;

    private ServerQueryAgent queryAgent;

    public ToolsInfos(IngredientPriceRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setQueryAgent(@Lazy ServerQueryAgent queryAgent) {
        this.queryAgent = queryAgent;
    }


    @Autowired
    private FragenHistoryService fragenHistoryService;




    @Tool(description = "")
    public ResponseEntity<String> response( String userQuestion) {

        String generatedQuery = queryAgent.generateSparql(userQuestion);

        if (generatedQuery == null || generatedQuery.isEmpty()) {
            throw new IllegalStateException("SPARQL query is empty!");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String body = "query=" + generatedQuery;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        fragenHistoryService.save(userQuestion, response.getBody());

        return ResponseEntity.status(response.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }





    @Tool(description = "Get all ingredients with their prices from the database.")
    public List<IngrediantPrice> getAllIngredients() {
        return repository.findAll();
    }



    @Tool(description = "Returns the last saved question and answer")
    public FragenHistory getLastQuestion() {
        return fragenHistoryService.getLast1();
    }


    @Tool(description = "Returns the last three saved questions and answers")
    public List<FragenHistory> getLast3Questions() {
        return fragenHistoryService.getLast3();
    }



}

//    @Tool(description = "Lädt eine RDF-Datei im N-Triples-Format, parsed sie in ein RDF Model und gibt es zurück.")
//    public Model getRdfModel() throws Exception {
//        String resourcePath = "/test.nt";
//
//        try (InputStream input = ToolsInfos.class.getResourceAsStream(resourcePath)) {
//            if (input == null) {
//                System.err.println("Datei nicht gefunden unter: " + resourcePath);
//                return null;
//            }
//            Model model = Rio.parse(input, "", RDFFormat.NTRIPLES);
//            return model;
//
//        } catch (Exception e) {
//            System.err.println("Fehler beim Parsen der RDF-Datei:");
//            e.printStackTrace();
//            throw e;
//        }
//    }
//
//
//    @Tool(description = "Hier sind Rezeptdaten im Turtle-Format.")
//    public String getRdfDataAsTurtleString() {
//
//        try {
//            // 1. RDF Modell laden
//            Model model = getRdfModel();
//            if (model == null) {
//                return "RDF-Datei konnte nicht geladen werden.";
//            }
//            StringWriter writer = new StringWriter();
//
//            Rio.write(model, writer, RDFFormat.TURTLE);
//            return writer.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Fehler beim Verarbeiten der Anfrage: " + e.getMessage();
//        }
//
//
//    }