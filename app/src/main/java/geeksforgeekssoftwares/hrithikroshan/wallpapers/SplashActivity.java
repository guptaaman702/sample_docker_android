package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppConst;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
            TAG_GPHOTO_ID = "id", TAG_T = "$t",
            TAG_ALBUM_TITLE = "title", TAG_NO_PHOTOS = "mediaItemsCount";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private int counterRateValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            setContentView(R.layout.activity_splash);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            final SharedPreferences.Editor editor = pref.edit();
            counterRateValue = pref.getInt("counterRateValue",0) + 1;
            int c = pref.getInt("counterRateValue",0);
            editor.putInt("counterRateValue",counterRateValue);
            editor.apply();
            Log.d("counterRateValueSPLASH",""+counterRateValue+"C:"+c);
            if(checkPermission()){
//                callAfterGettingPermissions();
                getHeadersData();
            }
            else {
                requestPermission();
                getHeadersData();
//                callAfterGettingPermissions();
            }
    }

    public void getHeadersData(){
        long cacheSize = AppController.getInstance().getCacheSize();
        Log.d("CACHESIZE",""+cacheSize);
        if(cacheSize>0){
            AppController.getInstance().clearCache();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST,AppConst.refresh_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    Log.d("OBJ",""+obj.getString( "access_token" ));
                    String auth = "Bearer " + obj.getString( "access_token" );
                    Log.d( "BEARER",auth );
                    callAfterGettingPermissions(auth);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("grant_type","refresh_token");
                MyData.put("client_id",AppConst.client_id);
                MyData.put("client_secret", AppConst.client_secret);
                MyData.put("refresh_token",AppConst.refresh_token);
                return MyData;
            }

            public Map<String, String> getHeaders() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Content-Type","application/x-www-form-urlencoded"); //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        MyStringRequest.setShouldCache(false);
        // Making the request
//        AppController.getInstance().addToRequestQueue(MyStringRequest);
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        queue.add(MyStringRequest);
    }

    public void callAfterGettingPermissions(String auth_token) {
        // Picasa request to get list of albums
//        String url = AppConst.URL_PICASA_ALBUMS
//                .replace("_PICASA_USER_", AppController.getInstance()
//                        .getPrefManger().getGoogleUserName());
        RequestQueue queue1 = Volley.newRequestQueue(this);

        Log.d("Authorization1",""+auth_token);

        String url = AppConst.URL_PICASA_ALBUMS;
        Log.d(TAG, "Albums request url: " + url);

        JSONObject jsonObject = null;
        // Preparing volley's json object request

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Albums Response: " + response.toString());
                        List<Category> albums = new ArrayList<Category>();
                        try {
                            // Parsing the json response
                            JSONArray entry = response.getJSONArray( "albums" );
                            Log.d("entry",""+entry);
                            // loop through albums nodes and add them to album
                            // list
                            for (int i = 0; i < entry.length(); i++) {
                                JSONObject albumObj = (JSONObject) entry.get(i);
                                // album id
                                String albumId = albumObj.getString(
                                        TAG_GPHOTO_ID);

                                // album title
                                String albumTitle = albumObj.getString(
                                        TAG_ALBUM_TITLE);

                                String albumNoOfPhotos = albumObj.getString(
                                        TAG_NO_PHOTOS);

                                Log.d("ALBUMID",albumId);

                                if(albumId.equals("AKW9TemhOU0u_3UFGZQgas3LJ87h5Wk8YWkcPFxFZnZyj_Ss0MOOoHShuha1AKZvmEWR3ZKoroHB") ||
                                        albumId.equals("AKW9Tek6qvVW5IV6oG6_eFhBHXtFArtNmZZkwTOu1DcZHjR4vp9VpPjy9S0hwT7z4TfIlBQgDJdM") ||
                                        albumId.equals("AKW9TemsxRZxTK3LoXgZRcf3ffKsi6GvvaadiaMKf40Qt3hhjC2vizsP8xCX3BJjyNS-MCH75I5m") ||
                                        albumId.equals("AKW9Temid5jy1f2OFpxTkTSOr2cuEY6iYsvu3dSvneGkGbUZHuLsk-uhL39L6_CZkxdYnF16WA0z")){
                                    Category album = new Category();
                                    album.setId(albumId);
                                    album.setTitle(albumTitle);

                                    album.setPhotoNo(albumNoOfPhotos);
                                    // add album to list
                                    albums.add(album);
                                }

                            Log.d(TAG, "Album Id: " + albumId
                                        + ", Album Title: " + albumTitle
                                        + " No Of Photos it has " +albumNoOfPhotos);
                            }

                            // Store albums in shared pref
                            AppController.getInstance().getPrefManger()
                                    .storeCategories(albums);

                            //Store access_token in shared pref
                            AppController.getInstance().getPrefManger()
                                    .storeAccessToken(auth_token);

                            // String the main activity
                            Intent intent = new Intent(getApplicationContext(),
                                    MainActivity.class);
                            startActivity(intent);
                            // closing spalsh activity
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.msg_unknown_error),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error: " + error.getMessage());

                // show error toast
                Toast.makeText(getApplicationContext(),
                        getString(R.string.splash_error),
                        Toast.LENGTH_LONG).show();

                // Unable to fetch albums
                // check for existing Albums data in Shared Preferences
                if (AppController.getInstance().getPrefManger()
                        .getCategories() != null && AppController.getInstance().getPrefManger()
                        .getCategories().size() > 0) {
                    // String the main activity
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intent);
                    // closing spalsh activity
                    finish();
                } else {
                    // Albums data not present in the shared preferences
                    // Launch settings activity, so that user can modify
                    // the settings

                   /* Intent i = new Intent(SplashActivity.this,
                            SettingsActivity.class);
                    // clear all the activities
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);*/
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                Log.d("Authorization2",""+auth_token);
                params.put("Authorization",auth_token);
                return params;
            }
        };



        // disable the cache for this request, so that it always fetches updated
        // json


        jsonObjReq.setShouldCache(false);

        // Making the request
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        queue1.add(jsonObjReq);
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }

                }
                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
