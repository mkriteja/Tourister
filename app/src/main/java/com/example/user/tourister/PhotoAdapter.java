package com.example.user.tourister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import DataModel.Photo;
import DataModel.Result;

/**
 * Created by user on 4/25/2016.
 */
public class PhotoAdapter extends android.support.v7.widget.RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> mDataset;
    onItemClickListener itemClickListener;

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.cardimage);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

        }

        public void bindMovieData(int pos) {
            String imgurl= AppManager.getGooglePhotoUrl()+"maxwidth="+1500+"&photoreference="+mDataset.get(pos)+"&key="+AppManager.getApiKey();
            Picasso.with(context).load(imgurl).into(imageView);
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
                case 1:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.temp, parent, false);
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
        if(position<2) return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindMovieData(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final onItemClickListener mitemClickListener) {
        this.itemClickListener = mitemClickListener;
    }
}

