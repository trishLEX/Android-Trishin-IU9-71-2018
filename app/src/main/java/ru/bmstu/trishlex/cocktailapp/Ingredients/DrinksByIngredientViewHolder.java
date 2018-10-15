package ru.bmstu.trishlex.cocktailapp.Ingredients;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.bmstu.trishlex.cocktailapp.R;
import ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.ID;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.TYPE;
import static ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity.DRINK;

public class DrinksByIngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView listItemView;
    private ImageView imageView;
    private AppCompatActivity activity;
    private int drinkId;

    public DrinksByIngredientViewHolder(View itemView, AppCompatActivity activity) {
        super(itemView);

        listItemView = itemView.findViewById(R.id.drinkItem);
        imageView = itemView.findViewById(R.id.drinkImage);

        listItemView.setTextSize(COMPLEX_UNIT_SP, PreferenceManager.getDefaultSharedPreferences(activity).getInt(activity.getString(R.string.textSizeSimpleKey), R.integer.textSizeDefValue));

        this.activity = activity;
        itemView.setOnClickListener(this);
    }

    public void setDrink(Drink drink) {
        listItemView.setText(drink.getName());
        drinkId = drink.getId();

        boolean showPhoto = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.showPhotoKey), activity.getResources().getBoolean(R.bool.showPhoto));

        if (showPhoto) {
            imageView.setImageBitmap(drink.getImage());
        }
    }

    @Override
    public void onClick(View v) {
        activity.startActivity(new Intent(activity, SingleDrinkActivity.class).putExtra(TYPE, ID).putExtra(DRINK, drinkId));
    }
}
