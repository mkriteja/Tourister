package com.krishna.user.tourister;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DataModel.Photo;
import DataModel.Place;
import DataModel.Result;
import retrofit2.Call;

public class PhotoFragment extends Fragment {


    private RecyclerView photorecyclerview;
    private PhotoAdapter photoAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerViewMaterialAdapter photomaterialpadapter;
    private List<String> photoreferenceslist;
    private AtomicInteger counter = new AtomicInteger(0);
    private int placecount;
    private FrameLayout progressBar;
    OnPhotoInteractionListener mListener;

    public PhotoFragment() {

    }

    public static PhotoFragment newInstance() {
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable("photoreferencelist", new ArrayList<String>());
        photoFragment.setArguments(args);
        return photoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        progressBar = (FrameLayout) rootView.findViewById(R.id.photoprogress);
        progressBar.setVisibility(View.VISIBLE);

        photorecyclerview = (RecyclerView) rootView.findViewById(R.id.photorecycler);
        photorecyclerview.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        photorecyclerview.setLayoutManager(gridLayoutManager);

        photoreferenceslist = Collections.synchronizedList((ArrayList<String>) getArguments().getSerializable("photoreferencelist"));
        photoAdapter = new PhotoAdapter(getActivity(), photoreferenceslist);
        photomaterialpadapter = new RecyclerViewMaterialAdapter(photoAdapter);
        photorecyclerview.setAdapter(photomaterialpadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), photorecyclerview, null);
        if (photoreferenceslist.isEmpty() && AppManager.getApicall() != null)
            executeCurrentphotos(AppManager.getApicall());
        else
            progressBar.setVisibility(View.GONE);
        photoAdapter.setOnItemClickListener(new PhotoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.onPhotoInteraction(position, view);
            }
        });
        return rootView;
    }

    private void setUpPhotos() {
        photomaterialpadapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private class DownloadPlaceDetails extends AsyncTask<Void, Integer, Result> {
        private String placeid;

        public DownloadPlaceDetails(String id) {
            placeid = id;
        }

        protected DataModel.Result doInBackground(Void... values) {


            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            Call<Place> call = service.getPlacesDetails(placeid, AppManager.getApiKey());
            try {
                Place res = call.execute().body();
                return res.getResult();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(DataModel.Result tresult) {
            if (tresult != null && photoreferenceslist != null) {
                if (tresult.getPhotos() != null) {
                    for (Photo p : tresult.getPhotos()) {
                        photoreferenceslist.add(p.getPhotoReference());
                    }
                }
            }
            if (counter.addAndGet(1) == placecount) {
                ArrayList<String> bundlelist = new ArrayList<>(photoreferenceslist);
                getArguments().putSerializable("photoreferencelist", bundlelist);
                setUpPhotos();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoInteractionListener) {
            mListener = (OnPhotoInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPhotoInteractionListener {
        void onPhotoInteraction(int position, View view);
    }

    private class DownloadPlaces extends AsyncTask<Call<Place>, Integer, ArrayList<Result>> {

        public DownloadPlaces() {
        }

        @SafeVarargs
        final protected ArrayList<Result> doInBackground(Call<Place>... values) {
            try {
                Place res = values[0].clone().execute().body();
                return res.getResults();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Result> tresults) {
            ArrayList<String> photolist = new ArrayList<>();
            for (Result r : tresults) {
                photolist.add(r.getPlaceId());
            }
            dataAvailable(photolist);
        }
    }

    public void dataAvailable(ArrayList<String> data) {
        placecount = data.size();
        for (String place : data) {
            new DownloadPlaceDetails(place).execute();
        }
    }

    public void executeCurrentphotos(Call<Place> call) {
        photoreferenceslist.clear();
        counter = new AtomicInteger(0);
        progressBar.setVisibility(View.VISIBLE);
        new DownloadPlaces().execute(call);

    }
}
