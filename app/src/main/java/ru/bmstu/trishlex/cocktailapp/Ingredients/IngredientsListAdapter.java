package ru.bmstu.trishlex.cocktailapp.Ingredients;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.bmstu.trishlex.cocktailapp.R;
import ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity;

import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.ID;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.TYPE;
import static ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity.DRINK;

/**
 * Реализация RecyclerView.Adapter
 */
public class IngredientsListAdapter extends RecyclerView.Adapter<DrinksByIngredientViewHolder> {
    private List<Drink> drinks;
    private int drinksCount;
    private AppCompatActivity activity;

    public IngredientsListAdapter(AppCompatActivity activity) {
        drinks = new ArrayList<>();
        drinksCount = 0;

        this.activity = activity;
    }

    @NonNull
    @Override
    public DrinksByIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForDrinksItem = R.layout.drinks_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForDrinksItem, viewGroup, false);

        DrinksByIngredientViewHolder viewHolder = new DrinksByIngredientViewHolder(view, activity);
        viewHolder.setDrink(drinks.get(drinksCount++));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DrinksByIngredientViewHolder drinksByIngredientViewHolder, int i) {
        drinksByIngredientViewHolder.setDrink(drinks.get(i));
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    public void setDrinks(List<Drink> drinks) {
        Log.d("debugLog", "setting drinks " + drinks);
        this.drinks = drinks;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }
}
