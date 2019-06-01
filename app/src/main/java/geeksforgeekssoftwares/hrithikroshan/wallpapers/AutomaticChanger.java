package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AutomaticChanger extends BroadcastReceiver {
    private PrefManager pref;
    File[] wallpaperImages = null;
    int counterTime = 0;
    Bitmap tmp = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = new PrefManager(context);
        SharedPreferences pref1 = context.getSharedPreferences("CounterPref", 0);
        final SharedPreferences.Editor editor = pref1.edit();
        counterTime = pref1.getInt("counterTime",0);
        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+"hdwallpapershrithikroshanhdwallpapersgpa");
        if(!dir.exists())
            dir.mkdir();

        wallpaperImages = dir.listFiles();

        if(counterTime < wallpaperImages.length)
        {
            this.tmp = BitmapFactory.decodeFile(wallpaperImages[counterTime].getAbsolutePath());
            Log.d("Acount1",wallpaperImages.length + "," + counterTime);
            counterTime++;
            editor.putInt("counterTime",counterTime);
            editor.apply();
        }
        else
        {
            counterTime = 0;
            Log.d("Acount0",wallpaperImages.length + "," + counterTime);
            this.tmp = BitmapFactory.decodeFile(wallpaperImages[counterTime].getAbsolutePath());
            editor.putInt("counterTime",counterTime);
            editor.apply();
        }

        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        try
        {
            //Bitmap wallpaper = ((BitmapDrawable) drawable).getBitmap();
            myWallpaperManager.setBitmap(tmp);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
