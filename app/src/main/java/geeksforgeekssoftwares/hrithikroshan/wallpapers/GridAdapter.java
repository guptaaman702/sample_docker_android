package geeksforgeekssoftwares.hrithikroshan.wallpapers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.app.AppController;
import geeksforgeekssoftwares.hrithikroshan.wallpapers.model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerViewClickListener mListener;

    ArrayList<Wallpaper> wallpapers;
    Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int imageWidth;
    private static final int AD_DISPLAY_FREQUENCY = 15;
    public static final int CONTENT_TYPE = 0;
    public static final int AD_TYPE = 1;
    private List<NativeAd> mAdItems;
    private NativeAdsManager mNativeAdsManager;
    private Activity mActivity;


    public GridAdapter(ArrayList<Wallpaper> wallpapers, Activity activity, int imageWidth,NativeAdsManager
            nativeAdsManager,RecyclerViewClickListener listener) {
        this.wallpapers = wallpapers;
        this.mActivity = activity;
        this.imageWidth = imageWidth;
        this.mNativeAdsManager = nativeAdsManager;
        this.mAdItems = new ArrayList<>();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .native_ad_unit, parent, false);
            AdHolder holder1 = new AdHolder(inflatedView);
            return holder1;
        } else {
            Log.e("TAG", "content type : ");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
            MyViewHolder holder = new MyViewHolder(view , mListener);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == CONTENT_TYPE && position==0) {
            MyViewHolder holder1 = (MyViewHolder) holder;
            int index = position;

            Wallpaper w = wallpapers.get(index+20);
            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            holder1.networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1.networkImageView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth,
                    imageWidth));
            holder1.networkImageView.setImageUrl(w.getUrl(), imageLoader);

        }
        if(holder.getItemViewType() == CONTENT_TYPE && position!=0) {
            MyViewHolder holder1 = (MyViewHolder) holder;
            int index = position - (position / AD_DISPLAY_FREQUENCY) - 1;

            Wallpaper w = wallpapers.get(index);
            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            holder1.networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1.networkImageView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth,
                    imageWidth));
            holder1.networkImageView.setImageUrl(w.getUrl(), imageLoader);

        }
        if (holder.getItemViewType() == AD_TYPE) {
            NativeAd ad;

            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                mAdItems.add(ad);
            }

            AdHolder adHolder = (AdHolder) holder;
            adHolder.adChoicesContainer.removeAllViews();

            if (ad != null) {
                adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                adHolder.tvAdBody.setText(ad.getAdBodyText());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.tvAdSponsoredLabel.setText(ad.getSponsoredTranslation());
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                adHolder.btnAdCallToAction.setVisibility(
                        ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                AdChoicesView adChoicesView = new AdChoicesView(mActivity,
                        ad, true);
                adHolder.adChoicesContainer.addView(adChoicesView, 0);
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adHolder.ivAdIcon);
                clickableViews.add(adHolder.mvAdMedia);
                clickableViews.add(adHolder.btnAdCallToAction);
                clickableViews.add(adHolder.tvAdBody);
                clickableViews.add(adHolder.tvAdTitle);
                clickableViews.add(adHolder.tvAdSocialContext);
                clickableViews.add(adHolder.tvAdSponsoredLabel);

                ad.registerViewForInteraction(
                        adHolder.itemView,
                        adHolder.mvAdMedia,
                        adHolder.ivAdIcon,
                        clickableViews);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
            if(position==0){
                return CONTENT_TYPE;
            }
            else {
                return position % AD_DISPLAY_FREQUENCY == 0 ? AD_TYPE : CONTENT_TYPE;
            }
    }
    @Override
    public int getItemCount() {
        return wallpapers.size() + mAdItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        NetworkImageView networkImageView;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View v , RecyclerViewClickListener listener) {
            super(v);
            imageView = v.findViewById(R.id.RimgLoader);
            networkImageView = v.findViewById(R.id.Rthumbnail);
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

   class AdHolder extends RecyclerView.ViewHolder {
        MediaView mvAdMedia;
        AdIconView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdHolder(View view) {
            super(view);

            mvAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
            tvAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
            tvAdBody = (TextView) view.findViewById(R.id.native_ad_body);
            tvAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = (Button) view.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = (AdIconView) view.findViewById(R.id.native_ad_icon);
            adChoicesContainer = (LinearLayout) view.findViewById(R.id.ad_choices_container);

        }
    }
}
