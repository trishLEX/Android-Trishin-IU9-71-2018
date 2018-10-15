package ru.bmstu.trishlex.cocktailapp.Ingredients;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import ru.bmstu.trishlex.cocktailapp.DrinksJsonParser;

import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_PHOTO;

public class DrinksByIngredientLoader extends AsyncTaskLoader<List<Drink>> {
    private Bundle params;
    private String text;

    public static final int DRINKS_BY_INGREDIENTS_LOADER_ID = 1;

    public DrinksByIngredientLoader(String text, Bundle params, Context context) {
        super(context);

        this.text = text;
        this.params = params;
    }

    @Override
    protected void onStartLoading() {
        if (params == null) {
            return;
        }

        forceLoad();
    }

    @Nullable
    @Override
    public List<Drink> loadInBackground() {
        String strUrl = String.format(
                "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=%s",
                text
        );

        Uri builtUri = Uri.parse(strUrl).buildUpon().build();

        URL url;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Wrong URL: " + strUrl, e);
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try {
                InputStream in = connection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    return DrinksJsonParser.getDrinksFromJson(scanner.next(), params.getBoolean(SHOW_PHOTO));
                } else {
                    return null;
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't open connection", e);
        }
    }
}
