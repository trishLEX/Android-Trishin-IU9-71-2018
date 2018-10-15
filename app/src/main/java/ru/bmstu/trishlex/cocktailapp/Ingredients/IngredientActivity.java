package ru.bmstu.trishlex.cocktailapp.Ingredients;

import android.app.ProgressDialog;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ru.bmstu.trishlex.cocktailapp.R;

import static ru.bmstu.trishlex.cocktailapp.Ingredients.DrinksByIngredientLoader.DRINKS_BY_INGREDIENTS_LOADER_ID;
import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_ALCOHOL;
import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_PHOTO;

public class IngredientActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Drink>> {
    private RecyclerView drinks;
    private static IngredientsListAdapter adapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        Button button = findViewById(R.id.buttonDrinksByIngredients);
        button.setOnClickListener(v -> {
            Bundle params = new Bundle();

            LoaderManager loaderManager = LoaderManager.getInstance(this);
            Loader<List<Drink>> loader = loaderManager.getLoader(DRINKS_BY_INGREDIENTS_LOADER_ID);
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            if (loader == null) {
                loaderManager.initLoader(DRINKS_BY_INGREDIENTS_LOADER_ID, params, this);
                Log.d("debugLog", "init loader");
            } else {
                loaderManager.restartLoader(DRINKS_BY_INGREDIENTS_LOADER_ID, params, this);
            }
        });

        Log.d("debugLog", "Ingredients create start");

        EditText editText = findViewById(R.id.editIngredient);

        Log.d("debugLog", "set drinks");
        drinks = findViewById(R.id.drinksByIngredientsRecyclerView);

        drinks.setLayoutManager(new LinearLayoutManager(this));
        drinks.setHasFixedSize(false);

        if (adapter != null) {
            drinks.setAdapter(adapter);
        } else {
            Log.d("debugLog", "set adapter");
            adapter = new IngredientsListAdapter(this);
        }

        Log.d("debugLog", "Ingredients create end");

    }

    @NonNull
    @Override
    public Loader<List<Drink>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d("debugLog", "create loader");
        bundle.putBoolean(SHOW_PHOTO, PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.showPhotoKey), getResources().getBoolean(R.bool.showPhoto)));
        bundle.putBoolean(SHOW_ALCOHOL, PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.showAlcoKey), getResources().getBoolean(R.bool.showAlco)));
        return new DrinksByIngredientLoader(((EditText)findViewById(R.id.editIngredient)).getText().toString(), bundle, getBaseContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Drink>> loader, List<Drink> drinks) {
        Log.d("debugLog", "load finished " + loader + " " + drinks);
        if (loader.getId() == DRINKS_BY_INGREDIENTS_LOADER_ID) {
            dialog.dismiss();
            LoaderManager.getInstance(this).destroyLoader(DRINKS_BY_INGREDIENTS_LOADER_ID);
            if (drinks != null) {
                adapter.setDrinks(drinks);
                this.drinks.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Wrong ingredient", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Drink>> loader) {

    }
}
