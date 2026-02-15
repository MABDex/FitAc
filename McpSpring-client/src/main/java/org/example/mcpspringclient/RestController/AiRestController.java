package org.example.mcpspringclient.RestController;


import org.example.mcpspringclient.Agents.AiAgent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AiRestController {

    private AiAgent aiAgent;

    public AiRestController(AiAgent aiAgent) {
        this.aiAgent = aiAgent;
    }



    @GetMapping(value = "/chat" , produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat(@RequestParam String query , @RequestParam String conversationId) {
        try {
            String answer = aiAgent.askLLM(conversationId , query);

            // Optional: prüfen, ob die Antwort leer ist oder Fehler enthält
            if (answer == null || answer.isEmpty() || answer.contains("MALFORMED QUERY")) {
                return ResponseEntity.ok("{\"error\":\"Das Rezept wurde nicht in der Datenbank gefunden.\"}");
            }

            return ResponseEntity.ok( answer );
        } catch (Exception e) {
            // WICHTIG: Den Fehler in die Konsole schreiben, damit man ihn in Azure sieht!
            e.printStackTrace(); 
            
            // Generische Fehlerbehandlung für den User
            return ResponseEntity.ok("{\"error\":\"Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.\"}");
        }
    }



}
