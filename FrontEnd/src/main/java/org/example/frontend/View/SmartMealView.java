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

// Markdown Imports
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

        // --- Linke Navigationsleiste ---
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);
        navBar.setSpacing(true);
        navBar.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        // Logo Bereich
        Div logoDiv = new Div();
        logoDiv.setWidth("250px");
        logoDiv.setHeight("100px");
        logoDiv.getStyle().set("background-image", "url('logo2.png')");
        logoDiv.getStyle().set("background-size", "contain");
        logoDiv.getStyle().set("background-repeat", "no-repeat");
        logoDiv.getStyle().set("background-position", "center");

        Div spacer = new Div();
        spacer.setHeight("100px");

      

        // Links mit Icons
        Anchor impressumLink = new Anchor("https://www.fit.fraunhofer.de/de/impressum.html", "Impressum");
        impressumLink.setTarget("_blank");
        HorizontalLayout impressumLayout = new HorizontalLayout(new Icon(VaadinIcon.CLOCK), impressumLink);
        impressumLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        impressumLayout.getChildren().findFirst().ifPresent(icon -> ((Icon)icon).setSize("14px"));

        Anchor datenschutzLink = new Anchor("https://www.fit.fraunhofer.de/de/datenschutzerklaerung.html", "Datenschutzinformation");
        datenschutzLink.setTarget("_blank");
        HorizontalLayout datenschutzLayout = new HorizontalLayout(new Icon(VaadinIcon.OPEN_BOOK), datenschutzLink);
        datenschutzLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        datenschutzLayout.getChildren().findFirst().ifPresent(icon -> ((Icon)icon).setSize("14px"));

        Anchor barrierefreiheitLink = new Anchor("https://www.fit.fraunhofer.de/de/barrierefreiheitserklaerung.html", "Barrierefreiheitserklärung");
        barrierefreiheitLink.setTarget("_blank");
        HorizontalLayout barriereLayout = new HorizontalLayout(new Icon(VaadinIcon.CHECK_CIRCLE), barrierefreiheitLink);
        barriereLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        barriereLayout.getChildren().findFirst().ifPresent(icon -> ((Icon)icon).setSize("14px"));

        Div chatLabel = new Div("Chat-Bot");
        chatLabel.getStyle().set("margin-top", "auto").set("font-size", "24px")
                 .set("font-weight", "bold").set("color", "white").set("font-style", "italic");

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
        chatbotButton.addClassName("button-selected"); // Aktiv markieren

        Button ragBotButton = new Button("RAG-Bot", e -> getUI().ifPresent(ui -> ui.navigate("ragBot")));
        ragBotButton.addClassName("button-ragbot");

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Titel
        H1 chatTitle = new H1("Dieser Chat-Bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        // Nachrichtenbereich
        Div messageArea = new Div();
        messageArea.addClassName("chat-area");
        messageArea.setWidthFull();

        Div placeholder = new Div("🤖 Hier steht die Antwort des LLM...");
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

        // Mikrofon Button (identisch zu RagBot)
        Button micButton = new Button(new Icon(VaadinIcon.MICROPHONE));
        micButton.addClassName("mic-button");
        micButton.addClickListener(e -> {
            inputField.getElement().executeJs(
                    "if (!('webkitSpeechRecognition' in window)) { alert('Browser Support fehlt'); } " +
                    "else { var recognition = new webkitSpeechRecognition(); recognition.lang = 'de-DE'; " +
                    "recognition.onresult = function(event) { $0.value = event.results[0][0].transcript; " +
                    "$0.dispatchEvent(new Event('input', { bubbles: true })); }; recognition.start(); }", 
                    inputField.getElement());
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        // Send-Logik
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();
            if (!query.isEmpty()) {
                if (messageArea.getChildren().anyMatch(c -> c == placeholder)) messageArea.remove(placeholder);

                Div userMsg = new Div("User Frage: " + query);
                userMsg.getStyle().set("font-weight", "bold").set("margin-bottom", "10px");
                messageArea.add(userMsg);

                String markdownAnswer = callBackend(query);
                String html = convertMarkdownToHtml(markdownAnswer);

                Div botMsg = new Div();
                botMsg.addClassName("bot-message-markdown");
                botMsg.getElement().setProperty("innerHTML", html);
                messageArea.add(botMsg);

                messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");
                inputField.clear();
                inputField.getElement().executeJs("this.style.height='auto';");
            } else {
                Notification.show("Bitte geben Sie eine Frage ein!");
            }
        });

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question=" + URLEncoder.encode(question, StandardCharsets.UTF_8);
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line).append("\n");
            in.close();
            return response.toString();
        } catch (Exception e) {
            return "**Fehler:** Backend nicht erreichbar.";
        }
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
