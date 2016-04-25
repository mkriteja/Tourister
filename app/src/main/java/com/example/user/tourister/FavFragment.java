package com.example.user.tourister;


import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import DataModel.Place;
import DataModel.Result;
import retrofit2.Call;


public class FavFragment extends android.support.v4.app.Fragment {

    private RecyclerView favrecyclerView;
    private PlacesAdapter favpadapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewMaterialAdapter favmaterialpadapter;
    private ArrayList<Result> fplaces;

    public FavFragment() {

    }


    public static FavFragment newInstance() {
        FavFragment fragment = new FavFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav, container, false);

        favrecyclerView = (RecyclerView) rootView.findViewById(R.id.favrecycler);
        favrecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        favrecyclerView.setLayoutManager(mLayoutManager);

        fplaces = new ArrayList<>();

        favpadapter = new PlacesAdapter(getActivity(),fplaces);
        favmaterialpadapter = new RecyclerViewMaterialAdapter(favpadapter);
        favrecyclerView.setAdapter(favmaterialpadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), favrecyclerView, null);


        favpadapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = position -1;
                ImageView hero = (ImageView) view.findViewById(R.id.placeimage);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("lat",fplaces.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng",fplaces.get(position).getGeometry().getLocation().getLng());
                intent.putExtra("zoom", 15.0f);
                intent.putExtra("title",fplaces.get(position).getName());
                intent.putExtra("placeid",fplaces.get(position).getPlaceId());
                intent.putExtra("photo",R.drawable.photo1);
                intent.putExtra("favfragment",true);
                AppManager.setsPhotoCache(((BitmapDrawable) hero.getDrawable()).getBitmap());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), hero, "photo_hero");
                startActivity(intent, options.toBundle());
            }
        });
        getPlacesFromFirebase();
        return rootView;
    }

    private void getPlacesFromFirebase(){
        final Firebase favref = AppManager.getRef().child(AppManager.getUseremail()).child("places");
        Query queryRef = favref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    ArrayList<String> favplaces = (ArrayList<String>) dataSnapshot.getValue();
                    new DownloadFavPlaces(favplaces,favpadapter).execute();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    private class DownloadFavPlaces extends AsyncTask<Void, Integer, ArrayList<Result>> {

        private ArrayList<String> fplaceids;
        private final WeakReference<PlacesAdapter> adapterWeakReference;

        public DownloadFavPlaces(ArrayList<String> placesids,PlacesAdapter adapter){
            fplaceids = placesids;
            adapterWeakReference = new WeakReference<>(adapter);
        }

        protected ArrayList<Result> doInBackground(Void... values) {

            ArrayList<Result> templist = new ArrayList<>();
            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            for(String placeid:fplaceids){
                Call<Place> call = service.getPlacesDetails(placeid,AppManager.getApiKey());
                try {
                    Place res = call.execute().body();
                    templist.add(res.getResult());
                }catch (Exception e){
                    return null;
                }
            }
            return templist;
        }

        protected void onPostExecute(ArrayList<Result> results) {
            fplaces.clear();
            fplaces.addAll(results);
            if (adapterWeakReference != null) {
                final PlacesAdapter adapter = adapterWeakReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    favmaterialpadapter.notifyDataSetChanged();
                }
            }
        }
    }
}
