package com.enisspahi.example.model;

import java.util.List;

public record Recipe(String title,
                     List<Ingredient> ingredients,
                     Integer preparationTime,
                     Integer cookingTime,
                     Integer servings,
                     List<String> instructions,
                     List<NutritionFact> nutritionFacts) {

    public record Ingredient(String name,
                             Double quantity,
                             String unit) {
    }

    public enum NutritionFact {LOW_CALORIE, HIGH_CALORIE, HIGH_PROTEIN, CARBS}

}


