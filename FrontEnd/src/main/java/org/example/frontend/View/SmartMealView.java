package org.example.frontend.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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

        /* ---------------- NAVBAR ---------------- */
        VerticalLayout navBar = new VerticalLayout();
        navBar.setWidth("20%");
        navBar.getStyle().set("background", "#1F2937");
        navBar.setPadding(true);

        Div chatLabel = new Div("Chat-Bot");
        chatLabel.getStyle()
                .set("color", "white")
                .set("font-size", "24px")
                .set("font-weight", "bold");

        navBar.add(chatLabel);

        /* ---------------- MAIN ---------------- */
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        H1 title = new H1("SmartMeal – Rezept Chatbot");

        Div messageArea = new Div();
        messageArea.setWidthFull();
        messageArea.getStyle()
                .set("overflow-y", "auto")
                .set("background", "#FFFFFF")
                .set("padding", "10px");
        messageArea.setText("🤖 Stelle mir eine Frage…");

        TextArea inputField = new TextArea();
        inputField.setPlaceholder("z.B. Ich brauche ein Rezept mit Tomaten");
        inputField.setWidthFull();

        Button sendButton = new Button(new Icon(VaadinIcon.PAPERPLANE));

        HorizontalLayout inputLayout = new HorizontalLayout(inputField, sendButton);
        inputLayout.setWidthFull();
        inputLayout.setFlexGrow(1, inputField);

        /* ---------------- CLICK ---------------- */
        sendButton.addClickListener(e -> {
            String query = inputField.getValue().trim();

            if (query.isEmpty()) {
                Notification.show("Bitte Frage eingeben");
                return;
            }

            String rawResponse = callBackend(query);

            /* ========= UNIVERSALE BEREINIGUNG ========= */
            String cleanResponse = rawResponse;

            // Markdown-Codeblock entfernen
            cleanResponse = cleanResponse.replaceAll("```json", "");
            cleanResponse = cleanResponse.replaceAll("```", "");

            // JSON-Struktur flach machen
            if (cleanResponse.contains("{") && cleanResponse.contains("}")) {
                cleanResponse = cleanResponse
                        .replaceAll("[{}\\[\\]\"]", "")
                        .replaceAll("\\s*,\\s*", "\n")
                        .replaceAll("\\s*:\\s*", ": ");
            }

            cleanResponse = cleanResponse.trim();
            /* ========================================= */

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

        mainLayout.add(title, messageArea, inputLayout);
        mainLayout.expand(messageArea);

        add(navBar, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    /* ---------------- BACKEND CALL ---------------- */
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
