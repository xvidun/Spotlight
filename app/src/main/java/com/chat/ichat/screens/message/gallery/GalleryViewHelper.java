package com.chat.ichat.screens.message.gallery;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chat.ichat.R;
import com.chat.ichat.core.lib.AndroidUtils;
import com.chat.ichat.screens.message.audio.ComposerViewHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vidhun on 17/05/17.
 */

public class GalleryViewHelper {
    Context mContext;

    private ViewGroup galleryLayout;
    private View galleryPickerLayout;
    private Window window;

    private boolean isGalleryViewInflated = false;
    private boolean isGalleryState = true;

    private ComposerViewHelper composerViewHelper;
    private Listener listener;

    public GalleryViewHelper(Context mContext, final ViewGroup viewGroup, Window window) {
        this.mContext = mContext;
        this.galleryLayout = viewGroup;
        this.window = window;
        this.galleryPickerLayout = createCustomView();
        this.composerViewHelper = new ComposerViewHelper(mContext, viewGroup);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public void addGalleryView() {
        if(!isGalleryViewInflated) {
            this.isGalleryViewInflated = true;
            ViewGroup.LayoutParams layoutParams = galleryLayout.getLayoutParams();
            layoutParams.height = composerViewHelper.getLayoutHeightpx();
            galleryLayout.setLayoutParams(layoutParams);
            galleryLayout.addView(galleryPickerLayout);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

            RecyclerView galleryRV = (RecyclerView) galleryLayout.findViewById(R.id.rv_gallery);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            galleryRV.setLayoutManager(linearLayoutManager);
            galleryRV.setAdapter(new ImageAdapter(mContext, getAllShownImagesPath(mContext), composerViewHelper.getLayoutHeightpx()));

            FloatingActionButton floatingActionButton = (FloatingActionButton) galleryLayout.findViewById(R.id.fab_gallery);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null) {
                        listener.onOpenGalleryClicked();
                    }
                }
            });
        }
    }

    public void removeGalleryPickerView() {
        if(isGalleryViewInflated) {
            isGalleryViewInflated = false;
            ViewGroup.LayoutParams layoutParams = galleryLayout.getLayoutParams();
            layoutParams.height = 0;
            galleryLayout.setLayoutParams(layoutParams);
            galleryLayout.removeAllViews();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    public void handleBackPress() {
        if(!isGalleryState()) {
            reset();
        } else {
            removeGalleryPickerView();
        }
    }

    public void galleryButtonToggle() {
        Activity currentActivity = (Activity) mContext;
        View view = currentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(isGalleryState) {
            addGalleryView();
            isGalleryState = false;
        } else {
            isGalleryState = true;
        }
    }

    public boolean isGalleryState() {
        return isGalleryState;
    }

    public void reset() {
        isGalleryState = true;
        removeGalleryPickerView();
    }

    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_layout, null, false);
        return view;
    }

    private ArrayList<String> getAllShownImagesPath(Context activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    /**
     * The Class ImageAdapter.
     */
    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.GalleryViewHolder>  {

        /** The context. */
        private Context context;
        private ArrayList<String> images;
        private int heightPx;

        public ImageAdapter(Context context, ArrayList<String> images, int heightPx) {
            this.context = context;
            this.images = images;
            this.heightPx = heightPx;
            this.notifyDataSetChanged();
        }

        @Override
        public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_gallery_pic, parent, false);
            return new GalleryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(GalleryViewHolder holder, int position) {
            holder.render(images.get(position), composerViewHelper.getLayoutHeightpx());
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class GalleryViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.gallery_pic)
            ImageView imageView;

            private String uri;
            public GalleryViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void render(String uri, int layoutHeightPx) {
                this.uri = uri;
                imageView.getLayoutParams().height = layoutHeightPx;
                imageView.requestLayout();

                Glide.with(context).load(uri)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }

            @OnClick(R.id.gallery_pic)
            public void onPicClicked(){
                if(listener!=null) {
                    listener.onImageClicked(uri);
                }
            }
        }
    }

    public interface Listener{
        void onOpenGalleryClicked();
        void onImageClicked(String uri);
    }
}
