package org.example.frontend.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.JSONObject; // WICHTIG: Erfordert org.json in der pom.xml

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@SpringComponent
@UIScope
@Route("smartMeal")
@CssImport("./styles/shared-styles.css")
public class SmartMealView extends HorizontalLayout {

    public SmartMealView() {
        setSizeFull();

        // --- Navigationsleiste (links) ---
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);
        navBar.setSpacing(true);

        Div logoDiv = new Div();
        logoDiv.setWidth("250px");
        logoDiv.setHeight("100px");
        logoDiv.getStyle().set("background-image", "url('logo2.png')");
        logoDiv.getStyle().set("background-size", "contain");
        logoDiv.getStyle().set("background-repeat", "no-repeat");
        logoDiv.getStyle().set("background-position", "center");

        Div chatLabel = new Div();
        chatLabel.setText("Chat-Bot");
        chatLabel.getStyle().set("margin-top", "auto");
        chatLabel.getStyle().set("font-size", "24px");
        chatLabel.getStyle().set("font-weight", "bold");
        chatLabel.getStyle().set("color", "white");
        chatLabel.getStyle().set("font-style", "italic");

        navBar.add(logoDiv, chatLabel);

        // --- Hauptchatbereich ---
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("chat-header");
        header.setWidthFull();

        Button chatbotButton = new Button("Chat-Bot");
        chatbotButton.addClassName("button-chatbot");

        Button ragBotButton = new Button("RAG-Bot");
        ragBotButton.addClassName("button-ragbot");
        ragBotButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ragBot")));

        header.add(chatbotButton, ragBotButton);

        H1 chatTitle = new H1("OpenAI Chat-Bot");
        chatTitle.addClassName("chat-title");

        // Die Fläche, in der die Nachrichten erscheinen
        Div messageArea = new Div();
        messageArea.setWidthFull();
        messageArea.getStyle().set("overflow-y", "auto");
        messageArea.getStyle().set("padding", "20px");
        messageArea.getStyle().set("flex-grow", "1");

        // --- Eingabefeld & Senden ---
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setWidthFull();
        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir helfen?");
        inputField.setWidthFull();

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        // --- CLICK LISTENER (LOGIK) ---
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                // 1. User Frage anzeigen
                Div userMsg = new Div();
                userMsg.setText("Ich: " + query);
                userMsg.getStyle().set("font-weight", "bold");
                messageArea.add(userMsg);

                // 2. Antwort vom Backend holen
                String rawResponse = callBackend(query);

                // 3. WICHTIG: JSON "auspacken" und nur den Text behalten
                String cleanText = extractTextFromJson(rawResponse);

                // 4. Saubere Antwort anzeigen
                Div botMsg = new Div();
                botMsg.getStyle().set("background-color", "#F1F5F9"); // Helles Grau
                botMsg.getStyle().set("padding", "15px");
                botMsg.getStyle().set("border-radius", "10px");
                botMsg.getStyle().set("margin-bottom", "15px");
                
                // Markdown zu HTML konvertieren (für Listen, Fett, etc.)
                botMsg.getElement().setProperty("innerHTML", renderMarkdown(cleanText));
                
                messageArea.add(botMsg);

                // Scrollen & Reset
                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");
                inputField.clear();
            }
        });

        inputLayout.add(inputField, sendButton);
        mainLayout.add(header, chatTitle, messageArea, inputLayout);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    /**
     * Entfernt den JSON-Ballast und die geschweiften Klammern.
     */
    private String extractTextFromJson(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();
        
        // Versuchen, das "response" Feld aus dem JSON zu ziehen
        try {
            if (trimmed.startsWith("{")) {
                JSONObject json = new JSONObject(trimmed);
                return json.optString("response", trimmed);
            }
            // Falls das Backend "json { ... }" sendet (wie im Screenshot)
            if (trimmed.startsWith("json")) {
                String potentialJson = trimmed.substring(4).trim();
                JSONObject json = new JSONObject(potentialJson);
                return json.optString("response", trimmed);
            }
        } catch (Exception e) {
            // Falls es kein echtes JSON ist, säubern wir manuell die schlimmsten Zeichen
            return trimmed.replace("json{", "").replace("{\"response\":\"", "").replace("\"}", "").replace("}", "");
        }
        return trimmed;
    }

    private String renderMarkdown(String text) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question=" + URLEncoder.encode(question, StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) res.append(line);
            in.close();
            return res.toString();
        } catch (Exception e) {
            return "Fehler bei der Verbindung.";
        }
    }
}
