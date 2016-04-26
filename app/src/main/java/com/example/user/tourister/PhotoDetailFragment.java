package com.example.user.tourister;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoDetailFragment extends Fragment {


    public PhotoDetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ImageView displayphoto= (ImageView) rootView.findViewById(R.id.currentphoto);
        displayphoto.setImageBitmap(AppManager.getSelectedPhoto());
        return rootView;
    }

}
