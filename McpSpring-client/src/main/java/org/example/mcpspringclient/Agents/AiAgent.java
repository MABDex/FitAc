package org.example.mcpspringclient.Agents;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AiAgent {


    private ChatClient chatClient;
                                                  // Object contient la Liste des Tools
    public AiAgent(ChatClient.Builder chatClient , ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClient
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem( """
                        You are an AI Assistant that answers user questions about recipes
                        
                         -Translate the user's question into English if necessary,
                         -The SPARQL query has already been generated and executed by the MCP-server,
                         -your task is to read the JSON results returned from the query,
                         -Explain the answer to the user in natural language (same language as the user),Always include the following properties: 
                           1) recipe name,
                           2) description, 
                           3) ingredients, 
                           4) recipeIngredients , 
                           5) recipeInstructions (strictly include the instructions as text, do NOT provide any links)
                         
                         
                         -If the user asks about prices or costs of ingredients,you must include the price for every ingredient in each recipe. always use Only the provided database tools getAllIngredients() to fetch the actual prices from the database and include them in the answer.
                         -If the user does not ask about prices, strictly do not include any price information.
                         -If no results are found, politely inform the user do NOT generate SPARQL queries
                         
                         Important rules:
                        - Always use the provided tools, do NOT invent answers without querying the data.
                        - If no matching recipes are found, politely inform the user.
                        - Include both the SPARQL query and the natural answer if the user might need it.
                       
                                    """ )
                .build();
    }

    public String askLLM(String query){
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }






}
