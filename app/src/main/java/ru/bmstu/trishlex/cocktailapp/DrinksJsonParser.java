package ru.bmstu.trishlex.cocktailapp;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ru.bmstu.trishlex.cocktailapp.Ingredients.Drink;
import ru.bmstu.trishlex.cocktailapp.SingleDrink.DrinkReceipt;

/**
 * Парсер JSON ответов от сервера с базой
 */
public class DrinksJsonParser {
    private static final Gson GSON = new Gson();

    /**
     * @param json - файл с коктейлями
     * @param showPhoto нужно ли выгружать фото
     * @return список коктейлей
     */
    public static List<Drink> getDrinksFromJson(String json, boolean showPhoto) {
        JsonObject drinks = GSON.fromJson(json, JsonObject.class);

        List<Drink> res = new ArrayList<>();

        JsonArray drinksArray = drinks.getAsJsonArray("drinks");

        List<Thread> downloads = new ArrayList<>();

        for (int i = 0; i < drinksArray.size(); i++) {
            JsonObject drinkJson = drinksArray.get(i).getAsJsonObject();
            Drink drink = new Drink(
                    drinkJson.get("idDrink").getAsInt(),
                    drinkJson.get("strDrink").getAsString(),
                    drinkJson.get("strDrinkThumb").getAsString(),
                    showPhoto);
            res.add(drink);
            downloads.add(drink.getDownload());
        }

        if (showPhoto) {
            for (Thread download : downloads) {
                try {
                    download.join();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Can't join thread " + download.toString(), e);
                }
            }
        }

        Log.d("debugLog", "drinks size: " + res.size());
        return res;
    }

    public static DrinkReceipt getRandomDrinkFromJson(String json, boolean showPhoto) {
        JsonObject drinks = GSON.fromJson(json, JsonObject.class);

        JsonArray drinksArray = drinks.getAsJsonArray("drinks");
        Random random = new Random();
        int index = random.nextInt(drinksArray.size());
        JsonObject drinkJson = drinksArray.get(index).getAsJsonObject();
        int id = drinkJson.get("idDrink").getAsInt();
        return getDrinkById(id, showPhoto);
    }

    public static DrinkReceipt getDrinkById(int id, boolean showPhoto) {
        String strUrl = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + id;

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
                    DrinkReceipt drink = DrinksJsonParser.getDrinkFromJson(scanner.next(), showPhoto);

                    return drink;
                } else {
                    throw new IllegalArgumentException("No input from " + strUrl);
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't open connection", e);
        }
    }

    public static DrinkReceipt getDrinkFromJson(String json, boolean showPhoto) {
        Log.d("debugLog", "DRINK: " + json);
        JsonObject drinks = GSON.fromJson(json, JsonObject.class);
        if (drinks.get("drinks").isJsonNull()) {
            return null;
        }

        JsonArray drinksArray = drinks.getAsJsonArray("drinks");

        JsonObject drinkJson = drinksArray.get(0).getAsJsonObject();

        ArrayList<String> ingredients = new ArrayList<>();

        for (int i = 1; i < 16; i++) {
            if (!drinkJson.get("strIngredient" + i).isJsonNull() && !drinkJson.get("strIngredient" + i).getAsString().equals("")) {
                ingredients.add(drinkJson.get("strIngredient" + i).getAsString());
            } else {
                break;
            }
        }

        return new DrinkReceipt(
                drinkJson.get("idDrink").getAsInt(),
                drinkJson.get("strDrink").getAsString(),
                drinkJson.get("strDrinkThumb").getAsString(),
                drinkJson.get("strInstructions").getAsString(),
                drinkJson.get("strAlcoholic").isJsonNull() || drinkJson.get("strAlcoholic").getAsString().equals("Alcoholic"),
                ingredients,
                showPhoto
        );
    }
}
