package com.enisspahi.example.repository;

import com.enisspahi.example.model.Recipe;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

final class YamlSourceReader {

    static List<Recipe> readFromYaml() {
        var yaml = new Yaml();
        InputStream inputStream = RecipeYmlRepository.class.getResourceAsStream("/recipes/recipes.yml");
        RecipesDTOWrapper recipesDTOList = yaml.loadAs(inputStream, RecipesDTOWrapper.class);
        return recipesDTOList.recipes.stream()
                .map(YamlSourceReader::toAPIModel)
                .collect(Collectors.toList());
    }

    private static Recipe toAPIModel(RecipesDTOWrapper.RecipeDTO dto) {
        return new Recipe(
                dto.title,
                dto.ingredients.stream()
                        .map(ingredientDTO -> new Recipe.Ingredient(ingredientDTO.name, ingredientDTO.quantity, ingredientDTO.unit))
                        .collect(Collectors.toList()),
                dto.preparationTime,
                dto.cookingTime,
                dto.servings,
                dto.instructions,
                dto.nutritionFacts.stream().map(Recipe.NutritionFact::valueOf).collect(Collectors.toList()));
    }

    @Data
    public static class RecipesDTOWrapper {
        private List<RecipeDTO> recipes;

        @Data
        public static class RecipeDTO {
            private String title;
            private List<IngredientDTO> ingredients;
            private Integer preparationTime;
            private Integer cookingTime;
            private Integer servings;
            private List<String> instructions;
            private List<String> nutritionFacts;

            @Data
            public static class IngredientDTO {
                private String name;
                private Double quantity;
                private String unit;
            }

        }

    }


}
