package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Category;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private List<Category> albumsList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6007058792756351/1047734811";
    private static final String INITIALIZE_UNIT_ID = "ca-app-pub-6007058792756351~8431400818";

    String name = new String("Main Grid Screen");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        int c = pref.getInt("counterRateValue",0);
        Log.d("counterRateValueMAIN",""+c);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(c>=2){
            AppRater.app_launched(this);
        }
        MobileAds.initialize(this, INITIALIZE_UNIT_ID);
        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
//        AppLovinSdk.initializeSdk(this);
//        interstitialAd.loadAd(adRequest);

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

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Getting the albums from shared preferences
        albumsList = AppController.getInstance().getPrefManger().getCategories();

        // Insert "Recently Added" in navigation drawer first position
       /* Category recentAlbum = new Category(null, getString(R.string.nav_drawer_recently_added), "(100)");

        albumsList.add(0, recentAlbum);*/

        // Loop through albums in add them to navigation drawer adapter
        for (Category a : albumsList) {
            navDrawerItems.add(new NavDrawerItem(true, a.getId(), a.getTitle(), a.getPhotoNo()));
            // titles a.getTitle()
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        displayView(0);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments

        Fragment fragment = null;
        String albumId = "";
        switch (position) {
            case 0:
                // Recently added item selected
                // don't pass album id to grid fragment
                /*Log.e(TAG, "GridFragment is creating");
                fragment = GridFragment.newInstance(null);*/
                 albumId = albumsList.get(position).getId();
                fragment = GridFragment.newInstance(albumId);
                break;

            default:
                // selected wallpaper category
                // send album id to grid fragment to list all the wallpapers
                albumId = albumsList.get(position).getId();
                fragment = GridFragment.newInstance(albumId);
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(albumsList.get(position).getTitle());
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,
                    SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        else if(id == R.id.rate_app) {
            Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
            }
        }

        else {
            Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=geeksforgeekssoftwares");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            }catch (ActivityNotFoundException e){
                startActivity(goToMarket);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if ( keyCode == KeyEvent.KEYCODE_BACK ) {

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setIcon(R.mipmap.ic_launcher);
            builderSingle.setTitle("Try Other Top Apps");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Kajal Raghwani HD Wallpapers");
            arrayAdapter.add("Shahrukh Khan HD Wallpapers");
            arrayAdapter.add("Tiger Shroff HD Wallpapers");
            arrayAdapter.add("Allu Arjun HD Wallpapers");
            arrayAdapter.add("Roman Reigns HD Wallpapers");

            builderSingle.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });

            builderSingle.setPositiveButton("Rate Us", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                    }
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    String appUri = "";
                    switch (strName) {
                        case "Kajal Raghwani HD Wallpapers":
                            appUri = "https://play.google.com/store/apps/details?id=geeksforgeekssoftwares.kajalraghwani.wallpapers";
                            break;

                        case "Shahrukh Khan HD Wallpapers":
                            appUri = "https://play.google.com/store/apps/details?id=geeksforgeekssoftwares.shahrukhkhan.wallpapers";
                            break;

                        case "Tiger Shroff HD Wallpapers":
                            appUri = "https://play.google.com/store/apps/details?id=geeksforgeekssoftwares.tigershroff.wallpapers";
                            break;

                        case "Allu Arjun HD Wallpapers":
                            appUri = "https://play.google.com/store/apps/details?id=geeksforgeekssoftwares.alluarjun.wallpapers";
                            break;

                        case "Roman Reigns HD Wallpapers":
                            appUri = "https://play.google.com/store/apps/details?id=geeksforgeekssoftwares.romanreigns.wallpapers";
                            break;
                    }
                    Uri uri = Uri.parse(appUri);
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(goToMarket);
                    }
                }
            });
            builderSingle.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

}