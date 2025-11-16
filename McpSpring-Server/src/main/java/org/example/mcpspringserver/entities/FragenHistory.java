package org.example.mcpspringserver.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor

@Getter
@Setter

@Entity
@Table(name = "qa_history")
public class FragenHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;



    public FragenHistory(String frage, String antwort) {
        this.question = frage;
        this.answer = antwort;
    }


}
