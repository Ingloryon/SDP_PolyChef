package ch.epfl.polychef.recipe;

import java.io.Serializable;

public class Ingredient implements Serializable {

    public enum Unit {
        TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM, CUP, OUNCE, NO_UNIT, NONE
    }

    private String name;
    private double quantity;
    private Unit unit;

    public Ingredient(String name, double quantity, Unit unit){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
    public double getQuantity(){
        return quantity;
    }

    public void setQuantity(double quantity){
        this.quantity = quantity;
    }
    public String getName(){
        return name;
    }
    public Unit getUnit(){
        return unit;
    }

    @Override
    public String toString(){
        if(unit == Unit.NONE) {
            return name;
        }else if(unit == Unit.NO_UNIT){
            return quantity + " " + name;
        }else{
            if(quantity > 1){
                return quantity + " " + unit.toString().toLowerCase() + "s of " + name;
            }else {
                return quantity + " " + unit.toString().toLowerCase() + " of " + name;
            }
        }
    }
}
