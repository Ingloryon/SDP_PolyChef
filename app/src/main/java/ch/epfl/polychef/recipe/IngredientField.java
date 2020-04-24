package ch.epfl.polychef.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;

import ch.epfl.polychef.R;

public class IngredientField extends ConstraintLayout {

    private EditText ingredient;
    private EditText quantity;
    private Spinner unit;

    public IngredientField(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_field, this, true);
    }
}
