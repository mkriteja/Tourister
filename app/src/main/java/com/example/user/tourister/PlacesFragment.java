package com.example.user.tourister;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import org.json.JSONObject;

import java.util.HashMap;

import DataModel.com.example.user.tourister.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private MovieData movieData;
    private int scrollpos=0;
    private RecyclerViewMaterialAdapter tempadapter;
    private static String API_KEY ="AIzaSyA7oAvJwhU-bgedvLqyS1TWdzlBS83nO5Q";

    public PlacesFragment() {

    }
    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args =new Bundle();
        args.putSerializable("movieData",new MovieData());
        fragment.setArguments(args);
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
        movieData = (MovieData)getArguments().getSerializable("movieData");
        for (int i=0;i<movieData.getSize();i++) {
            movieData.getItem(i).put("SelectStatus",false);
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        new DownloadPlaces().execute();
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getActivity(),movieData.getMoviesList());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlacesInterface service = retrofit.create(PlacesInterface.class);
        Call<Place> call = service.getPlaces("Museums in New york",API_KEY);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                int statusCode = response.code();
                Place user = response.body();
                Log.d("test",user.getResults().toString());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                // Log error here since request failed
            }
        });

        mAdapter.setOnItemClickListener(new MyAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getArguments().putSerializable("movieData",movieData);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                movieData.getMoviesList().add(position+1,new HashMap<>(movieData.getItem(position)));
                mAdapter.notifyItemInserted(position+1);
            }

            @Override
            public void onChangeClick(boolean ischecked,int position){
                if(ischecked)
                    movieData.getItem(position).put("SelectStatus",true);
                else
                    movieData.getItem(position).put("SelectStatus",false);
            }

        });

        tempadapter = new RecyclerViewMaterialAdapter(mAdapter);
        recyclerView.setAdapter(tempadapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
        return rootView;
    }

    private class DownloadPlaces extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... values) {


            return 0;
        }

        protected void onPostExecute(Integer res) {

        }
    }
}
