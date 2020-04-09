package ch.epfl.polychef.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.polychef.recipe.Ingredient;

public final class RecipeInputParsing {
    //private RecipeInputParsing() {}

    public static boolean parseInstructions(String instructions, List<String> recipeInstructions, List<String> errorLogs) {
        // TODO: Add more precise input sanitization of instructions : should we check for markdown / commands ? <>
        if (instructions.length()<3 || !instructions.contains("{") || !instructions.contains("}")){
            errorLogs.add("Instructions: the entered instructions should match format {a},{b},... (no spaces)");
            return false;
        }

        instructions = instructions.substring(1);
        String separator = Pattern.quote("},{");
        String[] mots = instructions.split(separator);
        for (int i = 0; i < mots.length - 1; i++) {
            recipeInstructions.add(mots[i]);
        }
        recipeInstructions.add(mots[mots.length - 1].substring(0, mots[mots.length - 1].length() - 1));
        return true;
    }

    public static boolean parseIngredients(String toMatch, String pattern, List<Ingredient> ingredients, List<String> errorLogs) {
        List<String> allMatches = new ArrayList<>();

        Matcher mat = Pattern.compile(pattern)
                .matcher(toMatch);
        while (mat.find()) {
            allMatches.add(mat.group());
        }
        if(allMatches.size()==0){
            allMatches.clear();
            errorLogs.add("Ingredients: There should be 3 arguments entered as {a,b,c}");
            return false;
        }
        for (String s : allMatches) {
            String[] list = s.split(",");
            String name = list[0].trim().substring(1).trim();
            double quantity = Double.parseDouble(list[1].trim()); // TODO: check this method does not throw errors
            Ingredient.Unit unit = null;
            String unitString = list[2].trim().substring(0, list[2].trim().length() - 1).trim();
            for (Ingredient.Unit u : Ingredient.Unit.values()) {
                if (u.toString().toLowerCase().equals(unitString.toLowerCase())) {
                    unit = u;
                }
            }
            if (unit == null) {
                allMatches.clear();
                errorLogs.add("Ingredients: The entered unit is not part of the possible units " + Arrays.asList(Ingredient.Unit.values()) + ".");
                return false;
            }

            try{
                ingredients.add(new Ingredient(name, quantity, unit));
            } catch (IllegalArgumentException e){
                errorLogs.add("Ingredients: " + e.toString().substring(35));
                return false;
            }
        }
        return true;
    }

}
