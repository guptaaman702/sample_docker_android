package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import geeksforgeekssoftwares.hrithikroshan.wallpapers.helper.GridViewAdapter;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Wallpaper;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridFragment extends Fragment {
    private static final String TAG = GridFragment.class.getSimpleName();
    private Utils utils;
    private GridViewAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private static final String bundleAlbumId = "albumId";
    private String selectedAlbumId;
    private List<Wallpaper> photosList;
    private ProgressBar pbLoader;
    private PrefManager pref;

    // Picasa JSON response node keys
    private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "mediaMetadata",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "baseUrl",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height", TAG_ID = "id",
            TAG_T = "filename";

    public GridFragment() {
    }


    public static GridFragment newInstance(String albumId) {
        GridFragment f = new GridFragment();
        Bundle args = new Bundle();
        args.putString(bundleAlbumId, albumId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        photosList = new ArrayList<Wallpaper>();
        pref = new PrefManager(getActivity());


        // Getting Album Id of the item selected in navigation drawer
        // if Album Id is null, user is selected recently added option
        if (getArguments().getString(bundleAlbumId) != null) {
            selectedAlbumId = getArguments().getString(bundleAlbumId);
            Log.d(TAG,
                    "Selected album id: "
                            + getArguments().getString(bundleAlbumId));
        } else {
            Log.d(TAG, "Selected recentlyed added album");
            selectedAlbumId = null;
        }

        // Preparing the request url
        String url = null;
        if (selectedAlbumId == null) {
            // Recently added album url
            url = AppConst.URL_MEDIAITEMS;
        } else {
            // Selected an album, replace the Album Id in the url
            url = AppConst.URL_MEDIAITEMS;
        }

        Log.d(TAG, "Final request url: " + url);

        View rootView = inflater.inflate(R.layout.fragment_grid, container,
                false);

        // Hiding the gridview and showing loader image before making the http
        // request
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setVisibility(View.GONE);
        pbLoader = (ProgressBar) rootView.findViewById(R.id.pbLoader);
        pbLoader.setVisibility(View.VISIBLE);

        utils = new Utils(getActivity());

        // initToolBar(rootView);
        /**
         * Making volley's json object request to fetch list of photos of an
         * album
         * */
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    JSONArray entry = obj.getJSONArray( "mediaItems" );
                    // looping through each photo and adding it to list
                    // data set
                    for (int i = 0; i < entry.length(); i++) {
                        JSONObject photoObj = (JSONObject) entry.get(i);
                        JSONObject mediacontentArry = photoObj
                                .getJSONObject(TAG_MEDIA_GROUP);
                        if (mediacontentArry.length() > 0) {
                            JSONObject mediaObj = (JSONObject) mediacontentArry;

                            String url = photoObj
                                    .getString(TAG_IMG_URL);

                            String photoJson = photoObj.getString("id");
                            int width = mediaObj.getInt(TAG_IMG_WIDTH);
                            int height = mediaObj
                                    .getInt(TAG_IMG_HEIGHT);

                            Wallpaper p = new Wallpaper(photoJson, url, width,
                                    height);

                            // Adding the photo to list data set
                            photosList.add(p);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Notify list adapter about dataset changes. So
                // that it renders grid again
                adapter.notifyDataSetChanged();

                // Hide the loader, make grid visible
                pbLoader.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // unable to fetch wallpapers
                // either google username is wrong or
                // devices doesn't have internet connection
                Toast.makeText(getActivity(),
                        getString(R.string.msg_wall_fetch_error),
                        Toast.LENGTH_LONG).show();

            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("albumId",selectedAlbumId);
                MyData.put("pageSize","100");
                return MyData;
            }

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
        // Initilizing Grid View
        InitilizeGridLayout();

        // Gridview adapter
        adapter = new GridViewAdapter(getActivity(), photosList, columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);

        // Grid item select listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // On selecting the grid image, we launch fullscreen activity
                Intent i = new Intent(getActivity(),
                        FullScreenViewActivity.class);

                // Passing selected image to fullscreen activity
                Wallpaper photo = photosList.get(position);

                i.putExtra(FullScreenViewActivity.TAG_SEL_IMAGE, photo);
                startActivity(i);
            }
        });

        return rootView;
    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * */
    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConst.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((utils.getScreenWidth() - ((pref
                .getNoOfGridColumns() + 1) * padding)) / pref
                .getNoOfGridColumns());

        // Setting number of grid columns
        gridView.setNumColumns(pref.getNoOfGridColumns());
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);

        // Setting horizontal and vertical padding
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}