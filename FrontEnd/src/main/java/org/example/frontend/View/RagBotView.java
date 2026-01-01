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
@Route("ragBot")
@CssImport("./styles/shared-styles.css")
public class RagBotView extends HorizontalLayout {

    public RagBotView() {
        setSizeFull();

        // --- Linke Navigationsleiste ---
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);
        navBar.setSpacing(true);
        navBar.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        // Logo Bereich
        Image img = new Image("logo2.png", "de");
        img.setWidth("150px");
        img.setHeight("80px");

        Div logoDiv = new Div();
        logoDiv.setWidth("250px");
        logoDiv.setHeight("100px");
        logoDiv.getStyle().set("background-image", "url('logo2.png')");
        logoDiv.getStyle().set("background-size", "contain");
        logoDiv.getStyle().set("background-repeat", "no-repeat");
        logoDiv.getStyle().set("background-position", "center");

        Div spacer = new Div();
        spacer.setHeight("100px");

        // ===== Price-L Button (NEU) =====
       Button priceLButton = new Button("Price-L", new Icon(VaadinIcon.TRENDING_UP));
       priceLButton.addClassName("nav-button");
       priceLButton.addClassName("nav-button-yellow");

       priceLButton.addClickListener(e ->
        getUI().ifPresent(ui ->
                ui.getPage().open(
                        "https://app.oprfs.org/ingredient-price",
                        "_blank"
                )
              )
            );
// === Ende Price-L Button =====


        // Links
        Anchor impressumLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Impressum");
        impressumLink.setTarget("_blank");

        Anchor datenschutzLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Datenschutzinformation");
        datenschutzLink.setTarget("_blank");

        Anchor barrierefreiheitLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Barrierefreiheitserklärung");
        barrierefreiheitLink.setTarget("_blank");

        // Icons & Layouts für Links
        Icon checkIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
        checkIcon.setSize("14px");
        checkIcon.getStyle().set("margin-left", "5px");
        HorizontalLayout barriereLayout = new HorizontalLayout(checkIcon, barrierefreiheitLink);
        barriereLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon bookIcon = new Icon(VaadinIcon.OPEN_BOOK);
        bookIcon.setSize("14px");
        bookIcon.getStyle().set("margin-left", "5px");
        HorizontalLayout datenschutzLayout = new HorizontalLayout(bookIcon, datenschutzLink);
        datenschutzLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon caretIcon = new Icon(VaadinIcon.CLOCK);
        caretIcon.setSize("14px");
        caretIcon.getStyle().set("margin-left", "5px");
        HorizontalLayout impressumLayout = new HorizontalLayout(caretIcon, impressumLink);
        impressumLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Div ragLabel = new Div();
        ragLabel.setText("RAG-Bot");
        ragLabel.getStyle().set("margin-top", "auto");
        ragLabel.getStyle().set("font-size", "24px");
        ragLabel.getStyle().set("font-weight", "bold");
        ragLabel.getStyle().set("color", "white");
        ragLabel.getStyle().set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressumLayout, datenschutzLayout, barriereLayout, ragLabel);

        // --- Hauptbereich ---
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("chat-header");
        header.setWidthFull();

        Button chatbotButton = new Button("Chat-Bot", e -> getUI().ifPresent(ui -> ui.navigate("smartMeal")));
        chatbotButton.addClassName("button-chatbot");

        Button ragBotButton = new Button("RAG-Bot");
        ragBotButton.addClassName("button-ragbot");
        ragBotButton.addClassName("button-selected");

        header.add(chatbotButton, ragBotButton ,  priceLButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Titel
        H1 chatTitle = new H1("Dieser RAG-Bot beantwortet Fragen zu Rezepten basierend auf unserer internen Datenbank");
        chatTitle.addClassName("chat-title");

        // Nachrichtenbereich
        Div messageArea = new Div();
        messageArea.addClassName("chat-area"); // Klasse aus CSS nutzen
        messageArea.setWidthFull();
        // Standard-Text
        Div placeholder = new Div();
        placeholder.setText("\uD83E\uDD16 Hier steht die Antwort des LLM...");
        placeholder.getStyle().set("font-style", "italic");
        messageArea.add(placeholder);

        mainLayout.expand(messageArea);

        // Eingabefeld
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.addClassName("input-layout");
        inputLayout.setWidthFull();

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.addClassName("input-field");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

        // JavaScript für dynamische Höhe
        inputField.getElement().executeJs(
                "this.style.height='auto';" +
                        "this.style.overflowY='hidden';" +
                        "this.addEventListener('input', function() {" +
                        "  this.style.height='auto';" +
                        "  this.style.height=(this.scrollHeight) + 'px';" +
                        "});"
        );

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        // Mikrofon Button
        Button micButton = new Button(new Icon(VaadinIcon.MICROPHONE));
        micButton.addClassName("mic-button");
        micButton.getElement().setAttribute("title", "Spracheingabe starten");
        micButton.addClickListener(e -> {
            inputField.getElement().executeJs(
                    "if (!('webkitSpeechRecognition' in window)) {" +
                            "  alert('Dein Browser unterstützt keine Spracherkennung');" +
                            "} else {" +
                            "  var recognition = new webkitSpeechRecognition();" +
                            "  recognition.lang = 'de-DE';" +
                            "  recognition.interimResults = false;" +
                            "  recognition.maxAlternatives = 1;" +
                            "  recognition.onresult = function(event) {" +
                            "    let text = event.results[0][0].transcript;" +
                            "    $0.value = text;" +
                            "    $0.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "    $0.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "  };" +
                            "  recognition.start();" +
                            "}"
                    , inputField.getElement());
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        // --- Klick-Listener ---
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                // Platzhalter entfernen beim ersten Mal
                if (messageArea.getChildren().anyMatch(c -> c == placeholder)) {
                    messageArea.remove(placeholder);
                }

                // User-Nachricht anzeigen
                Div userMessage = new Div();
                userMessage.setText("User Frage: " + query);
                userMessage.getStyle().set("font-weight", "bold");
                userMessage.getStyle().set("margin-bottom", "10px");
                userMessage.getStyle().set("color", "#333");
                messageArea.add(userMessage);

                // Backend aufrufen (Markdown Text holen)
                String answerMarkdown = callBackend(query);

                // Markdown zu HTML konvertieren
                String htmlContent = convertMarkdownToHtml(answerMarkdown);

                // Bot-Antwort anzeigen (HTML rendern)
                Div botMessage = new Div();
                botMessage.addClassName("bot-message-markdown"); // CSS Klasse für Styling
                botMessage.getElement().setProperty("innerHTML", htmlContent);
                
                messageArea.add(botMessage);

                // Scrollen nach unten
                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");

                // Input leeren
                inputField.clear();
                inputField.getElement().executeJs("this.style.height='auto';");
            } else {
                Notification.show("Bitte geben Sie eine Frage ein!");
            }
        });

        // Layout zusammenbauen
        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        
        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // --- Hilfsmethode: Backend Aufruf ---
    private String callBackend(String query) {
        try {
            String urlString = "http://localhost:8066/chat?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n"); // Newline wichtig für Markdown
            }
            in.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "**Fehler:** Die Antwort konnte nicht vom Backend geladen werden.";
        }
    }

    // --- Hilfsmethode: Markdown zu HTML ---
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
