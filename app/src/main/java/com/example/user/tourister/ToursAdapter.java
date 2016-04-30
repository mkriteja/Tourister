package com.example.user.tourister;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DataModel.Photo;
import DataModel.Result;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.firebase.client.Query;

/**
 * Created by apple on 26/04/16.
 */
public class ToursAdapter extends android.support.v7.widget.RecyclerView.Adapter<ToursAdapter.ViewHolder> {

    private List<Map<String,?>> mDataset;
    private Context context;
    onItemClickListener itemClickListener;

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vTitle;
        public TextView vPrice;

        public ViewHolder(View v) {
            super(v);
            vIcon = (ImageView) v.findViewById(R.id.tourimage);
            vTitle = (TextView) v.findViewById(R.id.imagename_tour);
            vPrice = (TextView) v.findViewById(R.id.price_tour);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

        }

        public void bindMovieData(Map<String,?> place) {

            vTitle.setText(place.get("Name").toString());
            vPrice.setText("$"+place.get("price").toString());
            Picasso.with(context).load(place.get("url").toString()).into(vIcon);

        }
    }

    public ToursAdapter(Context mycontext, ArrayList<Map<String,?>> myDataset) {
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
                        .inflate(R.layout.tour_card_item, parent, false);
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

        Map<String,?> place = mDataset.get(position);
        holder.bindMovieData(place);

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

