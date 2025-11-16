package org.example.mcpspringclient;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class McpSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSpringClientApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(List<McpSyncClient> clients) {
        return args -> {
            clients.forEach(cli -> {
                cli.listTools().tools().forEach(tool -> {
                    System.out.println("-------------------");
                    System.out.println(tool.name());
                    System.out.println(tool.description());
                    System.out.println(tool.inputSchema());
                    System.out.println("-------------------");
                });
            });
        };
    }

}
