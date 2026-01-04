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

// WICHTIG: Diese Imports für die saubere Darstellung
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.JSONObject;

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

        // --- Linke Navigationsleiste ---
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

        Anchor impressumLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Impressum");
        impressumLink.setTarget("_blank");

        Anchor datenschutzLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Datenschutzinformation");
        datenschutzLink.setTarget("_blank");

        Anchor barrierefreiheitLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Barrierefreiheitserklärung");
        barrierefreiheitLink.setTarget("_blank");

        Icon checkIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
        checkIcon.setSize("14px");
        HorizontalLayout barriereLayout = new HorizontalLayout(checkIcon, barrierefreiheitLink);
        barriereLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon bookIcon = new Icon(VaadinIcon.OPEN_BOOK);
        bookIcon.setSize("14px");
        HorizontalLayout datenschutzLayout = new HorizontalLayout(bookIcon, datenschutzLink);
        datenschutzLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon caretIcon = new Icon(VaadinIcon.CLOCK);
        caretIcon.setSize("14px");
        HorizontalLayout impressumLayout = new HorizontalLayout(caretIcon, impressumLink);
        impressumLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Div chatLabel = new Div();
        chatLabel.setText("Chat-Bot");
        chatLabel.getStyle().set("margin-top", "auto");
        chatLabel.getStyle().set("font-size", "24px");
        chatLabel.getStyle().set("font-weight", "bold");
        chatLabel.getStyle().set("color", "white");
        chatLabel.getStyle().set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressumLayout, datenschutzLayout, barriereLayout, chatLabel);

        // --- Hauptbereich ---
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("chat-header");
        header.setWidthFull();

        Button chatbotButton = new Button("Chat-Bot");
        chatbotButton.addClassName("button-chatbot");

        Button ragBotButton = new Button("RAG-Bot");
        ragBotButton.addClassName("button-ragbot");
        ragBotButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("ragBot")));

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 chatTitle = new H1("Dieser Chat-Bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        // --- Nachrichtenbereich ---
        Div messageArea = new Div();
        messageArea.setWidthFull();
        messageArea.getStyle().set("overflow-y", "auto");
        messageArea.getStyle().set("padding", "20px");
        messageArea.getStyle().set("background-color", "#FFFFFF");
        
        Div botIntro = new Div();
        botIntro.setText("\uD83E\uDD16 Hallo! Wie kann ich dir heute helfen?");
        botIntro.getStyle().set("font-style", "italic");
        messageArea.add(botIntro);

        // --- Eingabefeld ---
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setWidthFull();
        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.addClassName("input-field");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

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
        micButton.addClickListener(e -> {
            inputField.getElement().executeJs(
                "var recognition = new webkitSpeechRecognition(); recognition.lang = 'de-DE';" +
                "recognition.onresult = function(event) { $0.value = event.results[0][0].transcript;" +
                "$0.dispatchEvent(new Event('input', { bubbles: true })); }; recognition.start();", inputField.getElement());
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        // --- Logik beim Klicken auf Senden ---
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                // User-Nachricht anzeigen
                Div userMsg = new Div();
                userMsg.setText("Ich: " + query);
                userMsg.getStyle().set("font-weight", "bold");
                userMsg.getStyle().set("margin-bottom", "10px");
                messageArea.add(userMsg);

                // Backend aufrufen
                String rawBackendResponse = callBackend(query);
                
                // DAS HIER ENTFERNT DAS JSON UND DIE APOSTROPHE:
                String cleanText = extractResponseFromJson(rawBackendResponse);

                // Bot-Nachricht anzeigen
                Div botMsg = new Div();
                botMsg.getStyle().set("background-color", "#F3F4F6"); // Schönes Grau wie im 2. Screenshot
                botMsg.getStyle().set("padding", "15px");
                botMsg.getStyle().set("border-radius", "10px");
                botMsg.getStyle().set("margin-bottom", "20px");
                
                // Text als HTML (wegen Markdown) setzen
                botMsg.getElement().setProperty("innerHTML", convertMarkdownToHtml(cleanText));
                
                messageArea.add(botMsg);

                // UI aktualisieren
                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");
                inputField.clear();
                inputField.getElement().executeJs("this.style.height='auto';");
            } else {
                Notification.show("Bitte gib eine Frage ein!");
            }
        });

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        mainLayout.expand(messageArea);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // --- Hilfsmethode: Holt nur den reinen Text aus dem JSON ---
    private String extractResponseFromJson(String jsonRaw) {
        try {
            // Wir prüfen, ob es wirklich ein JSON ist
            if (jsonRaw != null && jsonRaw.trim().startsWith("{")) {
                JSONObject json = new JSONObject(jsonRaw);
                return json.getString("response"); // Holt den Wert von "response"
            }
        } catch (Exception e) {
            // Falls das Backend mal kein JSON schickt, zeigen wir den Text so an
        }
        return jsonRaw; 
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question=" + URLEncoder.encode(question, StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            return "Fehler: Verbindung zum Server fehlgeschlagen.";
        }
    }
}
