package com.example.user.tourister;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import DataModel.Photo;
import DataModel.Result;

/**
 * Created by user on 4/19/2016.
 */
public class PlacesAdapter extends android.support.v7.widget.RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private ArrayList<Result> mDataset;
    private Context context;
    onItemClickListener itemClickListener;

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView placename;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.placeimage);
            placename = (TextView) v.findViewById(R.id.imagename_tv);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemLongClick(v, getAdapterPosition());
                    }
                    return true;
                }
            });

        }

        public void bindMovieData(String name, Photo currphoto) {
            String imgurl= AppManager.getGooglePhotoUrl()+"maxwidth="+1500+"&photoreference="+currphoto.getPhotoReference()+"&key="+AppManager.getApiKey();
            Picasso.with(context).load(imgurl).into(imageView);
            placename.setText(name);

        }
    }

    public PlacesAdapter(Context mycontext, ArrayList<Result> myDataset) {
        context = mycontext;
        mDataset = myDataset;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v;
        switch (viewType) {
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.place_card_item, parent, false);
                break;
        }

        return new ViewHolder(v);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Result place = mDataset.get(position);
        Photo currentphoto = place.getPhotos().get(0);
        holder.bindMovieData(place.getName(),currentphoto);
        Animation slide_up = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);
        holder.itemView.startAnimation(slide_up);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(final onItemClickListener mitemClickListener) {
        this.itemClickListener = mitemClickListener;
    }
}
