package ru.bmstu.trishlex.cocktailapp.SingleDrink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.bmstu.trishlex.cocktailapp.DrinkLoader;
import ru.bmstu.trishlex.cocktailapp.Ingredients.Drink;
import ru.bmstu.trishlex.cocktailapp.MainActivity;
import ru.bmstu.trishlex.cocktailapp.R;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.DRINK_LOADER_ID;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.ID;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.NAME;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.RANDOM;
import static ru.bmstu.trishlex.cocktailapp.DrinkLoader.TYPE;
import static ru.bmstu.trishlex.cocktailapp.Ingredients.DrinksByIngredientLoader.DRINKS_BY_INGREDIENTS_LOADER_ID;
import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_ALCOHOL;
import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_PHOTO;

public class SingleDrinkActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DrinkReceipt> {
    public static final String DRINK = "drink";
    private static DrinkReceipt drinkReceipt;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_drink);

        TextView textView = findViewById(R.id.textSingleDrink);
        textView.setTextSize(
                COMPLEX_UNIT_SP,
                PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getInt(getString(R.string.textSizeSimpleKey), R.integer.textSizeDefValue)
        );
        textView.setMovementMethod(new ScrollingMovementMethod());

        TextView ingredientsView = findViewById(R.id.ingredientsSingleDrink);
        ingredientsView.setTextSize(
                COMPLEX_UNIT_SP,
                PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getInt(getString(R.string.textSizeIngredientsKey), R.integer.ingredientsSizeDefValue)
        );
        ingredientsView.setMovementMethod(new ScrollingMovementMethod());

        ImageView image = findViewById(R.id.imageSingleDrink);
        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.showPhotoKey), getResources().getBoolean(R.bool.showPhoto)))
        {
            image.getLayoutParams().height = image.getLayoutParams().width =
                    Integer.parseInt(PreferenceManager
                            .getDefaultSharedPreferences(this)
                            .getString(getString(R.string.listKey), getString(R.string.photoSize700)));
        } else {
            image.setVisibility(View.INVISIBLE);
        }

        Button share = findViewById(R.id.shareWithFriend);
        share.setOnClickListener(v -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    textView.getText().toString() + "\n" + ingredientsView.getText().toString()
            );
            shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(drinkReceipt.getImage()));
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "share"));
        });
        share.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if (intent.hasExtra(TYPE)) {
            int type = intent.getIntExtra(TYPE, -1);
            if (type == -1) {
                throw new IllegalStateException("Wrong type of intent");
            }
            if (savedInstanceState != null) {
                Log.d("debugLog", "saved instance not null");
                Log.d("debugLog", "DRINK " + savedInstanceState.getInt(DRINK));
                if (savedInstanceState.containsKey(DRINK)) {
                    DrinkReceipt drink = (DrinkReceipt) savedInstanceState.get(DRINK);
                    setDrink(drink);
                }
            } else {
                Log.d("debugLog", "saved instance is null");
                LoaderManager loaderManager = LoaderManager.getInstance(this);
                Loader<Drink> loader = loaderManager.getLoader(DRINK_LOADER_ID);
                savedInstanceState = new Bundle();
                savedInstanceState.putInt(TYPE, type);
                if (type == ID) {
                    savedInstanceState.putInt(DRINK, intent.getIntExtra(DRINK, -1));
                } else if (type == NAME) {
                    savedInstanceState.putString(DRINK, intent.getStringExtra(DRINK));
                }

                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if (loader == null) {
                    loaderManager.initLoader(DRINK_LOADER_ID, savedInstanceState, this);
                } else {
                    loaderManager.restartLoader(DRINK_LOADER_ID, savedInstanceState, this);
                }
            }
        }
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
        );

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("debugLog", "saving drink");
        outState.putSerializable(DRINK, drinkReceipt);
    }

    @NonNull
    @Override
    public Loader<DrinkReceipt> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d("debugLog", "create drink loader");

        bundle.putBoolean(SHOW_PHOTO, PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.showPhotoKey), getResources().getBoolean(R.bool.showPhoto))
        );

        bundle.putBoolean(SHOW_ALCOHOL, PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.showAlcoKey), getResources().getBoolean(R.bool.showAlco))
        );

        return new DrinkLoader(getBaseContext(), bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<DrinkReceipt> loader, DrinkReceipt drinkReceipt) {
        if (loader.getId() == DRINK_LOADER_ID) {
            dialog.dismiss();
            LoaderManager.getInstance(this).destroyLoader(DRINK_LOADER_ID);

            Button share = findViewById(R.id.shareWithFriend);
            share.setVisibility(View.VISIBLE);

            if (drinkReceipt == null) {
                Log.d("debugLog", "drink load finished, null");

                Toast.makeText(this, "Wrong drink", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            } else {
                Log.d("debugLog", "drink load finished");
                setDrink(drinkReceipt);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<DrinkReceipt> loader) {

    }

    private void setDrink(DrinkReceipt drinkReceipt) {
        boolean showPhoto = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.showPhotoKey), getResources().getBoolean(R.bool.showPhoto));

        if (showPhoto) {
            ImageView imageView = findViewById(R.id.imageSingleDrink);
            imageView.setImageBitmap(drinkReceipt.getImage());
        }

        TextView textView = findViewById(R.id.textSingleDrink);
        String strForTextView = "Name: " + drinkReceipt.getName() + "\n";
        strForTextView += "How to: " + drinkReceipt.getInstructions() + "\n";
        strForTextView += "Alcoholic: " + (drinkReceipt.isAlcoholic() ? "Alcoholic" : "Non Alcoholic") + "\n";
        Log.d("debugLog", strForTextView);
        textView.setText(strForTextView);


        TextView ingredients = findViewById(R.id.ingredientsSingleDrink);
        StringBuilder builder = new StringBuilder("Ingredients:\n");
        for (String str : drinkReceipt.getIngredients()) {
            builder.append(str).append("\n");
        }
        ingredients.setText(builder.toString());

        SingleDrinkActivity.drinkReceipt = drinkReceipt;
    }
}
