package ch.epfl.polychef.recipe;

import ch.epfl.polychef.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This is a Singleton class of the offline recipes that can't be modified
 */
public final class OfflineRecipes {
    private static final OfflineRecipes offlineRecipesInstance = new OfflineRecipes();

    private List<Recipe> offlineRecipes;

    public static OfflineRecipes getInstance(){
        return offlineRecipesInstance;
    }

    private OfflineRecipes(){
        offlineRecipes = new ArrayList<>();
        offlineRecipes.add(recipe1);
        offlineRecipes.add(recipe2);
        offlineRecipes.add(recipe3);
        offlineRecipes.add(recipe4);
        offlineRecipes.add(recipe5);
    }

    public List<Recipe> getCopy() {
        List<Recipe> copiedList = new ArrayList<>();
        for(Recipe recipe : offlineRecipes){
            try {
                copiedList.add((Recipe) recipe.clone());
            }catch(CloneNotSupportedException e){
                e.printStackTrace();
            }
        }
        // TODO Should we make it unmodifiable or not?  --> yes (#Guillaume)
        return Collections.unmodifiableList(copiedList);
    }

    private Recipe recipe1 = new RecipeBuilder()
            .setName("Oven-Baked Salmon")
            .setRecipeDifficulty(Recipe.Difficulty.HARD)
            .setEstimatedCookingTime(15)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(5)
            .addIngredient("Salmon fillet, cut into 4 pieces", 12, Ingredient.Unit.OUNCE)
            .addIngredient("Coarse-grained salt", 0, Ingredient.Unit.NONE)
            .addIngredient("Freshly ground black pepper", 0, Ingredient.Unit.NONE)
            .addIngredient("Baked squash, for serving, optional", 0, Ingredient.Unit.NONE)
            .addInstruction("Preheat the oven to 450 degrees F. ")
            .addInstruction("Season salmon with salt and pepper. Place salmon, skin side down, on a non-stick baking sheet or in a non-stick pan with an oven-proof handle. Bake until salmon is cooked through, about 12 to 15 minutes. Serve with the Toasted Almond Parsley Salad and squash, if desired. ")
            .addPicturePath(R.drawable.ovenbakedsalmon)
            .build();

    private Recipe recipe2 = new RecipeBuilder()
            .setName("Excellent MeatBalls")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(43)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(40)
            .addIngredient("Extra-virgin olive oil", 0, Ingredient.Unit.NONE)
            .addIngredient("large onion", 1, Ingredient.Unit.NO_UNIT)
            .addIngredient("Salt", 0, Ingredient.Unit.NONE)
            .addIngredient("cloves garlic, smashed and chopped", 2, Ingredient.Unit.NO_UNIT)
            .addIngredient("Pinch crushed red pepper", 0, Ingredient.Unit.NONE)
            .addIngredient("ground beef", 0.5, Ingredient.Unit.POUND)
            .addIngredient("ground veal", 0.5, Ingredient.Unit.POUND)
            .addIngredient("ground pork", 0.5, Ingredient.Unit.POUND)
            .addIngredient("larges eggs", 2, Ingredient.Unit.NO_UNIT)
            .addIngredient("grated Parmigiano", 1, Ingredient.Unit.CUP)
            .addIngredient("finely chopped fresh Italian parsley leaves", 0.25, Ingredient.Unit.CUP)
            .addIngredient("breadcrumbs", 1, Ingredient.Unit.CUP)
            .addIngredient("water", 0.5, Ingredient.Unit.CUP)
            .addIngredient("Marinara Sauce", 0, Ingredient.Unit.NONE)
            .addInstruction("Coat a large saute pan with olive oil, add the onions and bring to a medium-high heat. Season the onions generously with salt and cook for about 5 to 7 minutes. The onions should be very soft and aromatic but have no color. Add the garlic and the crushed red pepper and saute for another 1 to 2 minutes. Turn off heat and allow to cool. ")
            .addInstruction("In a large bowl combine the meats, eggs, Parmigiano, parsley and bread crumbs. It works well to squish the mixture with your hands. Add the onion mixture and season generously with salt and squish some more. Add the water and do 1 final really good squish. The mixture should be quite wet. Test the seasoning of the mix by making a mini hamburger size patty and cooking it. The mixture should taste really good! If it doesn't it is probably missing salt. Add more. Add more anyway. ")
            .addInstruction("Preheat the oven to 350 degrees F. ")
            .addInstruction("Shape the meat into desired size. Some people like 'em big some people like 'em small. I prefer meatballs slightly larger than a golf ball. Coat a large saute pan with olive oil and bring to a medium-high heat. Brown the meatballs on all sides. Place them on a cookie sheet and bake them in the preheated oven for about 15 minutes or until the meatballs are cooked all the way through. If using right away, add them to your big pot of marinara sauce. If not using right away, they can be frozen for later use. Serve with pasta and sauce or just eat them straight out of the pot! YUM! ")
            .addPicturePath(R.drawable.meatballs)
            .build();

