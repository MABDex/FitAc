package org.example.mcpspringserver.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor

@Getter
@Setter

@Table(name = "ingrediant_price")
public class IngrediantPrice {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;



}
