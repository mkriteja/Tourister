package com.example.user.tourister;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPager;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {


    private RecyclerView photorecyclerview;
    private PhotoAdapter photoAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerViewMaterialAdapter photomaterialpadapter;
    private List<String> photoreferenceslist;
    private AtomicInteger counter = new AtomicInteger(0);
    private int placecount;
    OnPhotoInteractionListener mListener;

    public PhotoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        photorecyclerview = (RecyclerView) rootView.findViewById(R.id.photorecycler);
        photorecyclerview.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        photorecyclerview.setLayoutManager(gridLayoutManager);
        photoreferenceslist = Collections.synchronizedList(new ArrayList<String>());
        photoAdapter = new PhotoAdapter(getActivity(), photoreferenceslist);
        photomaterialpadapter = new RecyclerViewMaterialAdapter(photoAdapter);
        photorecyclerview.setAdapter(photomaterialpadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), photorecyclerview, null);

        photoAdapter.setOnItemClickListener(new PhotoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.onPhotoInteraction(position,view);
            }
        });
        return rootView;
    }

    public void dataAvailable(ArrayList<String> data) {
        placecount = data.size();
        for (String place : data) {
            new DownloadPlaceDetails(place).execute();
        }
    }

    private void setUpPhotos() {
        photomaterialpadapter.notifyDataSetChanged();
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
            for (Photo p : tresult.getPhotos()) {
                photoreferenceslist.add(p.getPhotoReference());
            }
            if (counter.addAndGet(1) == placecount) {
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
}
