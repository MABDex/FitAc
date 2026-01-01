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

// Neue Imports für Markdown 
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

        
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);
        navBar.setSpacing(true);
        navBar.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        Div logoDiv = new Div();
        logoDiv.setWidth("250px");
        logoDiv.setHeight("100px");
        logoDiv.getStyle().set("background-image", "url('logo2.png')");
        logoDiv.getStyle().set("background-size", "contain");
        logoDiv.getStyle().set("background-repeat", "no-repeat");
        logoDiv.getStyle().set("background-position", "center");

        Div spacer = new Div();
        spacer.setHeight("100px");

        // Links und Icons
        Anchor impressumLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Impressum");
        impressumLink.setTarget("_blank");
        Anchor datenschutzLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Datenschutzinformation");
        datenschutzLink.setTarget("_blank");
        Anchor barrierefreiheitLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Barrierefreiheitserklärung");
        barrierefreiheitLink.setTarget("_blank");

        HorizontalLayout barriereLayout = new HorizontalLayout(new Icon(VaadinIcon.CHECK_CIRCLE), barrierefreiheitLink);
        barriereLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout datenschutzLayout = new HorizontalLayout(new Icon(VaadinIcon.OPEN_BOOK), datenschutzLink);
        datenschutzLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout impressumLayout = new HorizontalLayout(new Icon(VaadinIcon.CLOCK), impressumLink);
        impressumLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Div chatLabel = new Div();
        chatLabel.setText("Chat-Bot");
        chatLabel.getStyle().set("margin-top", "auto").set("font-size", "24px").set("font-weight", "bold")
                .set("color", "white").set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressumLayout, datenschutzLayout, barriereLayout, chatLabel);

        // --- Hauptbereich ---
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
        chatbotButton.addClassName("button-selected"); // Markierung als aktiv

        Button ragBotButton = new Button("RAG-Bot", e -> getUI().ifPresent(ui -> ui.navigate("ragBot")));
        ragBotButton.addClassName("button-ragbot");

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 chatTitle = new H1("Dieser Chat-Bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        // --- Nachrichtenbereich (Optimiert wie RagBot) ---
        Div messageArea = new Div();
        messageArea.addClassName("chat-area"); // Nutzt die CSS Klasse aus shared-styles.css
        messageArea.setWidthFull();

        Div placeholder = new Div();
        placeholder.setText("\uD83E\uDD16 Hier steht die Antwort des LLM...");
        placeholder.getStyle().set("font-style", "italic");
        messageArea.add(placeholder);

        // --- Eingabebereich ---
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.addClassName("input-layout");
        inputLayout.setWidthFull();

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.addClassName("input-field");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

        // JS für dynamische Höhe
        inputField.getElement().executeJs(
                "this.style.height='auto';" +
                "this.style.overflowY='hidden';" +
                "this.addEventListener('input', function() {" +
                "  this.style.height='auto';" +
                "  this.style.height=(this.scrollHeight) + 'px';" +
                "});"
        );

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        Button micButton = new Button(new Icon(VaadinIcon.MICROPHONE));
        micButton.addClassName("mic-button");
        micButton.addClickListener(e -> {
            inputField.getElement().executeJs(
                "if (!('webkitSpeechRecognition' in window)) { alert('Browser Error'); } " +
                "else { var recognition = new webkitSpeechRecognition(); recognition.lang = 'de-DE'; " +
                "recognition.onresult = function(event) { $0.value = event.results[0][0].transcript; " +
                "$0.dispatchEvent(new Event('input', { bubbles: true })); }; recognition.start(); }", 
                inputField.getElement());
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        // --- Sende-Logik mit Markdown-Support ---
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                if (messageArea.getChildren().anyMatch(c -> c == placeholder)) {
                    messageArea.remove(placeholder);
                }

                // User Frage
                Div userMsg = new Div();
                userMsg.setText("User Frage: " + query);
                userMsg.getStyle().set("font-weight", "bold").set("margin-bottom", "10px");
                messageArea.add(userMsg);

                // Antwort holen und rendern
                String rawAnswer = callBackend(query);
                String htmlAnswer = convertMarkdownToHtml(rawAnswer);

                Div botMsg = new Div();
                botMsg.addClassName("bot-message-markdown"); // Gleiches Styling wie RagBot
                botMsg.getElement().setProperty("innerHTML", htmlAnswer);
                messageArea.add(botMsg);

                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");
                inputField.clear();
                inputField.getElement().executeJs("this.style.height='auto';");
            } else {
                Notification.show("Bitte geben Sie eine Frage ein!");
            }
        });

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        mainLayout.expand(messageArea);
        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // --- Hilfsmethoden (Markdown & Backend) ---
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String callBackend(String question) {
        try {
            // Port 8070 wie in deinem Original
            String urlString = "http://localhost:8070/askLLM?question=" + URLEncoder.encode(question, StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            return "**Fehler:** Verbindung zum Server fehlgeschlagen.";
        }
    }
}
