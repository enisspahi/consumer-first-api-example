package com.enisspahi.example.repository;

import com.enisspahi.example.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    List<Recipe> findBy(Optional<String> title, List<String> ingredients, List<Recipe.NutritionFact> nutritionFacts);
}
