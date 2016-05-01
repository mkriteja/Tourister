package com.krishna.user.tourister;


import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private ImageView deleteView;
    private FrameLayout progressbar;
    private FrameLayout nofavorites;

    public FavFragment() {

    }


    public static FavFragment newInstance() {
        FavFragment fragment = new FavFragment();
        Bundle args = new Bundle();
        args.putSerializable("favoriteplaces", new ArrayList<Result>());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
        progressbar = (FrameLayout) rootView.findViewById(R.id.favprogress);
        progressbar.setVisibility(View.VISIBLE);
        favrecyclerView = (RecyclerView) rootView.findViewById(R.id.favrecycler);
        favrecyclerView.setHasFixedSize(true);
        nofavorites = (FrameLayout) rootView.findViewById(R.id.nofavroites);

        mLayoutManager = new LinearLayoutManager(getActivity());
        favrecyclerView.setLayoutManager(mLayoutManager);

        deleteView = (ImageView) rootView.findViewById(R.id.deleteicon);

        fplaces = (ArrayList<Result>) getArguments().getSerializable("favoriteplaces");

        favpadapter = new PlacesAdapter(getActivity(), fplaces);
        favmaterialpadapter = new RecyclerViewMaterialAdapter(favpadapter);
        favrecyclerView.setAdapter(favmaterialpadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), favrecyclerView, null);


        favpadapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = position - 1;
                ImageView hero = (ImageView) view.findViewById(R.id.placeimage);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("lat", fplaces.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng", fplaces.get(position).getGeometry().getLocation().getLng());
                intent.putExtra("zoom", 15.0f);
                intent.putExtra("title", fplaces.get(position).getName());
                intent.putExtra("placeid", fplaces.get(position).getPlaceId());
                intent.putExtra("photo", R.drawable.photo1);
                intent.putExtra("favfragment", true);
                AppManager.setsPhotoCache(((BitmapDrawable) hero.getDrawable()).getBitmap());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), hero, "photo_hero");
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                deleteView.setVisibility(View.VISIBLE);
                ClipData data = ClipData.newPlainText("Position", String.valueOf(position));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, position);
            }
        });
       deleteView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        ((View) event.getLocalState()).setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        int pos = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                        fplaces.remove(pos - 1);
                        getArguments().putSerializable("favoriteplaces", fplaces);
                        favmaterialpadapter.notifyItemRemoved(pos);
                        updateFirebase();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        ((View) event.getLocalState()).setVisibility(View.VISIBLE);
                        deleteView.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        if(fplaces.isEmpty())
            getPlacesFromFirebase();
        else
            progressbar.setVisibility(View.GONE);
        return rootView;
    }

    private void updateFirebase() {
        ArrayList<String> remainingplaceids = new ArrayList<>();
        for (Result r : fplaces) {
            remainingplaceids.add(r.getPlaceId());
        }
        AppManager.getRef().child(AppManager.getUseremail()).child("places").setValue(remainingplaceids);
        if (remainingplaceids.isEmpty()) {
            nofavorites.setVisibility(View.VISIBLE);
        }
    }

    private void getPlacesFromFirebase() {
        final Firebase favref = AppManager.getRef().child(AppManager.getUseremail()).child("places");
        Query queryRef = favref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    nofavorites.setVisibility(View.GONE);
                    ArrayList<String> favplaces = (ArrayList<String>) dataSnapshot.getValue();
                    new DownloadFavPlaces(favplaces, favmaterialpadapter).execute();
                } else {
                    nofavorites.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private class DownloadFavPlaces extends AsyncTask<Void, Integer, ArrayList<Result>> {

        private ArrayList<String> fplaceids;
        private final WeakReference<RecyclerViewMaterialAdapter> adapterWeakReference;

        public DownloadFavPlaces(ArrayList<String> placesids, RecyclerViewMaterialAdapter adapter) {
            fplaceids = placesids;
            adapterWeakReference = new WeakReference<>(adapter);
        }

        protected ArrayList<Result> doInBackground(Void... values) {

            ArrayList<Result> templist = new ArrayList<>();
            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            for (String placeid : fplaceids) {
                Call<Place> call = service.getPlacesDetails(placeid, AppManager.getApiKey());
                try {
                    Place res = call.execute().body();
                    templist.add(res.getResult());
                } catch (Exception e) {
                    return null;
                }
            }
            return templist;
        }

        protected void onPostExecute(ArrayList<Result> results) {
            fplaces.clear();
            fplaces.addAll(results);
            getArguments().putSerializable("favoriteplaces", fplaces);
            final RecyclerViewMaterialAdapter adapter = adapterWeakReference.get();
            if (adapter != null) {
                progressbar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
