package com.example.user.tourister;


import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import DataModel.Place;
import DataModel.Result;
import retrofit2.Call;


public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewMaterialAdapter materialAdapter;
    private ArrayList<Result> places;
    private ProgressBar progressBar;

    public PlacesFragment() {

    }

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.placesprogressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        places = new ArrayList<>();
        PlacesAdapter padapter = new PlacesAdapter(getActivity(), places);
        materialAdapter = new RecyclerViewMaterialAdapter(padapter);
        recyclerView.setAdapter(materialAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
        new DownloadPlaces(materialAdapter).execute();
        padapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = position - 1;
                ImageView hero = (ImageView) view.findViewById(R.id.placeimage);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("lat", places.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng", places.get(position).getGeometry().getLocation().getLng());
                intent.putExtra("zoom", 15.0f);
                intent.putExtra("title", places.get(position).getName());
                intent.putExtra("placeid", places.get(position).getPlaceId());
                intent.putExtra("photo", R.drawable.photo1);
                AppManager.setsPhotoCache(((BitmapDrawable) hero.getDrawable()).getBitmap());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), hero, "photo_hero");
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return rootView;
    }


    private class DownloadPlaces extends AsyncTask<Void, Integer, ArrayList<Result>> {
        private final WeakReference<RecyclerViewMaterialAdapter> adapterWeakReference;

        public DownloadPlaces(RecyclerViewMaterialAdapter adapter) {
            adapterWeakReference = new WeakReference<>(adapter);
        }

        protected ArrayList<Result> doInBackground(Void... values) {

            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            Call<Place> call = service.getPlaces("Museums in New york", AppManager.getApiKey());
            try {
                Place res = call.execute().body();
                return res.getResults();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Result> tresults) {
            places.addAll(tresults);
            if (adapterWeakReference != null) {
                final RecyclerViewMaterialAdapter adapter = adapterWeakReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
