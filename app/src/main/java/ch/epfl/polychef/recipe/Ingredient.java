package ch.epfl.polychef.recipe;

public class Ingredient {

    private String name;
    private double quantity;
    private Recipe.Unit unit;

    public Ingredient(String name, double quantity, Recipe.Unit unit){
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

    @Override
    public String toString(){
        return quantity + " " + unit.toString().toLowerCase() + " of " + name;
    }
}
