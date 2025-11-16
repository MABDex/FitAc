package org.example.mcpspringserver;

import org.example.mcpspringserver.entities.IngrediantPrice;
import org.example.mcpspringserver.repository.IngredientPriceRepository;
import org.example.mcpspringserver.tools.ServerQueryAgent;
import org.example.mcpspringserver.tools.ToolsInfos;

import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class McpSpringServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSpringServerApplication.class, args);
    }

    /*
 MethodToolCallbackProvider :
  Le Bean Le plus important pour que les functions aver Toll annotation
 */
    @Bean
    public MethodToolCallbackProvider methodToolCallbackProvider(ToolsInfos toolsInfos) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(toolsInfos)
                .build();

    }






    //@Bean
    CommandLineRunner start(IngredientPriceRepository ingredientRepository) {
        return args -> {
            ingredientRepository.save(new IngrediantPrice(null, "fennel seeds", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "green olives", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "ripe olives", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "garlic", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "peppercorn", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "orange rind", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "orange juice", 1.2));
            ingredientRepository.save(new IngrediantPrice(null, "red chile", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "extra virgin olive oil", 5.0));

            ingredientRepository.save(new IngrediantPrice(null, "pork spareribs", 10.0));
            ingredientRepository.save(new IngrediantPrice(null, "soy sauce", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "fresh garlic", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "fresh ginger", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "chili powder", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "fresh coarse ground black pepper", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "salt", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "fresh cilantro leaves", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "tomato sauce", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "brown sugar", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "yellow onion", 0.8));
            ingredientRepository.save(new IngrediantPrice(null, "white vinegar", 1.2));
            ingredientRepository.save(new IngrediantPrice(null, "honey", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "a.1. original sauce", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "liquid smoke", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "cracked black pepper", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "cumin", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "dry mustard", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "cinnamon sticks", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "orange, juice of", 1.2));
            ingredientRepository.save(new IngrediantPrice(null, "mirin", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "water", 0.0));

            ingredientRepository.save(new IngrediantPrice(null, "chocolate sandwich style cookies", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "chocolate syrup", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "vanilla ice cream", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "bananas", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "strawberry ice cream", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "whipped cream", 2.0));

            ingredientRepository.save(new IngrediantPrice(null, "sugar", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "unsalted butter", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "eggs", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "fresh lemon juice", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "cake flour", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "baking soda", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "salt", 0.5));

            ingredientRepository.save(new IngrediantPrice(null, "whole berry cranberry sauce", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "sour cream", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "prepared horseradish", 2.0));

            ingredientRepository.save(new IngrediantPrice(null, "vanilla wafers", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "butter", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "powdered sugar", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "whipping cream", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "strawberry", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "walnuts", 3.0));

            ingredientRepository.save(new IngrediantPrice(null, "great northern bean", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "chicken bouillon cubes", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "dark brown sugar", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "molasses", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "cornstarch", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "onion", 0.8));
            ingredientRepository.save(new IngrediantPrice(null, "garlic powder", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "mustard powder", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "chili powder", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "black pepper", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "bacon", 4.0));
            ingredientRepository.save(new IngrediantPrice(null, "water", 0.0));


            ingredientRepository.save(new IngrediantPrice(null, "ground sirloin", 8.0));
            ingredientRepository.save(new IngrediantPrice(null, "bread", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "onions", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "worcestershire sauce", 3.0));
            ingredientRepository.save(new IngrediantPrice(null, "egg", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "salt and pepper", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "seasoned stuffing mix", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "shortening", 2.0));

            ingredientRepository.save(new IngrediantPrice(null, "onion soup mix", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "seasoning salt", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "brown gravy mix", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "water", 0.0));

            ingredientRepository.save(new IngrediantPrice(null, "puff pastry", 4.0));
            ingredientRepository.save(new IngrediantPrice(null, "butter", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "mushrooms", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "onion", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "sirloin", 9.0));
            ingredientRepository.save(new IngrediantPrice(null, "parsley", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "basil", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "salt", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "pepper", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "eggs", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "liver pate", 3.5));

            ingredientRepository.save(new IngrediantPrice(null, "olive oil", 5.0));
            ingredientRepository.save(new IngrediantPrice(null, "ground turkey", 7.0));
            ingredientRepository.save(new IngrediantPrice(null, "garlic", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "onion", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "paprika", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "thyme", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "nonfat beef broth", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "medium egg noodles", 2.5));
            ingredientRepository.save(new IngrediantPrice(null, "fat free sour cream", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "flour", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "broccoli florets", 2.0));

            ingredientRepository.save(new IngrediantPrice(null, "fennel seed", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "lawry's seasoned salt", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "long grain rice", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "celery", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "green pepper", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "carrots", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "chicken broth", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "vegetable oil cooking spray", 2.0));

            ingredientRepository.save(new IngrediantPrice(null, "tomato juice", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "red potatoes", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "white beans", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "diced tomatoes", 1.5));
            ingredientRepository.save(new IngrediantPrice(null, "dried oregano", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "dried basil", 1.0));
            ingredientRepository.save(new IngrediantPrice(null, "old bay seasoning", 2.0));
            ingredientRepository.save(new IngrediantPrice(null, "bay leaves", 0.5));
            ingredientRepository.save(new IngrediantPrice(null, "cheese", 2.5));


        };
    }




//    @Bean
//    public MethodToolCallbackProvider methodToolCallbackProvider(ToolsInfos toolsInfos) {
//        return MethodToolCallbackProvider.builder()
//                .toolObjects(new ToolsInfos(toolsInfos)).build();
//        //Smyt class li fih tools
//
//    }
}
