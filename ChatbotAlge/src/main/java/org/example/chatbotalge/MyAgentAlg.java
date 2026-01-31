package org.example.chatbotalge;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyAgentAlg {


    private ChatClient chatClient;


    public MyAgentAlg(ChatClient.Builder chatClient) {
        this.chatClient = chatClient

                .defaultSystem( """
                       ANTWORTE IMMER in SAUBEREM MARKDOWN.
                       GIB KEIN JSON, KEINE CODEBLÖCKE, KEINE ROHDATEN AUS.

                       FORMAT-REGELN:
                       - Nutze Überschriften (##, ###), wenn sinnvoll
                       - Nutze Listen (- oder 1.), wenn es hilft
                       - Nutze Absätze für gute Lesbarkeit
                       - Antworte in der Sprache des Nutzers.
                       - Antworte direkt auf die Nutzerfrage
                       - KEIN zusätzlicher Meta-Text wie "Hier ist die Antwort"

                        WENN die Frage ein Rezept betrifft:
                        ##   Titel
                        ###  Zutaten
                        ###  Zubereitung
                        ###  Preise der Zutaten       

                        WENN die Frage KEIN Rezept betrifft:
                        - Nutze erklärende Überschriften oder Listen
                        - Halte die Antwort klar und strukturiert
                                    """ )
                .build();
    }



    @GetMapping(value="/askLLM" , produces = MediaType.TEXT_PLAIN_VALUE)
    public String askLLM(String question){
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }


}
