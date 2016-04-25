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
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import DataModel.Place;
import DataModel.Result;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlacesAdapter padapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewMaterialAdapter tempadapter;
    private ArrayList<Result> places;

    public PlacesFragment() {

    }
    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        places = new ArrayList<>();
        padapter = new PlacesAdapter(getActivity(),places);
        tempadapter = new RecyclerViewMaterialAdapter(padapter);
        recyclerView.setAdapter(tempadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
        new DownloadPlaces(padapter).execute();
        padapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = position-1;
                ImageView hero = (ImageView) view.findViewById(R.id.placeimage);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("lat",places.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng",places.get(position).getGeometry().getLocation().getLng());
                intent.putExtra("zoom", 15.0f);
                intent.putExtra("title",places.get(position).getName());
                intent.putExtra("placeid",places.get(position).getPlaceId());
                intent.putExtra("photo",R.drawable.photo1);
                AppManager.setsPhotoCache(((BitmapDrawable) hero.getDrawable()).getBitmap());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), hero, "photo_hero");
                startActivity(intent, options.toBundle());
            }
        });

        return rootView;
    }


    private class DownloadPlaces extends AsyncTask<Void, Integer, ArrayList<Result>> {
        private final WeakReference<PlacesAdapter> adapterWeakReference;

        public DownloadPlaces(PlacesAdapter adapter){
            adapterWeakReference = new WeakReference<>(adapter);
        }

        protected ArrayList<Result> doInBackground(Void... values) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PlacesInterface service = retrofit.create(PlacesInterface.class);
            Call<Place> call = service.getPlaces("Museums in New york",AppManager.getApiKey());
            try {
             Place res = call.execute().body();
                return res.getResults();
            }catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Result> tresults) {
            places.clear();
            places.addAll(tresults);
            if (adapterWeakReference != null) {
                final PlacesAdapter adapter = adapterWeakReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    tempadapter.notifyDataSetChanged();
                }
            }
        }
    }

}
