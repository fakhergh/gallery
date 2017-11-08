package com.benboubaker.gallery.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benboubaker.gallery.PreviewActivity;
import com.benboubaker.gallery.R;
import com.benboubaker.gallery.models.Image;
import com.benboubaker.gallery.ui.SquareImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by fekhe on 06/11/2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private List<Image> mImages;

    public RecyclerViewAdapter(Activity activity, List<Image> mImages) {
        this.activity = activity;
        this.mImages = mImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Image image = mImages.get(holder.getAdapterPosition());
        Picasso.with(activity)
                .load(new File(image.getPath()))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.thumbnail_place_holder)
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PreviewActivity.class);
                intent.putExtra(Image.TAG, image);
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        holder.thumbnail,
                        activity.getResources().getString(R.string.transition_name));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activity.startActivity(intent, opts.toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void updateAndNotify(List<Image> images) {
        mImages = images;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SquareImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (SquareImageView) itemView.findViewById(R.id.thumbanil);
        }
    }
}
