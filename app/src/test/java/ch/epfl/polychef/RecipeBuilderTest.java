package ch.epfl.polychef;

import org.junit.Test;

import ch.epfl.polychef.RecipeObj.RecipeBuilder;

/*
import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
 */

public class RecipeBuilderTest {


    @Test
    public void recipeBuilderRejectsInvalidInputs() {
        RecipeBuilder rb = new RecipeBuilder();
/*
        // rejects empty names
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        // setter rejects empty names
        assertThrows(IllegalArgumentException.class, () -> rb.setName(""));
        rb.setName("Tartare de poulet");

        // rejects when no instructions added
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.addInstruction(""));
        rb.addInstruction("Start by the beginning");

        // rejects when no ingredients
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("", 300));
        assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("Carrots", 0));
        rb.addIngredient("Carrots", 300);

        // rejects when no persons
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.setPersonNumber(0));
        rb.setPersonNumber(4);

        // rejects when no entered estimatedPreparationTime
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.setEstimatedPreparationTime(0));
        rb.setEstimatedPreparationTime(45);

        // rejects when no entered estimatedCookingTime
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.estimatedCookingTime(0));
        rb.setEstimatedCookingTime(45);



*/





    }

    //TODO: check methode addIngredient is unmodif outside
}
