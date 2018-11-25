package ru.bmstu.trishlex.cocktailapp.Service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ru.bmstu.trishlex.cocktailapp.Ingredients.Drink;
import ru.bmstu.trishlex.cocktailapp.R;

public class DownloadService extends IntentService {
    private volatile int max;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private NotificationChannel channel;
    private volatile int current;

    public DownloadService() {
        super("PhotoDownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("debugLog", "service is handling intent");
        List<String> urls = intent.getStringArrayListExtra("images");
        List<String> names = intent.getStringArrayListExtra("names");
        Log.d("debugLog", urls.toString());

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this, "ru.bmstu.trishlex.cocktail");
        builder
                .setContentTitle("Cocktails download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.logo2)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        channel = new NotificationChannel("ru.bmstu.trishlex.cocktail", "Cocktails download", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId("ru.bmstu.trishlex.cocktail");

        max = urls.size();
        current = 0;

        for (int i = 0; i < urls.size(); i++) {
            int index = i;
            String name = String.join("_", names.get(i).split(" ")) + ".png";

            new Thread(() -> saveImage(urls.get(index), name)).run();
        }

        Log.d("debugLog", "all is good");
    }

    private void saveImage(String url, String fileName) {
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File cocktails = new File(pictures, "Cocktails");
        cocktails.mkdir();
        File imageFile = new File(cocktails, fileName);
        imageFile.setWritable(true);
        Log.d("debugLog", imageFile.getPath());

        if (!imageFile.exists()) {
            try (FileOutputStream fOut = new FileOutputStream(imageFile)) {
                Drink.getBitMapFromUrl(url).compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
            } catch (IOException e) {
                Log.d("debugLog", "Problems with file: " + imageFile.getName());
            }
        }
        notifyDownload();
    }

    private synchronized void notifyDownload() {
        current++;
        builder.setProgress(max, current, false);
        notificationManager.notify(1, builder.build());

        if (current == max) {
            builder
                    .setContentText("Download complete")
                    .setProgress(0, 0, false);
            notificationManager.notify(1, builder.build());
            stopSelf();
        }
    }
}
