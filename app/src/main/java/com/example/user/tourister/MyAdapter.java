package com.example.user.tourister;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 2/15/2016.
 */
public class MyAdapter extends android.support.v7.widget.RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Map<String,?>> mDataset;
    private Context context;
    onItemClickListener itemClickListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView description;
        public TextView title;
        public CheckBox checkBox;
        public RatingBar movie_rb;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)v.findViewById(R.id.cardimg);
            description = (TextView) v.findViewById(R.id.carddescp);
            checkBox = (CheckBox) v.findViewById(R.id.cardcb);
            title = (TextView) v.findViewById(R.id.moviename);
            movie_rb = (RatingBar) v.findViewById(R.id.movieratingBar);


          checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (itemClickListener != null) {
                        itemClickListener.onChangeClick(isChecked, getAdapterPosition());
                    }
                }
            });

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

        public void bindMovieData(Map<String,?> movie){
            imageView.setImageResource((Integer) movie.get("image"));
            description.setText((String) movie.get("description"));
            title.setText((String) movie.get("name"));
            movie_rb.setRating((float) (((Double) movie.get("rating")) / 2));
            if(movie.containsKey("SelectStatus"))
                checkBox.setChecked(Boolean.parseBoolean(movie.get("SelectStatus").toString()));

        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context mycontext,List<Map<String,?>> myDataset) {

        context = mycontext;
        mDataset = myDataset;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v;
        switch (viewType){
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_item, parent, false);
                break;
        }

        return new ViewHolder(v);
    }


    @Override
    public int getItemViewType(int position){
        if(position==0)
            return 0;
        return 1;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Map<String,?> movie = mDataset.get(position);
        holder.bindMovieData(movie);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onItemClickListener{
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
        public void onChangeClick(boolean ischecked, int position);
    }

    public void setOnItemClickListener(final onItemClickListener mitemClickListener){
        this.itemClickListener = mitemClickListener;
    }
}
