package ru.bmstu.trishlex.cocktailapp.SingleDrink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.bmstu.trishlex.cocktailapp.Ingredients.Drink;

/**
 * Класс, описывающий рецепт коктейля, фото и id находятся в {@link Drink}
 */
public class DrinkReceipt extends Drink implements Serializable {
    private String instructions;
    private ArrayList<String> ingredients;
    private boolean isAlcoholic;

    public DrinkReceipt(int id,
                        String name,
                        String strUrl,
                        String instructions,
                        boolean isAlcoholic,
                        ArrayList<String> ingredients,
                        boolean showPhoto)
    {
        super(id, name, strUrl, showPhoto);

        this.isAlcoholic = isAlcoholic;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public boolean isAlcoholic() {
        return isAlcoholic;
    }
}
