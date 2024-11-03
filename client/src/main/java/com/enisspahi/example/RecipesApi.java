package com.enisspahi.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class RecipesApi {

    private final String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public RecipesApi(@Value("${recipes-api-base-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Recipe> findRecipes(Optional<String> title,
                                    Optional<List<String>> ingredients,
                                    Optional<List<String>> nutritionFacts) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/recipes");

        title.ifPresent(param -> uriBuilder.queryParam("title", param));
        ingredients.ifPresent(param -> uriBuilder.queryParam("ingredients", param));
        nutritionFacts.ifPresent(param -> uriBuilder.queryParam("nutritionFacts", param));

        URI uri = uriBuilder.build().encode().toUri();
        Recipe[] response = restTemplate.getForObject(uri, Recipe[].class);

        return List.of(response);
    }

    public record Recipe(String title,
                         List<Ingredient> ingredients,
                         Integer preparationTime,
                         Integer cookingTime,
                         Integer servings,
                         List<String> instructions,
                         List<NutritionFact> nutritionFacts) {

        public record Ingredient(String name, Double quantity, String unit) { }

        public enum NutritionFact { LOW_CALORIE, HIGH_CALORIE, HIGH_PROTEIN, CARBS }

    }
}


