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
                        You are an AI assistant that answers user questions in JSON format
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
