package ch.epfl.polychef.recipe;

public class Ingredient {

    public enum Unit {
        TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM, NONE, OUNCE
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
        if(unit == Unit.NONE){
            return name;
        }else{
            if(quantity > 1){
                return quantity + " " + unit.toString().toLowerCase() + "s of " + name;
            }else {
                return quantity + " " + unit.toString().toLowerCase() + " of " + name;
            }
        }
    }
}
