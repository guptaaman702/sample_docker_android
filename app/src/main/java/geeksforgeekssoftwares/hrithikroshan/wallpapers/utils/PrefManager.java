package geeksforgeekssoftwares.hrithikroshan.wallpapers.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppConst;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AwesomeWallpapers";

    // Google's username
    private static final String KEY_GOOGLE_USERNAME = "google_username";

    // No of grid columns
    private static final String KEY_NO_OF_COLUMNS = "no_of_columns";

    // Gallery directory name
    private static final String KEY_GALLERY_NAME = "gallery_name";

    // gallery albums key
    private static final String KEY_ALBUMS = "albums";

    private static final String ACCESS_TOKEN = "access_token";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    /**
     * Storing google username
     * */
    public void setGoogleUsername(String googleUsername) {
        editor = pref.edit();

        editor.putString(KEY_GOOGLE_USERNAME, googleUsername);

        // commit changes
        editor.commit();
    }

    public String getGoogleUserName() {
        return pref.getString(KEY_GOOGLE_USERNAME, AppConst.PICASA_USER);
    }

    /**
     * store number of grid columns
     * */
    public void setNoOfGridColumns(int columns) {
        editor = pref.edit();

        editor.putInt(KEY_NO_OF_COLUMNS, columns);

        // commit changes
        editor.commit();
    }

    public int getNoOfGridColumns() {
        return pref.getInt(KEY_NO_OF_COLUMNS, AppConst.NUM_OF_COLUMNS);
    }

    /**
     * storing gallery name
     * */
    public void setGalleryName(String galleryName) {
        editor = pref.edit();

        editor.putString(KEY_GALLERY_NAME, galleryName);

        // commit changes
        editor.commit();
    }

    public String getGalleryName() {
        return pref.getString(KEY_GALLERY_NAME, AppConst.SDCARD_DIR_NAME);
    }

    /**
     * Storing albums in shared preferences
     * */
    public void storeCategories(List<Category> albums) {
        editor = pref.edit();
        Gson gson = new Gson();

        Log.d(TAG, "Albums: " + gson.toJson(albums));

        editor.putString(KEY_ALBUMS, gson.toJson(albums));

        // save changes
        editor.commit();
    }

    public void storeAccessToken(String access_token) {
        editor = pref.edit();
        Gson gson = new Gson();

        Log.d(TAG, "ACCESS_TOKEN: " + access_token);

        editor.putString(ACCESS_TOKEN, access_token);

        // save changes
        editor.commit();
    }

    public String getAccessToken() {
        String access_token;
        if (pref.contains( ACCESS_TOKEN )) {
            access_token = pref.getString(ACCESS_TOKEN, null);
        }  else
            return null;
        return access_token;
    }

    /**
     * Fetching albums from shared preferences. Albums will be sorted before
     * returning in alphabetical order
     * */
    public List<Category> getCategories() {
        List<Category> albums = new ArrayList<Category>();

        if (pref.contains(KEY_ALBUMS)) {
            String json = pref.getString(KEY_ALBUMS, null);
            Gson gson = new Gson();
            Category[] albumArry = gson.fromJson(json, Category[].class);

            albums = Arrays.asList(albumArry);
            albums = new ArrayList<Category>(albums);
        } else
            return null;

        List<Category> allAlbums = albums;

//         Sort the albums in alphabetical order
        Collections.sort(allAlbums, new Comparator<Category>() {
            public int compare(Category a1, Category a2) {
                return a1.getTitle().compareToIgnoreCase(a2.getTitle());
            }
        });

        Category album = new Category();
        album.setId("5");
        album.setTitle("Auto Wallpaper Changer");

        album.setPhotoNo("");
        // add album to list
        allAlbums.add(album);

        Category album21 = new Category();
        album21.setId("21");
        album21.setTitle("Sponsor");

        // add album to list
        allAlbums.add(album21);

        Category album1 = new Category();
        album1.setId("6");
        album1.setTitle("Auto Wallpaper Changer Settings");

        album1.setPhotoNo("");
        // add album to list
        allAlbums.add(album1);

        Category album2 = new Category();
        album2.setId("7");
        album2.setTitle("Rate this App");

        album2.setPhotoNo("");
        // add album to list
        allAlbums.add(album2);

        Category album3 = new Category();
        album3.setId("8");
        album3.setTitle("Sharing is Caring!");

        album3.setPhotoNo("");
        // add album to list
        allAlbums.add(album3);

        Category album4 = new Category();
        album4.setId("9");
        album4.setTitle("App Settings");

        album4.setPhotoNo("");
        // add album to list
        allAlbums.add(album4);

        Category album5 = new Category();
        album5.setId("10");
        album5.setTitle("Try Other Top Apps");

        album5.setPhotoNo("");
        // add album to list
        allAlbums.add(album5);

        return allAlbums;

    }

    /**
     * Comparing albums titles for sorting
     * */
    public class CustomComparator implements Comparator<Category> {
        @Override
        public int compare(Category c1, Category c2) {
            return c1.getTitle().compareTo(c2.getTitle());
        }
    }

}
