package com.enisspahi.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    private final RecipesApi recipesApi;

    public ClientController(RecipesApi recipesApi) {
        this.recipesApi = recipesApi;
    }

    @GetMapping("/")
    public String defaultPage() {
        return "search";
    }

    @PostMapping("/searchRecipe")
    public String handleSearch(@RequestParam Optional<String> title,
                               @RequestParam Optional<List<String>> ingredients,
                               @RequestParam Optional<List<String>> nutritionFacts,
                               Model model) {
        var recipes = getRecipes(title, ingredients, nutritionFacts);
        model.addAttribute("recipes", recipes);
        return "result";
    }

    private List<RecipesApi.Recipe> getRecipes(Optional<String> title, Optional<List<String>> ingredients, Optional<List<String>> nutritionFacts) {
        return recipesApi.findRecipes(title, ingredients, nutritionFacts);
    }
}
