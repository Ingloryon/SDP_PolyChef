package ch.epfl.polychef.recipe;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

import ch.epfl.polychef.utils.Preconditions;

/**
 * Class that represent a Recipe's ingredient.
 */
public final class Ingredient implements Serializable {

    private String name;
    private double quantity;
    private Unit unit;

    /**
     * An enum representing possible units for the ingredients.
     */
    public enum Unit {
        TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM, CUP, OUNCE, NO_UNIT, NONE
    }

    /**
     * Required empty public constructor for Firebase.
     */
    public Ingredient(){
    }

    /**
     * This is the ingredient of a recipe described by a name, a quantity and a unit.
     * Unit can also be NONE for some ingredient with no quantity or NO_UNIT for the ones that is a number of such ingredients.
     * @param name the name of the ingredient
     * @param quantity the quantity of ingredient needed
     * @param unit the unit the quantity should be expressed in
     */
    public Ingredient(@NonNull String name, double quantity,@NonNull Unit unit){
        Preconditions.checkArgument(quantity >= 0, "The quantity should be positive");
        Preconditions.checkArgument(!name.equals(""), "The ingredient's name must be non empty");
        if(quantity == 0) {
            Preconditions.checkArgument(unit == Unit.NONE || unit == Unit.NO_UNIT, "The quantity can only be zero for NO_UNIT or NONE units.");
        }

        this.name = name.toLowerCase(Locale.ENGLISH);
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Returns the quantity needed corresponding to the ingredient.
     * @return the quantity, a positive double
     */
    @SuppressWarnings("WeakerAccess")
    public double getQuantity(){
        return quantity;
    }

    /**
     * Set the ingredient's corresponding quantity.
     * @param quantity the new quantity, must be positive
     */
    @SuppressWarnings("WeakerAccess")
    public void setQuantity(double quantity){
        Preconditions.checkArgument(quantity >= 0, "The quantity should be positive");
        this.quantity = quantity;
    }

    /**
     * Returns the name of the ingredient.
     * @return the name of the ingredient
     */
    public String getName(){
        return name;
    }

    /**
     * Set the name of the ingredient.
     */
    public void setName(String newName){
        name = newName.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Return the unit of the ingredient.
     * @return the unit of the ingredient
     */
    public Unit getUnit(){
        return unit;
    }

    /**
     * Set the unit of the ingredient.
     */
    public void setUnit(Unit newUnit){
        unit= newUnit;
    }

    @NonNull
    @Override
    public String toString(){
        switch (unit){
            case NONE:
                return name;
            case NO_UNIT:
                return quantity + " " + name;
            default:
                String str = quantity + " " + unit.toString().toLowerCase(Locale.ENGLISH);
                return quantity > 1 ? str + "s of " + name : str + " of " + name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Ingredient){
            Ingredient other=(Ingredient) obj;
            return other.getName().equals(name) && other.getQuantity()==quantity && other.getUnit()==unit;
        }
        return false;
    }
}
