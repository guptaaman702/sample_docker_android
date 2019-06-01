package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.helper.NavigationDrawerAdapter;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Category;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.utils.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentDrawer extends Fragment {
    private static String TAG = FragmentDrawer.class.getSimpleName();
    private PrefManager pref;

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private static String[] photos = null;
    private FragmentDrawerListener drawerListener;

    private List<Category> albumsList;
    private ArrayList<NavDrawerItem> navDrawerItems;

    TimePicker timePicker;
    TimePickerDialog timePickerDialog;
    final static int RQS_1 = 1;
    private int previouslySelectedTag = 0;

    FragmentActivity mContext;
    File[] wallpaperImages = null;
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6007058792756351/1047734811";
    private static final String INITIALIZE_UNIT_ID = "ca-app-pub-6007058792756351~8431400818";

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setPhotoNo(photos[i]);
            data.add(navItem);
        }
        return data;



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        // titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Getting the albums from shared preferences
        albumsList = AppController.getInstance().getPrefManger().getCategories();

        // Insert "Recently Added" in navigation drawer first position
        /*Category recentAlbum = new Category(null, getString(R.string.nav_drawer_recently_added), "");

        albumsList.add(0, recentAlbum);*/

        // Loop through albums in add them to navigation drawer adapter
        for (Category a : albumsList) {
            navDrawerItems.add(new NavDrawerItem(true, a.getId(), a.getTitle(), a.getPhotoNo()));
            // titles a.getTitle()
            //  photos = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavigationDrawerAdapter(getActivity(), navDrawerItems );

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NavDrawerItem item = navDrawerItems.get(position);
                if (item.getAlbumId().equals("5") ) {
                    setTime();
                } else if (item.getAlbumId().equals("6") ) {
//                    openTimePickerDialog(false);
                    Intent intent = new Intent(getActivity(),
                            AutomaticChangerActivity.class);
                    startActivity(intent);
                } else if (item.getAlbumId().equals("7") ) {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
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
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                } else if(item.getAlbumId().equals("8")) {
                    final String appPackageName = getActivity().getPackageName();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the most awesome and hot collection of Hrithik Roshan HD Wallpapers at:"+ "\n\n" +  "https://play.google.com/store/apps/details?id=" + appPackageName
                            + " ." + "\n\n" + "This App has Auto Wallpaper Changer which can change background image Automatically after specified time interval." + "\n\n" + "There are many stunning feature provided in this app which you can't miss.");
                    sendIntent.setType("text/plain");
                    getActivity().startActivity(sendIntent);
                } else if(item.getAlbumId().equals("9")) {
                    Intent intent = new Intent(getActivity(),
                            SettingsActivity.class);
                    startActivity(intent);
                } else if(item.getAlbumId().equals("10")) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=gpatechnologies");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    }catch (ActivityNotFoundException e){
                        startActivity(goToMarket);
                    }

                } else if(item.getAlbumId().equals("21")) {
                    MobileAds.initialize(getContext(), INITIALIZE_UNIT_ID);
                    interstitialAd = new InterstitialAd(getContext());

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

                } else {
                    drawerListener.onDrawerItemSelected( view, position );

                    mDrawerLayout.closeDrawer( containerView );
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

//    private void openTimePickerDialog(boolean is24r) {
//        Calendar calendar = Calendar.getInstance();
//
//        timePickerDialog = new TimePickerDialog(getContext(),
//                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
//                calendar.get(Calendar.MINUTE), is24r);
//        timePickerDialog.setTitle("Set Alarm Time");
//
//        timePickerDialog.show();
//
//    }
//
//    TimePickerDialog.OnTimeSetListener onTimeSetListener  = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker timePicker, int i, int i1) {
//
//            Calendar calNow = Calendar.getInstance();
//            Calendar calSet = (Calendar) calNow.clone();
//
//            calSet.set(Calendar.HOUR_OF_DAY, i);
//            calSet.set(Calendar.MINUTE, i1);
//            calSet.set(Calendar.SECOND, 0);
//            calSet.set(Calendar.MILLISECOND, 0);
//
//            if (calSet.compareTo(calNow) <= 0) {
//                // Today Set time passed, count to tomorrow
//                calSet.add(Calendar.DATE, 1);
//            }
//
//            setTime(calSet);
//        }
//    };


    ///how u gonna set the wallpapers???? >>>>
    private void setTime(){
        SharedPreferences pref1 = getContext().getSharedPreferences("TimeChangerPref", 0);
        pref = new PrefManager(getContext());

        final SharedPreferences.Editor editor = pref1.edit();
        previouslySelectedTag = pref1.getInt("previouslySelectedTag",0);
        int intervalMillis;
        if(previouslySelectedTag==0){
            intervalMillis = 45;
            editor.putInt("previouslySelectedTag",intervalMillis);
            editor.apply();
        } else {
            intervalMillis = previouslySelectedTag;
        }
        Intent intent = new Intent(mContext,AutomaticChanger.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,RQS_1,intent,0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService( Context.ALARM_SERVICE );

        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +pref.getGalleryName());
        if(!dir.exists())
            dir.mkdir();
        wallpaperImages = dir.listFiles();

        if(wallpaperImages.length<2){
            Toast.makeText(getActivity(),
                    "Download Atleast 2 or more Wallpaper before using Auto Wallpaper Changer",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(),
                    "Auto Wallpaper Changer has started and your wallpaper will be changing automatically based on your preference.",
                    Toast.LENGTH_LONG).show();
            alarmManager.setInexactRepeating( AlarmManager.RTC, System.currentTimeMillis(), intervalMillis*60000,pendingIntent);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        mContext = (FragmentActivity) context;
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}