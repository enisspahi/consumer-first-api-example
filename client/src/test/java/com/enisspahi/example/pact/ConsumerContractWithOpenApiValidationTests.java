package com.enisspahi.example.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.pact.PactRequest;
import com.atlassian.oai.validator.pact.PactResponse;
import com.atlassian.oai.validator.pact.ValidatedPactProviderRule;
import com.enisspahi.example.ClientController;
import com.enisspahi.example.RecipesApi;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@PactConsumerTest
@PactTestFor(providerName = ConsumerContractWithOpenApiValidationTests.RECIPES_PROVIDER, pactVersion = PactSpecVersion.V3)
public class ConsumerContractWithOpenApiValidationTests {

    static final String RECIPES_PROVIDER = "RecipesAPI";
    static final String RECIPES_CONSUMER = "RecipesClient";

    private final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator
            .createForSpecificationUrl("https://raw.githubusercontent.com/enisspahi/contract-first-api-example/main/api/src/main/resources/recipes-api.yaml")
            .build();

    private Model model = new RedirectAttributesModelMap();

    @Pact(provider = RECIPES_PROVIDER, consumer = RECIPES_CONSUMER)
    public RequestResponsePact getAllRecipesPact(PactDslWithProvider builder) {
        var pact = builder
                .uponReceiving("GET all recipes")
                    .path("/recipes")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .body(recipesResponseStructure(Optional.empty(), Optional.empty()))
                .toPact();
        validate(pact);
        return pact;
    }

    @Pact(provider = RECIPES_PROVIDER, consumer = RECIPES_CONSUMER)
    public RequestResponsePact getRecipesByTitlePact(PactDslWithProvider builder) {
        var pact = builder
                .uponReceiving("GET pumpkin recipes")
                .path("/recipes")
                .query("title=Pumpkin")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(recipesResponseStructure(Optional.of("Pumpkin"), Optional.empty()))
                .toPact();
        validate(pact);
        return pact;
    }

    @Pact(provider = RECIPES_PROVIDER, consumer = RECIPES_CONSUMER)
    public RequestResponsePact getRecipesByNutritionPact(PactDslWithProvider builder) {
        var pact = builder
                .uponReceiving("GET LOW_CALORIE and HIGH_PROTEIN recipes")
                .path("/recipes")
                .query("nutritionFacts=LOW_CALORIE&nutritionFacts=HIGH_PROTEIN")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(recipesResponseStructure(Optional.empty(), Optional.of(Set.of("LOW_CALORIE", "HIGH_PROTEIN"))))
                .toPact();
        validate(pact);
        return pact;
    }

    @Test
    @PactTestFor(pactMethod = "getAllRecipesPact")
    void getsAllRecipes(MockServer mockServer) {
        var controller = new ClientController(new RecipesApi(mockServer.getUrl()));
        controller.handleSearch(Optional.empty(), Optional.empty(), Optional.empty(), model);

        var searchResult = model.getAttribute("recipes").toString();
        assertTrue(searchResult.contains("title=Chilli sin Carne"));
    }

    @Test
    @PactTestFor(pactMethod = "getRecipesByTitlePact")
    void getsRecipesByTitle(MockServer mockServer) {
        var controller = new ClientController(new RecipesApi(mockServer.getUrl()));
        controller.handleSearch(Optional.of("Pumpkin"), Optional.empty(), Optional.empty(), model);

        var searchResult = model.getAttribute("recipes").toString();
        assertTrue(searchResult.contains("title=Pumpkin"));
        assertFalse(searchResult.contains("title=Chilli sin Carne"));
    }

    @Test
    @PactTestFor(pactMethod = "getRecipesByNutritionPact")
    void getsRecipesByNutrition(MockServer mockServer) {
        var controller = new ClientController(new RecipesApi(mockServer.getUrl()));
        controller.handleSearch(Optional.empty(), Optional.empty(), Optional.of(List.of("LOW_CALORIE", "HIGH_PROTEIN")), model);

        var searchResult = model.getAttribute("recipes").toString();
        assertTrue(searchResult.contains("LOW_CALORIE"));
        assertTrue(searchResult.contains("HIGH_PROTEIN"));
        assertFalse(searchResult.contains("HIGH_CALORIE"));
        assertFalse(searchResult.contains("CARBS"));
    }

    public DslPart recipesResponseStructure(Optional<String> expectedTitle, Optional<Set<String>> expectedNutritionValues) {
        return newJsonArrayMinLike(1, array -> {
            array.object(recipe -> {
                expectedTitle.ifPresentOrElse(
                        expectedValue -> recipe.stringMatcher("title", expectedValue + ".*"),
                        () -> recipe.stringType("title", "Chilli sin Carne")
                );
                recipe.array("ingredients", ingredientsArray -> {
                    ingredientsArray.object(ingredient -> {
                        ingredient.stringType("name", "Kidney beans");
                        ingredient.numberType("quantity", 250);
                        ingredient.stringType("unit", "grams");
                    });
                });
                recipe.numberType("preparationTime", 30);
                recipe.numberType("cookingTime", 15);
                recipe.numberType("servings", 4);
                recipe.array("instructions", instructions -> instructions.stringType("string"));

                recipe.arrayContaining("nutritionFacts", nutritionFacts -> {
                    expectedNutritionValues.ifPresentOrElse(
                            expectedValues -> expectedValues.forEach(expectedValue -> nutritionFacts.stringMatcher(expectedValue, expectedValue)),
                            () -> nutritionFacts.stringType("LOW_CALORIE")
                    );

                });

            });
        }).build();
    }

    private void validate(RequestResponsePact pact) {
        pact.getInteractions().forEach(interaction -> {
            var result = openApiInteractionValidator.validate(PactRequest.of(interaction.asSynchronousRequestResponse().getRequest()), PactResponse.of(interaction.asSynchronousRequestResponse().getResponse()));
            if (result.hasErrors()) {
                throw new ValidatedPactProviderRule.PactValidationError(result);
            }
        });
    }
}
