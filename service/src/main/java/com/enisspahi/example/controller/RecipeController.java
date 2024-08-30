package com.enisspahi.example.controller;

import com.enisspahi.example.model.Recipe;
import com.enisspahi.example.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.store(recipe);
    }

    @GetMapping("/recipes")
    public List<Recipe> findRecipes(@RequestParam Optional<String> title,
                                    @RequestParam Optional<List<String>> ingredients,
                                    @RequestParam Optional<List<Recipe.NutritionFact>> nutritionFacts) {
        return recipeService.search(title, ingredients.orElse(Collections.emptyList()), nutritionFacts.orElse(Collections.emptyList()));
    }

}
