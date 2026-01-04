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

        // Navbar-Inhalt
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

        Div chatLabel = new Div();
        chatLabel.setText("Chat-Bot");
        chatLabel.getStyle().set("margin-top", "auto");
        chatLabel.getStyle().set("font-size", "24px");
        chatLabel.getStyle().set("font-weight", "bold");
        chatLabel.getStyle().set("color", "white");
        chatLabel.getStyle().set("font-style", "italic");

        navBar.add(logoDiv, spacer, impressumLayout, datenschutzLayout, barriereLayout, chatLabel);

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

        header.add(chatbotButton, ragBotButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        ragBotButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("ragBot"));
        });

        H1 chatTitle = new H1(" Dieser Chat-bot beantwortet Fragen basierend auf der OpenAI-Datenbank");
        chatTitle.addClassName("chat-title");

        Div messageArea = new Div();
        messageArea.setWidthFull();
        messageArea.getStyle().set("overflow-y", "auto");
        messageArea.getStyle().set("padding", "10px");
        messageArea.getStyle().set("background-color", "#FFFFFF");
        messageArea.setText("\uD83E\uDD16 Hier steht die Antwort des LLM...");
        messageArea.getStyle().set("font-style", "italic");

        HorizontalLayout inputLayout = new HorizontalLayout();
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
                        "  this.style.height='auto';" +
                        "  this.style.height=(this.scrollHeight) + 'px';" +
                        "});"
        );

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));
        sendButton.addClassName("send-button");

        Button micButton = new Button(new Icon(VaadinIcon.MICROPHONE));
        micButton.addClassName("mic-button");
        micButton.getElement().setAttribute("title", "Spracheingabe starten");
        micButton.addClickListener(e -> {
            inputField.getElement().executeJs(
                    "if (!('webkitSpeechRecognition' in window)) {" +
                            "  alert('Dein Browser unterstützt keine Spracherkennung');" +
                            "} else {" +
                            "  var recognition = new webkitSpeechRecognition();" +
                            "  recognition.lang = 'de-DE';" +
                            "  recognition.interimResults = false;" +
                            "  recognition.maxAlternatives = 1;" +
                            "  recognition.onresult = function(event) {" +
                            "    let text = event.results[0][0].transcript;" +
                            "    $0.value = text;" +
                            "    $0.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "    $0.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "  };" +
                            "  recognition.start();" +
                            "}"
                    , inputField.getElement());
        });

        inputLayout.add(inputField, sendButton, micButton);
        inputLayout.setFlexGrow(1, inputField);

        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();

            if (query.isEmpty()) {
                Notification.show("Bitte Frage eingeben");
                return;
            }

            String rawResponse = callBackend(query);

            String cleanResponse = rawResponse;
            cleanResponse = cleanResponse.replaceAll("```json", "");
            cleanResponse = cleanResponse.replaceAll("```", "");

            if (cleanResponse.contains("{") && cleanResponse.contains("}")) {
                cleanResponse = cleanResponse
                        .replaceAll("[{}\\[\\]\"]", "")
                        .replaceAll("\\s*,\\s*", "\n")
                        .replaceAll("\\s*:\\s*", ": ");
            }

            cleanResponse = cleanResponse.trim();

            Div userMsg = new Div("User Frage: " + query);
            userMsg.getStyle().set("font-weight", "bold");

            Div botMsg = new Div("Antwort:\n" + cleanResponse);
            botMsg.getStyle()
                    .set("white-space", "pre-wrap")
                    .set("background", "#E5E7EB")
                    .set("padding", "10px")
                    .set("border-radius", "6px");

            messageArea.add(userMsg, botMsg);
            messageArea.getElement().executeJs("this.scrollTop = this.scrollHeight");

            inputField.clear();
        });

        // ✅ Hier die Korrektur: title → chatTitle
        mainLayout.add(header, chatTitle, messageArea, inputLayout);
        mainLayout.expand(messageArea);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    private String callBackend(String question) {
        try {
            String urlString = "http://localhost:8070/askLLM?question=" +
                    java.net.URLEncoder.encode(question, "UTF-8");

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
            return "Fehler beim Backend-Aufruf.";
        }
    }
}
