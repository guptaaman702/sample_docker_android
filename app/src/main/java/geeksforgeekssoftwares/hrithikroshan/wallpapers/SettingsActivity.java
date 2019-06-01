package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener  {
    private PrefManager pref;
    private TextView txtGoogleUsername, txtNoOfGridColumns, txtGalleryName;
    private Button btnSave;
    private Button btnClearCache;
    private Button btnCacheSize;
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6007058792756351/1047734811";
    private static final String INITIALIZE_UNIT_ID = "ca-app-pub-6007058792756351~8431400818";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        txtGoogleUsername = (TextView) findViewById(R.id.txtGoogleUsername);
        txtNoOfGridColumns = (TextView) findViewById(R.id.txtNoOfColumns);
        txtGalleryName = (TextView) findViewById(R.id.txtGalleryName);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnClearCache = (Button) findViewById(R.id.btnClearCache);
        btnCacheSize  = (Button) findViewById(R.id.btnCacheSize);

        pref = new PrefManager(getApplicationContext());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.action_settings));

        // Display edittext values stored in shared preferences
        // Google username
        txtGoogleUsername.setText(pref.getGoogleUserName());

        // Number of grid columns
        txtNoOfGridColumns.setText(String.valueOf(pref.getNoOfGridColumns()));

        // Gallery name
        txtGalleryName.setText(pref.getGalleryName());

        // Save settings button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Validating the data before saving to shared preferences
                // validate google username
                String googleUsername = txtGoogleUsername.getText().toString()
                        .trim();
                if (googleUsername.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_enter_google_username),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // validate number of grid columns
                String no_of_columns = txtNoOfGridColumns.getText().toString()
                        .trim();
                if (no_of_columns.length() == 0 || !isInteger(no_of_columns)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_enter_valid_grid_columns),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // validate gallery name
                String galleryName = txtGalleryName.getText().toString().trim();
                if (galleryName.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_enter_gallery_name),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Check for setting changes
                if (!googleUsername.equalsIgnoreCase(pref.getGoogleUserName())
                        || !no_of_columns.equalsIgnoreCase(String.valueOf(pref
                        .getNoOfGridColumns()))
                        || !galleryName.equalsIgnoreCase(pref.getGalleryName())) {
                    // User changed the settings
                    // save the changes and launch SplashScreen to initialize
                    // the app again
                    pref.setGoogleUsername(googleUsername);
                    pref.setNoOfGridColumns(Integer.parseInt(no_of_columns));
                    pref.setGalleryName(galleryName);

                    // start the app from SplashScreen
                    Intent i = new Intent(SettingsActivity.this,
                            SplashActivity.class);
                    // Clear all the previous activities
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    // user not modified any values in the form
                    // skip saving to shared preferences
                    // just go back to previous activity
                    onBackPressed();
                }

            }
        });


        btnClearCache.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppController.getInstance().clearCache();
                // start the app from SplashScreen
//                Intent i = new Intent(SettingsActivity.this,
//                        SplashActivity.class);
//                // Clear all the previous activities
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
            }
        });

        btnCacheSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppController.getInstance().getCacheSize();
            }
        });

    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }
}