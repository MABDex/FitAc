package org.example.frontend.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
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

        // ===== NAVBAR =====
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);

        Div logoDiv = new Div();
        logoDiv.setWidth("250px");
        logoDiv.setHeight("100px");
        logoDiv.getStyle()
                .set("background-image", "url('logo2.png')")
                .set("background-size", "contain")
                .set("background-repeat", "no-repeat")
                .set("background-position", "center");

        Div spacer = new Div();
        spacer.setHeight("100px");

        Anchor impressum = new Anchor("https://www.fit.fraunhofer.de/de/impressum.html", "Impressum");
        impressum.setTarget("_blank");

        Anchor datenschutz = new Anchor("https://www.fit.fraunhofer.de/de/impressum.html", "Datenschutzinformation");
        datenschutz.setTarget("_blank");

        Anchor barriere = new Anchor("https://www.fit.fraunhofer.de/de/impressum.html", "Barrierefreiheitserklärung");
        barriere.setTarget("_blank");

        Div chatLabel = new Div("Chat-Bot");
        chatLabel.getStyle()
                .set("margin-top", "auto")
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressum, datenschutz, barriere, chatLabel);

        // ===== MAIN LAYOUT =====
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

        Button ragBotButton = new Button("RAG-Bot",
                e -> getUI().ifPresent(ui -> ui.navigate("ragBot")));
        ragBotButton.addClassName("button-ragbot");

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Title
        H1 chatTitle = new H1("Dieser Chat-Bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        // ===== CHAT AREA =====
        Div messageArea = new Div();
        messageArea.addClassName("chat-area");

        Div placeholder = new Div("🤖 Hier steht die Antwort des LLM...");
        placeholder.getStyle().set("font-style", "italic");
        messageArea.add(placeholder);

        mainLayout.expand(messageArea);

        // ===== INPUT =====
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.addClassName("input-layout");
        inputLayout.setWidthFull();

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.addClassName("input-field");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

        inputField.getElement().executeJs(
                "this.style.height='auto';" +
                "this.style.overflowY='hidden';" +
                "this.addEventListener('input', function() {" +
                "this.style.height='auto';" +
                "this.style.height=(this.scrollHeight) + 'px';});"
        );

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        inputLayout.add(inputField, sendButton);
        inputLayout.setFlexGrow(1, inputField);

        // ===== SEND BUTTON =====
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();

            if (query.isEmpty()) {
                Notification.show("Bitte geben Sie eine Frage ein!");
                return;
            }

            if (messageArea.getChildren().anyMatch(c -> c == placeholder)) {
                messageArea.remove(placeholder);
            }

            Div userMsg = new Div("User Frage: " + query);
            userMsg.getStyle().set("font-weight", "bold");
            messageArea.add(userMsg);

            String markdownAnswer = callBackend(query);
            String html = convertMarkdownToHtml(markdownAnswer);

            Div botMsg = new Div();
            botMsg.addClassName("bot-message-markdown");
            botMsg.getElement().setProperty("innerHTML", html);

            messageArea.add(botMsg);
            messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");

            inputField.clear();
        });

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // ===== BACKEND CALL =====
    private String callBackend(String question) {
        try {
            String urlString =
                    "http://localhost:8070/askLLM?question=" +
                    URLEncoder.encode(question, StandardCharsets.UTF_8);

            HttpURLConnection conn =
                    (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            in.close();
            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "**Fehler:** Backend nicht erreichbar.";
        }
    }

    // ===== MARKDOWN → HTML =====
    private String convertMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
