package org.example.mcpspringserver.repository;

import org.example.mcpspringserver.entities.IngrediantPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientPriceRepository extends JpaRepository <IngrediantPrice ,Long>{

    IngrediantPrice findByName(String name);
    List<IngrediantPrice> findByNameIn(List<String> names);

}
