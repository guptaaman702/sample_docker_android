package geeksforgeekssoftwares.hrithikroshan.wallpapers.app;

/**
 * Created by geekmentors on 1/26/16.
 */
public class AppConst {
    // Number of columns of Grid View
    // by default 2 but user can configure this in settings activity
    public static final int NUM_OF_COLUMNS = 2;

    public static final String refresh_token = "1/OESHdDl1t3RZkWU499L6jUtKpASPcp4s9gF2HfUrwMI";
    public static final String  client_id = "56270733579-1lsb26g9p8jcmu4anfc8nrhgv0qk4f84.apps.googleusercontent.com";
    public static final String client_secret = "fmWQB8SaUkC0NK1r-lBlgwco";
    public static final String refresh_url = "https://www.googleapis.com/oauth2/v4/token";;

    // Gridview image padding
    public static final int GRID_PADDING = 0; // in dp

    // Gallery directory name to save wallpapers
    public static final String SDCARD_DIR_NAME = "hdwallpapershrithikroshanhdwallpapersgpa";

    // Picasa/Google web album username
    public static final String PICASA_USER = "gma1.2.3702@gmail.com";

    // Public albums list url
//    public static final String URL_PICASA_ALBUMS = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=album&alt=json";
    public static final String URL_PICASA_ALBUMS = "https://content-photoslibrary.googleapis.com/v1/albums?pageSize=50";
    // Picasa album photos url
    public static final String URL_ALBUM_PHOTOS = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_/albumid/_ALBUM_ID_?alt=json";

    // Picasa recenlty added photos url
    public static final String URL_RECENTLY_ADDED = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=photo&alt=json";

    public static final String URL_ACCESSTOKEN = "grant_type=refresh_token&client_id=_client_id_&client_secret=_client_secret_&refresh_token=_refresh_token_";
    public static final String URL_MEDIAITEMS = "https://content-photoslibrary.googleapis.com/v1/mediaItems:search";
    public static final String URL_MEDIAITEMSBYID = "https://content-photoslibrary.googleapis.com/v1/mediaItems/_MEDIAITEMID_";

}
