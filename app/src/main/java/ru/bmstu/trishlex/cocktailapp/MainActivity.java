package ru.bmstu.trishlex.cocktailapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import ru.bmstu.trishlex.cocktailapp.Ingredients.IngredientActivity;
import ru.bmstu.trishlex.cocktailapp.Settings.PreferencesActivity;
import ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity;

import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.NAME;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.RANDOM;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.TYPE;
import static ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity.DRINK;


/**
 * Основной экран, только на нём доступны настройки
 */
public class MainActivity extends AppCompatActivity {
    public static final String SHOW_ALCOHOL = "showAlcohol";
    public static final String SHOW_PHOTO = "showPhoto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ingredientsButton = findViewById(R.id.buttonIngredients);
        ingredientsButton.setOnClickListener(v ->
                startActivity(new Intent(getBaseContext(), IngredientActivity.class)));

        Button randomButton = findViewById(R.id.buttonRandom);
        randomButton.setOnClickListener(v ->
                startActivity(new Intent(getBaseContext(), SingleDrinkActivity.class).putExtra(TYPE, RANDOM)));

        Button nameButton = findViewById(R.id.buttonName);
        nameButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter cocktail name");

            EditText editText = new EditText(this);
            builder.setView(editText);

            builder.setPositiveButton("OK",
                    (dialog, which) ->
                            startActivity(new Intent(getBaseContext(), SingleDrinkActivity.class)
                                    .putExtra(TYPE, NAME)
                                    .putExtra(DRINK, editText.getText().toString())));

            builder.setNegativeButton("Cancel", ((dialog, which) -> dialog.cancel()));

            builder.show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu) {
            startActivity(new Intent(this, PreferencesActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
