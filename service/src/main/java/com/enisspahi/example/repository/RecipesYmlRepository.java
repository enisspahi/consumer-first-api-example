package com.enisspahi.example.repository;

import com.enisspahi.example.model.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
class RecipesYmlRepository implements RecipesRepository {

    private final List<Recipe> recipes = YamlSourceReader.readFromYaml();

    @Override
    public Recipe save(Recipe recipe) {
        recipes.add(recipe);
        return recipe;
    }

    @Override
    public List<Recipe> findBy(Optional<String> title, List<String> ingredients, List<Recipe.NutritionFact> nutritionFacts) {
        return recipes.stream()
                .filter(filterByTitle(title))
                .filter(filterByIngredients(ingredients))
                .filter(filterByNutritionFacts(nutritionFacts))
                .collect(Collectors.toList());
    }


    private static Predicate<Recipe> filterByTitle(Optional<String> title) {
        return recipe -> title.map(t -> recipe.title().toLowerCase().contains(t.toLowerCase())).orElse(true);
    }

    private static Predicate<Recipe> filterByIngredients(List<String> ingredients) {
        return recipe -> ingredients.stream()
                .map(String::toLowerCase)
                .allMatch(searchedIngredient ->
                        recipe.ingredients().stream()
                                .map(Recipe.Ingredient::name)
                                .map(String::toLowerCase)
                                .anyMatch(ingredientOfRecipe -> ingredientOfRecipe.contains(searchedIngredient))
                );
    }

    private static Predicate<Recipe> filterByNutritionFacts(List<Recipe.NutritionFact> nutritionFacts) {
        return recipe -> nutritionFacts.stream()
                .allMatch(searchedNutritionFact ->
                        recipe.nutritionFacts().stream()
                                .anyMatch(nutritionFactOfRecipe -> nutritionFactOfRecipe == searchedNutritionFact)
                );
    }

}
