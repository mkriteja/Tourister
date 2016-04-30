package com.example.user.tourister;


import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import DataModel.Place;
import DataModel.Result;
import retrofit2.Call;


public class PlacesFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<Result> places;
    private FrameLayout progressBar;
    private RecyclerViewMaterialAdapter materialAdapter;
    private boolean callExecuted = false;

    public PlacesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static PlacesFragment newInstance() {
        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putSerializable("placeslist",new ArrayList<Result>());
        placesFragment.setArguments(args);
        return placesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        progressBar = (FrameLayout) rootView.findViewById(R.id.placeprogress);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        places = (ArrayList<Result>) getArguments().getSerializable("placeslist");

        PlacesAdapter padapter = new PlacesAdapter(getActivity(), places);
        materialAdapter = new RecyclerViewMaterialAdapter(padapter);
        recyclerView.setAdapter(materialAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
        if(AppManager.getApicall()!=null){
            makeAsyncCall(AppManager.getApicall());
        }
        padapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position -= 1;
                ImageView hero = (ImageView) view.findViewById(R.id.placeimage);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("lat", places.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng", places.get(position).getGeometry().getLocation().getLng());
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
        progressBar.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.placesmenu, menu);
        if (menu.findItem(R.id.search) == null) {
            inflater.inflate(R.menu.placesmenu, menu);
        }
        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    item.collapseActionView();
                    PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
                    Call<Place> call = service.getPlaces(query.trim(), AppManager.getApiKey());
                    ((SliderActivity)getActivity()).callPlacesExecuteAsync(call);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
    }

    private class DownloadPlaces extends AsyncTask<Call<Place>, Integer, ArrayList<Result>> {
        private final WeakReference<RecyclerViewMaterialAdapter> adapterWeakReference;

        public DownloadPlaces(RecyclerViewMaterialAdapter adapter) {
            adapterWeakReference = new WeakReference<>(adapter);
        }

        @SafeVarargs
        protected final ArrayList<Result> doInBackground(Call<Place>... values) {
            try {
                Place res = values[0].clone().execute().body();
                return res.getResults();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Result> tresults) {
            places.clear();
            if (tresults != null) {
                for (Result result : tresults) {
                    if (!result.getPhotos().isEmpty())
                        places.add(result);
                }
                final RecyclerViewMaterialAdapter adapter = adapterWeakReference.get();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
                getArguments().putSerializable("placeslist",places);
            }
        }
    }

    public void makeAsyncCall(Call<Place> call) {
        progressBar.setVisibility(View.VISIBLE);
        if(!callExecuted) {
            callExecuted = true;
            new DownloadPlaces(materialAdapter).execute(call);
        }
    }
}
