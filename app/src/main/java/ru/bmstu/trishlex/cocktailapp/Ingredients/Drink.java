package ru.bmstu.trishlex.cocktailapp.Ingredients;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class Drink implements Serializable {
    private final int id;
    private final String name;

    private transient Bitmap image;
    private transient Thread download;

    public Drink(int id, String name, String strUrl, boolean showImages) {
        this.id = id;
        this.name = name;

        if (showImages) {
            download = new Thread(() -> setBitmapFromUrl(strUrl));
            download.start();
        }
    }

    public Thread getDownload() {
        return download;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    /**
     * Устанавливает в {@link this#image} выгруженное из Интернета изображение
     * @param strUrl - ссылка на изображение
     */
    private void setBitmapFromUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            this.image = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Can't download thumb", e);
        }
    }
}
