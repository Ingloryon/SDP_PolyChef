package ch.epfl.polychef.recipe;

import org.mockito.Mockito;

import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.utils.SearchTest;

import static org.mockito.Mockito.when;

public class SearchRecipeTest extends SearchTest {
    SearchRecipe spyRecipeSearch;

    Recipe recipe0 = getBuilder().setName("123456") .build();

    Recipe recipe1 = getBuilder().addIngredient("salt", 420, Ingredient.Unit.KILOGRAM)
            .setName("34").build();

    Recipe recipe2 = getBuilder().setName("43-aBcD").build();

    Recipe[] recipes = {recipe0, recipe1, recipe2};

    @Override
    public void initTests() {
        dbName = RecipeStorage.DB_NAME;

        super.initTests();

        spyRecipeSearch = Mockito.spy(SearchRecipe.getInstance());
        when(spyRecipeSearch.getDatabase()).thenReturn(mockDataBase);
    }

    @Override
    public void callSearch1(String query, CallHandler<List<Miniatures>> caller) {
        spyRecipeSearch.searchForRecipe(query, caller);
    }

    @Override
    public void callSearch2(String ingredient, CallHandler<List<Miniatures>> caller) {
        spyRecipeSearch.searchRecipeByIngredient(ingredient, caller);
    }

    @Override
    public Class getMiniatureClass() {
        return Recipe.class;
    }

    @Override
    public Miniatures getMiniature(int index) {
        if(0 <= index && index < 3){
            return recipes[index];
        }
        return getBuilder().setName("other recipe").build();
    }

    private RecipeBuilder getBuilder(){
        return new RecipeBuilder()
                .addInstruction("Yay")
                .setAuthor("testAuthor")
                .setPersonNumber(6)
                .setRecipeDifficulty(Recipe.Difficulty.VERY_HARD)
                .addIngredient("Mockitooo", 42, Ingredient.Unit.KILOGRAM)
                .setEstimatedPreparationTime(1000)
                .setEstimatedCookingTime(1000);
    }
}
