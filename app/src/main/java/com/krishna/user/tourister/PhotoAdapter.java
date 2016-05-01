package com.krishna.user.tourister;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.List;


/**
 * Created by user on 4/25/2016.
 */
public class PhotoAdapter extends android.support.v7.widget.RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> mDataset;
    onItemClickListener itemClickListener;
    private final static int FADE_DURATION = 1000;

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.cardimage);
        }

        public void bindMovieData(int pos) {
            if(pos>1) {
                String imgurl = AppManager.getGooglePhotoUrl() + "maxwidth=" + 500 + "&photoreference=" + mDataset.get(pos-2) + "&key=" + AppManager.getApiKey();
                Picasso.with(context).load(imgurl).into(imageView);
            }
        }
    }

    public PhotoAdapter(Context mycontext,List<String> photoslist) {
        context = mycontext;
        mDataset = photoslist;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
            View v;
            switch (viewType) {
                case 0:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.temp, parent, false);
                    break;
                case 1:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.first_row_photo_item, parent, false);
                    break;
                default:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.photo_item, parent, false);
                    break;
            }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if(position<2) return 0;
        else if(position<5) return 1;
        return 2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bindMovieData(position);
        ViewCompat.setTransitionName(holder.imageView,String.valueOf(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v,position);
                }
            }
        });

        Animation slide_up = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);
        holder.itemView.startAnimation(slide_up);
    }
    @Override
    public int getItemCount() {
        return mDataset.size()+2;
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final onItemClickListener mitemClickListener) {
        this.itemClickListener = mitemClickListener;
    }
}