    private Recipe recipe3 = new RecipeBuilder()
            .setName("Shrimp Scampi Tetrazzini")
            .setRecipeDifficulty(Recipe.Difficulty.VERY_HARD)
            .setEstimatedCookingTime(20)
            .setPersonNumber(6)
            .setEstimatedPreparationTime(30)
            .addIngredient("Kosher salt and freshly ground black pepper", 0, Ingredient.Unit.NONE)
            .addIngredient("spaghetti", 1, Ingredient.Unit.POUND)
            .addIngredient("unsalted butter", 8, Ingredient.Unit.TABLESPOON)
            .addIngredient("baby bella mushrooms, sliced", 1, Ingredient.Unit.POUND)
            .addIngredient("large shallot, finely chopped", 1, Ingredient.Unit.NO_UNIT)
            .addIngredient("cloves garlic, minced", 4, Ingredient.Unit.NO_UNIT)
            .addIngredient("crushed red pepper flakes", 0.25, Ingredient.Unit.TEASPOON)
            .addIngredient("dry white wine", 1, Ingredient.Unit.CUP)
            .addIngredient("all-purpose flour", 0.25, Ingredient.Unit.CUP)
            .addIngredient("heavy cream", 3, Ingredient.Unit.CUP)
            .addIngredient("lemon juice", 1, Ingredient.Unit.NO_UNIT)
            .addIngredient("freshly grated Parmesan", 1, Ingredient.Unit.CUP)
            .addIngredient("panko", 0.5, Ingredient.Unit.CUP)
            .addIngredient("peeled and deveined large shrimp, tails removed", 1, Ingredient.Unit.POUND)
            .addIngredient("lightly packed fresh parsley leaves, finely chopped", 0.25, Ingredient.Unit.CUP)

            .addInstruction("Preheat the oven to 450 degrees F. Bring a large pot of salted water to a boil and cook the spaghetti according to package directions for al dente. Drain and set aside. ")
            .addInstruction("Melt 2 tablespoons of the butter in a large heavy-bottom pot or Dutch oven over medium-high heat. Add the mushrooms, 1/2 teaspoon salt and a few grinds black pepper and cook, stirring occasionally, until the mushrooms have softened and released their liquid, 5 to 6 minutes. Add the shallot, 2 minced garlic cloves and the red pepper flakes and cook, stirring occasionally, until the shallot has softened, 3 to 4 minutes.   ")
            .addInstruction("Add the wine and bring to a boil, stirring to combine. Cook, stirring frequently, until reduced completely, 5 to 6 minutes. Stir in 2 tablespoons of the butter until melted, then sprinkle in the flour and stir until well combined with the vegetables. Slowly pour in the cream, then add the lemon juice, 1 teaspoon salt and a few grinds black pepper. Simmer, stirring occasionally, until the sauce is slightly thickened and no longer has a floury taste, 6 to 8 minutes. Add the pasta and cheese and toss until pasta is well coated. Transfer to a 3-quart casserole dish.")
            .addInstruction("Meanwhile, microwave the remaining 4 tablespoons butter in a small bowl until melted, about 1 minute. Stir 2 tablespoons of the melted butter and 1/2 teaspoon salt into the panko in a small bowl; set aside. Toss the remaining 2 tablespoons melted butter with the shrimp, grated garlic, 1/2 teaspoon salt and a few grinds black pepper in a medium bowl. Scatter the shrimp evenly over top of the pasta, then sprinkle with the panko mixture.")
            .addInstruction("Bake until the shrimp are opaque throughout and the topping is slightly golden, about 10 minutes. Let sit for 10 minutes before sprinkling with the parsley.")
            .addPicturePath(R.drawable.shrimps)
            .build();

