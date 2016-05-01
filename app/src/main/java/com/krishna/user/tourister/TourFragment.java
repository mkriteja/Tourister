package com.krishna.user.tourister;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class TourFragment extends android.support.v4.app.Fragment {

    RecyclerView RecyclerView;
    RecyclerView.LayoutManager Layoutmanager;
    ToursAdapter mRecyclerViewAdapter;
    private RecyclerViewMaterialAdapter favmaterialpadapter;
    private ArrayList<Map<String, ?>> tplaces;

    public TourFragment() {

    }

    public static TourFragment newInstance() {
        TourFragment fragment = new TourFragment();
        Bundle args = new Bundle();
        args.putSerializable("tours", new ArrayList<Map<String, ?>>());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_tour, container, false);
        tplaces = (ArrayList<Map<String, ?>>) getArguments().getSerializable("tours");

        RecyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_tour);
        RecyclerView.setHasFixedSize(true);

        Layoutmanager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(Layoutmanager);

        mRecyclerViewAdapter = new ToursAdapter(getActivity(), tplaces);
        favmaterialpadapter = new RecyclerViewMaterialAdapter(mRecyclerViewAdapter);
        RecyclerView.setAdapter(favmaterialpadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), RecyclerView, null);

        getToursFromFirebase();

        mRecyclerViewAdapter.setOnItemClickListener(new ToursAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = position - 1;
                ImageView hero1 = (ImageView) view.findViewById(R.id.tourimage);
                Intent intent = new Intent(getActivity(), TourDetailActivity.class);
                intent.putExtra("title", tplaces.get(position).get("Name").toString());
                intent.putExtra("price", tplaces.get(position).get("price").toString());
                intent.putExtra("zoom", 15.0f);
                intent.putExtra("tcode", tplaces.get(position).get("tcode").toString());
                intent.putExtra("Description", tplaces.get(position).get("Description").toString());
                intent.putExtra("rating", tplaces.get(position).get("rating").toString());
                intent.putExtra("loc", tplaces.get(position).get("loc").toString());
                intent.putExtra("photo", R.drawable.photo1);
                intent.putExtra("Duration", tplaces.get(position).get("Duration").toString());
                AppManager.setTourPhotoCache(((BitmapDrawable) hero1.getDrawable()).getBitmap());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), hero1, "photo_hero");
                startActivity(intent, options.toBundle());
            }
        });
        return rootview;
    }

    private void getToursFromFirebase() {
        final Firebase favref = AppManager.getRef().child("Tours");
        favref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                addPlaces(snapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void addPlaces(DataSnapshot snapshot) {
        tplaces.clear();
        if (snapshot.getChildrenCount() > 0) {
            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                String name = (String) messageSnapshot.child("Name").getValue();
                String price = messageSnapshot.child("price").getValue().toString();
                String url = messageSnapshot.child("url").getValue().toString();
                String Duration = messageSnapshot.child("Duration").getValue().toString();
                String rating = messageSnapshot.child("rating").getValue().toString();
                String Description = messageSnapshot.child("Description").getValue().toString();
                String tcode = messageSnapshot.child("TourCode").getValue().toString();
                String location = messageSnapshot.child("Location").getValue().toString();
                Map<String, String> t = new HashMap<>();
                t.put("Name", name);
                t.put("price", price);
                t.put("url", url);
                t.put("Duration", Duration);
                t.put("rating", rating);
                t.put("Description", Description);
                t.put("tcode", tcode);
                t.put("loc", location);
                tplaces.add(t);
            }
            getArguments().putSerializable("tours", tplaces);
            favmaterialpadapter.notifyDataSetChanged();
        }
    }

}