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

// WICHTIG: Neue Imports für Markdown
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

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

        // --- Navigationsleiste (gekürzt für Übersichtlichkeit, bleibt gleich) ---
        VerticalLayout navBar = createNavBar(); 

        // --- Hauptbereich für Chat ---
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("chat-header");
        header.setWidthFull();

        Button chatbotButton = new Button("Chat-Bot");
        chatbotButton.addClassName("button-chatbot");
        chatbotButton.addClassName("button-selected"); // Markieren als aktiv

        Button ragBotButton = new Button("RAG-Bot", e -> getUI().ifPresent(ui -> ui.navigate("ragBot")));
        ragBotButton.addClassName("button-ragbot");

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Titel
        H1 chatTitle = new H1("Dieser Chat-bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        // --- Nachrichtenbereich (Optimiert) ---
        Div messageArea = new Div();
        messageArea.addClassName("chat-area"); // Nutze die CSS Klasse vom RagBot
        messageArea.setWidthFull();
        
        Div placeholder = new Div();
        placeholder.setText("\uD83E\uDD16 Hier steht die Antwort des LLM...");
        placeholder.getStyle().set("font-style", "italic");
        messageArea.add(placeholder);

        // Eingabefeld Layout
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setWidthFull();
        inputLayout.addClassName("input-layout");

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.addClassName("input-field");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

        // JS für dynamische Höhe
        inputField.getElement().executeJs(
                "this.style.height='auto';" +
                "this.addEventListener('input', function() {" +
                "  this.style.height='auto';" +
                "  this.style.height=(this.scrollHeight) + 'px';" +
                "});"
        );

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        Button micButton = new Button(new Icon(VaadinIcon.MICROPHONE));
        micButton.addClassName("mic-button");

        // --- Sende-Logik (Verbessert) ---
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                // Platzhalter entfernen
                if (messageArea.getChildren().anyMatch(c -> c == placeholder)) {
                    messageArea.remove(placeholder);
                }

                // Nutzer-Nachricht
                Div userMessage = new Div();
                userMessage.setText("User Frage: " + query);
                userMessage.getStyle().set("font-weight", "bold");
                userMessage.getStyle().set("margin-bottom", "10px");
                messageArea.add(userMessage);

                // Backend Antwort holen
                String answerMarkdown = callBackend(query);

                // Markdown zu HTML konvertieren
                String htmlContent = convertMarkdownToHtml(answerMarkdown);

                // Bot-Antwort als HTML anzeigen
                Div botMessage = new Div();
                botMessage.addClassName("bot-message-markdown"); // Gleiches Styling wie RagBot
                botMessage.getElement().setProperty("innerHTML", htmlContent);
                messageArea.add(botMessage);

                // UI Cleanup
                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");
                inputField.clear();
                inputField.getElement().executeJs("this.style.height='auto';");
            } else {
                Notification.show("Bitte geben Sie eine Frage ein!");
            }
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        mainLayout.expand(messageArea);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // --- Hilfsmethode: Markdown zu HTML (Kopie von RagBot) ---
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    // --- Hilfsmethode: Backend (angepasst an Port 8070) ---
    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question=" + URLEncoder.encode(question, StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            return "**Fehler:** Antwort konnte nicht geladen werden.";
        }
    }

    // Hilfsfunktion für die Navbar (Inhalt aus deinem Original übernommen)
    private VerticalLayout createNavBar() {
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        // ... hier den Rest deines Navbar Codes einfügen ...
        return navBar;
    }
}