    private Recipe recipe4 = new RecipeBuilder()
            .setName("Garlic Roasted Potatoes")
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setEstimatedCookingTime(60)
            .setPersonNumber(8)
            .setEstimatedPreparationTime(10)
            .addIngredient("small red or white potatoes", 3, Ingredient.Unit.POUND)
            .addIngredient("good olive oil", 0.25, Ingredient.Unit.CUP)
            .addIngredient("kosher salt", 1.5, Ingredient.Unit.TEASPOON)
            .addIngredient("freshly ground black pepper", 2, Ingredient.Unit.TABLESPOON)
            .addIngredient("minced garlic", 2, Ingredient.Unit.TABLESPOON)
            .addIngredient("minced fresh parsley", 2, Ingredient.Unit.TABLESPOON)
            .addInstruction("Preheat the oven to 400 degrees F.")
            .addInstruction("Cut the potatoes in half or quarters and place in a bowl with the olive oil, salt, pepper, and garlic; toss until the potatoes are well coated. Transfer the potatoes to a sheet pan and spread out into 1 layer. Roast in the oven for 45 minutes to 1 hour or until browned and crisp. Flip twice with a spatula during cooking in order to ensure even browning. ")
            .addInstruction("Remove the potatoes from the oven, toss with parsley, season to taste, and serve hot. ")
            .addPicturePath(R.drawable.potatoes)
            .build();

    private Recipe recipe5 = new RecipeBuilder()
            .setName("French toast")
            .setRecipeDifficulty(Recipe.Difficulty.VERY_EASY)
            .setEstimatedCookingTime(10)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(20)
            .addIngredient("ground cinnamon", 1, Ingredient.Unit.TEASPOON)
            .addIngredient("ground nutmeg", 0.25, Ingredient.Unit.TEASPOON)
            .addIngredient("sugar", 2, Ingredient.Unit.TABLESPOON)
            .addIngredient("butter", 4, Ingredient.Unit.TABLESPOON)
            .addIngredient("eggs", 4, Ingredient.Unit.NO_UNIT)
            .addIngredient("milk", 0.25, Ingredient.Unit.CUP)
            .addIngredient("vanilla extract", 0.5, Ingredient.Unit.TEASPOON)
            .addIngredient("slices challah, brioche, or white bread", 8, Ingredient.Unit.NO_UNIT)
            .addIngredient("maple syrup, warmed", 0.5, Ingredient.Unit.CUP)
            .addInstruction("In a small bowl, combine cinnamon, nutmeg, and sugar and set aside briefly.")
            .addInstruction("In a 10-inch or 12-inch skillet, melt butter over medium heat. Whisk together cinnamon mixture, eggs, milk, and vanilla and pour into a shallow container such as a pie plate. Dip bread in egg mixture. Fry slices until golden brown, then flip to cook the other side. Serve with syrup. ")
            .addPicturePath(R.drawable.frenchtoast)
            .build();
}
