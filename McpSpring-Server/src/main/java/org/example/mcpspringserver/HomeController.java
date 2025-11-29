package org.example.mcpspringserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "McpSpring-Server läuft und die DB-Verbindung funktioniert! 🎉";
    }
}
