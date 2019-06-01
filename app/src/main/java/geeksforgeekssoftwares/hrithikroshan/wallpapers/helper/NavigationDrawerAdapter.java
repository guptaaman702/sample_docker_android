package geeksforgeekssoftwares.hrithikroshan.wallpapers.helper;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import geeksforgeekssoftwares.hrithikroshan.wallpapers.NavDrawerItem;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.R;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    Context context;
    public static final int NORMAL_TYPE = 0;
    public static final int DIVIDER_TYPE = 1;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("VIEWTYPE",""+viewType);
        if(viewType==DIVIDER_TYPE){
            View view = inflater.inflate(R.layout.naw_drawer_rowdivider, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        } else {
            View view = inflater.inflate(R.layout.naw_drawer_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
    }


    @Override
    public int getItemViewType(int position) {
        NavDrawerItem current = data.get(position);
        if (current.getTitle().toString().equalsIgnoreCase( "Auto Wallpaper Changer" ) || current.getTitle().toString().equalsIgnoreCase( "Rate this App" )) {
            return DIVIDER_TYPE;
        } else {
            return NORMAL_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());

        if(holder.title.getText().toString().equalsIgnoreCase( "Auto Wallpaper Changer" )
                || holder.title.getText().toString().equalsIgnoreCase( "Auto Wallpaper Changer Settings" )
                || holder.title.getText().toString().equalsIgnoreCase( "Rate this App" )
                || holder.title.getText().toString().equalsIgnoreCase( "Sharing is Caring!" )
                || holder.title.getText().toString().equalsIgnoreCase( "App Settings" )
                || holder.title.getText().toString().equalsIgnoreCase( "Try Other Top Apps")
                || holder.title.getText().toString().equalsIgnoreCase( "Sponsor")){
            holder.photocount.setText("");
        } else{
            holder.photocount.setText("(" + current.getPhotoNo() + ")");
        }
        /*if(position==0)
        {
            holder.photocount.setText("(100)");
        }else {
            holder.photocount.setText("(" + current.getPhotoNo() + ")");
        }*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView photocount;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            photocount = (TextView) itemView.findViewById(R.id.photocount);
        }
    }
}