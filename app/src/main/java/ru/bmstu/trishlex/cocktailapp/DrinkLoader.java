package ru.bmstu.trishlex.cocktailapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ru.bmstu.trishlex.cocktailapp.SingleDrink.DrinkReceipt;

import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_ALCOHOL;
import static ru.bmstu.trishlex.cocktailapp.MainActivity.SHOW_PHOTO;
import static ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity.DRINK;

/**
 * "Загрузчик коктелей"
 */
public class DrinkLoader extends AsyncTaskLoader<DrinkReceipt> {
    private Bundle params;

    public static final int DRINK_LOADER_ID = 2;
    public static final String TYPE = "drinkLoadType";

    public static final int RANDOM = 1;
    public static final int ID = 2;
    public static final int NAME = 3;

    public DrinkLoader(Context context, Bundle params) {
        super(context);

        this.params = params;
    }

    @Override
    protected void onStartLoading() {
        if (params == null) {
            return;
        }

        forceLoad();
    }

    public Bundle getParams() {
        return params;
    }

    /**
     * Загружает случайный рецепт, по id и по имени
     * @return DrinkReceipt - выгруженный рецепт коктейля
     */
    @Nullable
    @Override
    public DrinkReceipt loadInBackground() {
        String strUrl;

        if (params.getInt(TYPE) == RANDOM) {
            String suffix = params.getBoolean(SHOW_ALCOHOL) ? "Alcoholic" : "Non_Alcoholic";
            strUrl = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=" + suffix;

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
                        DrinkReceipt drink = DrinksJsonParser.getRandomDrinkFromJson(scanner.next(), params.getBoolean(SHOW_PHOTO));

                        try {
                            if (params.getBoolean(SHOW_PHOTO))
                                drink.getDownload().join();
                        } catch (InterruptedException e) {
                            throw new IllegalStateException("Can't join");
                        }

                        params.putSerializable(DRINK, drink);
                        Log.d("debugLog", "put to DRINK obj " + drink);
                        return drink;
                    } else {
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Can't open connection", e);
            }
        } else if (params.getInt(TYPE) == ID) {
            int id = params.getInt(DRINK);
            if (id == -1) {
                throw new IllegalStateException("Wrong Id");
            }

            DrinkReceipt drink = DrinksJsonParser.getDrinkById(id, params.getBoolean(SHOW_PHOTO));
            try {
                if (params.getBoolean(SHOW_PHOTO))
                    drink.getDownload().join();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Can't join");
            }

            params.putSerializable(DRINK, drink);
            Log.d("debugLog", "put to DRINK obj by id " + drink);
            return drink;
        } else if (params.getInt(TYPE) == NAME) {
            String name = params.getString(DRINK);

            strUrl = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + name;

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
                        DrinkReceipt drink = DrinksJsonParser.getDrinkFromJson(scanner.next(), params.getBoolean(SHOW_PHOTO));

                        if (drink == null) {
                            return null;
                        }

                        try {
                            if (params.getBoolean(SHOW_PHOTO))
                                drink.getDownload().join();
                        } catch (InterruptedException e) {
                            throw new IllegalStateException("Can't join");
                        }

                        params.putSerializable(DRINK, drink);
                        Log.d("debugLog", "put to DRINK obj " + drink);
                        return drink;
                    } else {
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Can't open connection", e);
            }
        } else {
            throw new IllegalArgumentException("Wrong type: " + params.getInt(TYPE));
        }
    }
}
