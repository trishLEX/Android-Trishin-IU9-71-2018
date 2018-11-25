package ru.bmstu.trishlex.cocktailapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.bmstu.trishlex.cocktailapp.SingleDrink.SingleDrinkActivity;

public class DrinkModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debugLog", "bluetooth is on");

        SingleDrinkActivity singleDrinkActivity = (SingleDrinkActivity) context;

        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File cocktails = new File(pictures, "Cocktails");
        cocktails.mkdir();

        String fileName = String.join("_", singleDrinkActivity.getDrinkReceipt().getName().split(" ")) + ".png";
        File imageFile = new File(cocktails, fileName);
        imageFile.setWritable(true);
        Log.d("debugLog", imageFile.getPath());

        View view = singleDrinkActivity.getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Problems with file", e);
        }

        Log.d("debugLog", "end receiving");
    }
}
