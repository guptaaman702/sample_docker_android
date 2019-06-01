package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AutomaticChangerActivity extends AppCompatActivity implements View.OnClickListener  {
    private PrefManager pref;
    private TextView folderLocation,notice;
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6007058792756351/1047734811";
    private static final String INITIALIZE_UNIT_ID = "ca-app-pub-6007058792756351~8431400818";
    File[] wallpaperImages = null;
    private int previouslySelectedTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_changer);
        MobileAds.initialize(this, INITIALIZE_UNIT_ID);
        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdOpened() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });

        //Showing banner ads
        AdView layout = (AdView) this.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        layout.loadAd(adRequest1);
        folderLocation = (TextView) findViewById(R.id.folderLocation);
        notice = (TextView) findViewById(R.id.notice);
        pref = new PrefManager(getApplicationContext());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.live_settings));

        Log.d("PREFSETTINGS",Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +pref.getGalleryName());

        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +pref.getGalleryName());

        if(!dir.exists())
            dir.mkdir();

        wallpaperImages = dir.listFiles();

        if(wallpaperImages.length==0){
            folderLocation.setText( "Folder Location : " +  Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +pref.getGalleryName() + '\n'
                    + "You do not have any images stored in this location.Start Downloading Wallpapers in this App." + '\n' + '\n' + '\n');
        } else {
            folderLocation.setText( "Folder Location : " +  Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +pref.getGalleryName()+ '\n' + '\n' + '\n');
        }

        notice.setText("Notice : " + "\n" + "ALWAYS SET AUTO WALLPAPER CHANGER FROM THE APP. Do not open Live Wallpaper" +
                " from Android Sytem once Auto Wallpaper Changer is set. If you do,Auto Wallpaper Changer will not work" +
                "! You'll have to remove Auto Wallpaper Changer and re-set it from the App.");

        final Spinner itemsSpinner= (Spinner) findViewById(R.id.spinner);
        final int[] actualValues={1,2,5,10,15,30,45,60,120,300,600,1440,2880,7200,10080,20160,50400};

        itemsSpinner.setSelection(6);
        itemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("TimeChangerPref", 0);
                final SharedPreferences.Editor editor = pref1.edit();
                int selectedItemTag = actualValues[position];
                previouslySelectedTag = pref1.getInt("previouslySelectedTag",0);
                editor.putInt("previouslySelectedTag",selectedItemTag);
                editor.apply();
                Log.d("previouslySelectedTag",""+previouslySelectedTag+selectedItemTag+""+pref1.getInt("previouslySelectedTag",0));
                Log.d("getItemAtPosition",""+itemsSpinner.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
