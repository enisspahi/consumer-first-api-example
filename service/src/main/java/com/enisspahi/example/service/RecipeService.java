package com.enisspahi.example.service;

import com.enisspahi.example.repository.RecipeRepository;
import com.enisspahi.example.model.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> search(Optional<String> title, List<String> ingredients, List<Recipe.NutritionFact> nutritionFacts) {
        return recipeRepository.findBy(title, ingredients, nutritionFacts);
    }

    public Recipe store(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

}
