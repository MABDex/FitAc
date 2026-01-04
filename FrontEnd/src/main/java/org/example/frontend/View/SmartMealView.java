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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringComponent
@UIScope
@Route("smartMeal")
@CssImport("./styles/shared-styles.css")
public class SmartMealView extends HorizontalLayout {

    public SmartMealView() {
        setSizeFull();

        // Linke Navigationsleiste
        VerticalLayout navBar = new VerticalLayout();
        navBar.addClassName("navbar");
        navBar.setWidth("20%");
        navBar.setPadding(true);
        navBar.setSpacing(true);
        navBar.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        Image img = new Image("fit.png", "logo");
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

        Anchor impressumLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Impressum");
        impressumLink.setTarget("_blank");

        Anchor datenschutzLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Datenschutzinformation");
        datenschutzLink.setTarget("_blank");

        Anchor barrierefreiheitLink = new Anchor("https://www.fit.fraunhofer.de/de/jobs.html", "Barrierefreiheitserklärung");
        barrierefreiheitLink.setTarget("_blank");

        HorizontalLayout impressumLayout = new HorizontalLayout(new Icon(VaadinIcon.CLOCK), impressumLink);
        HorizontalLayout datenschutzLayout = new HorizontalLayout(new Icon(VaadinIcon.OPEN_BOOK), datenschutzLink);
        HorizontalLayout barriereLayout = new HorizontalLayout(new Icon(VaadinIcon.CHECK_CIRCLE), barrierefreiheitLink);

        Div chatLabel = new Div("Chat-Bot");
        chatLabel.getStyle()
                .set("margin-top", "auto")
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressumLayout, datenschutzLayout, barriereLayout, chatLabel);

        // Hauptbereich
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("chat-header");
        header.setWidthFull();

        Button chatbotButton = new Button("Chat-Bot");
        Button ragBotButton = new Button("RAG-Bot");

        ragBotButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("ragBot"))
        );

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 chatTitle = new H1("Dieser Chat-bot beantwortet Fragen basierend auf der OpenAI-Datenbank");

        Div messageArea = new Div();
        messageArea.setWidthFull();
        messageArea.getStyle().set("overflow-y", "auto");
        messageArea.getStyle().set("padding", "10px");
        messageArea.getStyle().set("background-color", "#FFFFFF");
        messageArea.setText("🤖 Hier steht die Antwort des LLM...");
        messageArea.getStyle().set("font-style", "italic");

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("Wie kann ich dir weiterhelfen?");
        inputField.setWidthFull();
        inputField.getStyle().set("resize", "none");

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));

        HorizontalLayout inputLayout = new HorizontalLayout(inputField, sendButton);
        inputLayout.setWidthFull();
        inputLayout.setFlexGrow(1, inputField);

        // Klick-Listener
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();

            if (query.isEmpty()) {
                Notification.show("Bitte geben Sie eine Frage ein!");
                return;
            }

            String jsonResponse = callBackend(query);
            String cleanResponse = cleanLLMResponse(jsonResponse);

            Div userMessage = new Div("User Frage: " + query);
            userMessage.getStyle().set("font-weight", "bold");

            Div botMessage = new Div("Antwort:\n" + cleanResponse);
            botMessage.getStyle()
                    .set("white-space", "pre-wrap")
                    .set("background-color", "#E5E7EB")
                    .set("padding", "8px")
                    .set("border-radius", "6px");

            messageArea.add(userMessage, botMessage);
            messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");

            inputField.clear();
        });

        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        mainLayout.expand(messageArea);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    // Backend-Aufruf
    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question="
                    + java.net.URLEncoder.encode(question, "UTF-8");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Abrufen der Antwort vom Backend.";
        }
    }

    // ✅ NEUE METHODE: Bereinigt JSON / ```json / Markdown
    private String cleanLLMResponse(String rawResponse) {
        if (rawResponse == null) return "";

        String cleaned = rawResponse;

        cleaned = cleaned.replaceAll("```json", "");
        cleaned = cleaned.replaceAll("```", "");

        if (cleaned.contains("{") && cleaned.contains("}")) {
            cleaned = cleaned
                    .replaceAll("[{}\\[\\]\"]", "")
                    .replaceAll("\\s*,\\s*", "\n")
                    .replaceAll("\\s*:\\s*", ": ");
        }

        return cleaned.trim();
    }
}
