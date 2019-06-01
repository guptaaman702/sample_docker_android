package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppConst;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Wallpaper;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FullScreenViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FullScreenViewActivity.class
            .getSimpleName();
    public static final String TAG_SEL_IMAGE = "selectedImage";
    private Wallpaper selectedPhoto;
    private ImageView fullImageView;
    private LinearLayout llSetWallpaper, llDownloadWallpaper;
    private Utils utils;
    private ProgressBar pbLoader;
    private CoordinatorLayout coordinatorLayout;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6007058792756351/1047734811";
    private static final String INITIALIZE_UNIT_ID = "ca-app-pub-6007058792756351~8431400818";
    private PrefManager pref;
    private int counterValue = 0;
    private int counterValueSetAsWallpaper = 0;
    private int counterValueDownloadWallpaper = 0;
    private int counterValueShareWallpaper = 0;
    private String licencseURL,licenseeURL = null;

    // Picasa JSON response node keys
    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "mediaMetadata",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "baseUrl",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_image);
        pref = new PrefManager(this);
        MobileAds.initialize(this, INITIALIZE_UNIT_ID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID);

        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref1.edit();
        counterValue = pref1.getInt("counter",0) + 1;
        int c = pref1.getInt("counter",0);
        editor.putInt("counter",counterValue);
        editor.apply();
        Log.d("counterValue",""+counterValue+"C:"+c);
        if(c % 6 == 0){
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }

                }
            });
        }
        //Showing banner ads
        AdView layout = (AdView) this.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        layout.loadAd(adRequest1);
        fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
        llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper);
        llDownloadWallpaper = (LinearLayout) findViewById(R.id.llDownloadWallpaper);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        pbLoader.setIndeterminate(true);
        pbLoader.setMax(100);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);


        utils = new Utils(getApplicationContext());

        // layout click listeners
        llSetWallpaper.setOnClickListener(this);
        llDownloadWallpaper.setOnClickListener(this);

        // setting layout buttons alpha/opacity
        llSetWallpaper.getBackground().setAlpha(70);
        llDownloadWallpaper.getBackground().setAlpha(70);


        Intent i = getIntent();
        selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);

        // check for selected photo null
        if (selectedPhoto != null) {

            // fetch photo full resolution image by making another json request
            fetchFullResolutionImage();

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * Fetching image fullresolution json
     * */


    private void fetchFullResolutionImage() {
        String mediaItemID = selectedPhoto.getPhotoJson();
        Log.d("SSSSS",mediaItemID);
        // show loader before making request
        pbLoader.setVisibility(View.VISIBLE);
        llSetWallpaper.setVisibility(View.GONE);
        llDownloadWallpaper.setVisibility(View.GONE);
        String url = AppConst.URL_MEDIAITEMSBYID
                .replace("_MEDIAITEMID_", mediaItemID);

        getSupportActionBar().hide();
        Log.d( "MEDIAITEMURL",url );
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    Log.d("OBJ",""+obj);
                    // Parsing the json response
                    JSONObject entry = obj;

                    JSONObject mediacontentArry = entry
                            .getJSONObject(TAG_MEDIA_GROUP);


                    JSONObject mediaObj = (JSONObject) mediacontentArry;

                    String fullResolutionUrl = entry
                            .getString(TAG_IMG_URL);

                    // image full resolution widht and height
                    final int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

                    Log.d(TAG, "Full resolution image. url: "
                            + fullResolutionUrl + ", w: " + width
                            + ", h: " + height);

                    ImageLoader imageLoader = AppController
                            .getInstance().getImageLoader();

                    // We download image into ImageView instead of
                    // NetworkImageView to have callback methods
                    // Currently NetworkImageView doesn't have callback
                    // methods

                    ///
                    progressStatus = 0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressStatus<100){
                                progressStatus += 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLoader.setProgress(progressStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                }catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                    ///

                    imageLoader.get(fullResolutionUrl,
                            new ImageLoader.ImageListener() {

                                @Override
                                public void onErrorResponse(
                                        VolleyError arg0) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getString(R.string.msg_wall_fetch_error),
                                            Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(
                                        ImageLoader.ImageContainer response,
                                        boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load bitmap into imageview

                                        fullImageView
                                                .setImageBitmap(response
                                                        .getBitmap());
                                        adjustImageAspect(width, height);


                                        // hide loader and show set &
                                        // download buttons
                                        pbLoader.setVisibility(View.GONE);
                                        llSetWallpaper
                                                .setVisibility(View.VISIBLE);
                                        llDownloadWallpaper
                                                .setVisibility(View.VISIBLE);

                                        getSupportActionBar().show();

                                    }
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // unable to fetch wallpapers
                // either google username is wrong or
                // devices doesn't have internet connection
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_wall_fetch_error),
                        Toast.LENGTH_LONG).show();

            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> MyData = new HashMap<String, String>();
                String auth_token = AppController.getInstance().getPrefManger().getAccessToken();
                MyData.put("Content-Type","application/x-www-form-urlencoded"); //Add the data you'd like to send to the server.
                MyData.put("Authorization",auth_token);
                return MyData;
            }
        };

        // Remove the url from cache
        AppController.getInstance().getRequestQueue().getCache().remove(url);

        // Disable the cache for this url, so that it always fetches updated
        // json
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        queue.add(jsonObjReq);
    }


    /**
     * Adjusting the image aspect ration to scroll horizontally, Image height
     * will be screen height, width will be calculated respected to height
     * */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (bWidth == 0 || bHeight == 0)
            return;

        int sHeight = 0;

        if ( Build.VERSION.SDK_INT >= 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sHeight = size.y;
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            sHeight = display.getHeight();
        }

        int new_width = (int) Math.floor((double) bWidth * (double) sHeight
                / (double) bHeight);
        params.width = new_width;
        params.height = sHeight;

        Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width
                + ", h = " + sHeight);

        fullImageView.setLayoutParams(params);
    }

    /**
     * View click listener
     * */
    @Override
    public void onClick(View v) {
        final Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();


        switch (v.getId()) {
            // button Download Wallpaper tapped
            case R.id.llDownloadWallpaper:
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref1.edit();
                counterValueDownloadWallpaper = pref1.getInt("counterValueDownloadWallpaper",0) + 1;
                int counterValueDownloadWallpaperNew = pref1.getInt("counterValueDownloadWallpaper",0);
                editor.putInt("counterValueDownloadWallpaper",counterValueDownloadWallpaper);
                editor.apply();
                Log.d("counterValueDownload",""+counterValueDownloadWallpaper+"C:"+counterValueDownloadWallpaperNew);
                if(counterValueDownloadWallpaperNew % 3 == 0){
                    callInterstialAds(bitmap,"downloadwallpaper");
                    callBannerAds();
                } else {
                    utils.saveImageToSDCard(bitmap,coordinatorLayout);
                }
                break;
            // button Set As Wallpaper tapped
            case R.id.llSetWallpaper:
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor1 = pref2.edit();

                counterValueSetAsWallpaper = pref2.getInt("counterValueSetAsWallpaper",0) + 1;
                int counterValueSetAsWallpaperNew = pref2.getInt("counterValueSetAsWallpaper",0);
                editor1.putInt("counterValueSetAsWallpaper",counterValueSetAsWallpaper);
                editor1.apply();
                Log.d("counterValueSetAsWall",""+counterValueSetAsWallpaper+"C:"+counterValueSetAsWallpaperNew);
                if(counterValueSetAsWallpaperNew % 3 == 0){
                    callInterstialAds(bitmap,"setaswallpaper");
                    callBannerAds();
                } else {
                    utils.setAsWallpaper(bitmap,coordinatorLayout);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

    private void callBannerAds() {
        AdView layout = (AdView) this.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        layout.loadAd(adRequest);
    }

    private void callInterstialAds(final Bitmap bitmap, final String type) {
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
            public void onAdClosed() {
                Log.d("ADCLOSED","HERRE");
                super.onAdClosed();
                switch (type) {
                    case "setaswallpaper":
                        utils.setAsWallpaper(bitmap, coordinatorLayout);
                        break;
                    case "downloadwallpaper":
                        utils.saveImageToSDCard(bitmap,coordinatorLayout);
                        break;
                    case "sharewallpaper":
                        utils.shareImage(bitmap, coordinatorLayout);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("FAILEDLOAD","HERRREFA");
                switch (type) {
                    case "setaswallpaper":
                        utils.setAsWallpaper(bitmap, coordinatorLayout);
                        break;
                    case "downloadwallpaper":
                        utils.saveImageToSDCard(bitmap,coordinatorLayout);
                        break;
                    case "sharewallpaper":
                        utils.shareImage(bitmap, coordinatorLayout);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void gotoURL(String url)
    {
        Uri uri =  Uri.parse(url);
        Intent goToWebsite = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        goToWebsite.addFlags(flags);

        try {
            startActivity(goToWebsite);
        } catch (ActivityNotFoundException e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_set_as_wallpaper:
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref1.edit();
                counterValueSetAsWallpaper = pref1.getInt("counterValueSetAsWallpaper",0) + 1;
                int counterValueSetAsWallpaperNew = pref1.getInt("counterValueSetAsWallpaper",0);
                editor.putInt("counterValueSetAsWallpaper",counterValueSetAsWallpaper);
                editor.apply();
                Log.d("counterValueSetAsWall",""+counterValueSetAsWallpaper+"C:"+counterValueSetAsWallpaperNew);
                if(counterValueSetAsWallpaperNew % 3 == 0){
                    callInterstialAds(bitmap,"setaswallpaper");
                    callBannerAds();
                } else {
                    utils.setAsWallpaper(bitmap,coordinatorLayout);
                }
                return true;
            case R.id.action_download:
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor1 = pref2.edit();
                counterValueDownloadWallpaper = pref2.getInt("counterValueDownloadWallpaper",0) + 1;
                int counterValueDownloadWallpaperNew = pref2.getInt("counterValueDownloadWallpaper",0);
                editor1.putInt("counterValueDownloadWallpaper",counterValueDownloadWallpaper);
                editor1.apply();
                Log.d("counterValueDownload",""+counterValueDownloadWallpaper+"C:"+counterValueDownloadWallpaperNew);
                if(counterValueDownloadWallpaperNew % 3 == 0){
                    callInterstialAds(bitmap,"downloadwallpaper");
                    callBannerAds();
                } else {
                    utils.saveImageToSDCard(bitmap,coordinatorLayout);
                }
                return true;
            case R.id.action_share:
                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor2 = pref3.edit();
                counterValueShareWallpaper = pref3.getInt("counterValueShareWallpaper",0) + 1;
                int counterValueShareWallpaperNew = pref3.getInt("counterValueShareWallpaper",0);
                editor2.putInt("counterValueShareWallpaper",counterValueShareWallpaper);
                editor2.apply();
                Log.d("counterValueShare",""+counterValueShareWallpaper+"C:"+counterValueShareWallpaperNew);
                if(counterValueShareWallpaperNew % 3 == 0){
                    callInterstialAds(bitmap,"sharewallpaper");
                    callBannerAds();
                } else {
                    utils.shareImage(bitmap,coordinatorLayout);
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}