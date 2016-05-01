package com.krishna.user.tourister;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PhotoDetailFragment extends Fragment {


    private ImageView imageView;
    public PhotoDetailFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        imageView =(ImageView) rootView.findViewById(R.id.currphoto);
        imageView.setTransitionName(AppManager.getTransitionName());
        imageView.setImageBitmap(AppManager.getSelectedPhoto());
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.photomenu, menu);
        if(menu.findItem(R.id.share)==null){
            inflater.inflate(R.menu.photomenu, menu);
        }
        MenuItem share = menu.findItem(R.id.share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("image/*");
        File media = saveFile(getContext());
        intentShare.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(media));
        shareActionProvider.setShareIntent(intentShare);

        super.onCreateOptionsMenu(menu,inflater);
    }

    public static File saveFile(Context context){
        String baseFolder;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            baseFolder = context.getExternalFilesDir(null).getAbsolutePath();
        }
        else {
            baseFolder = context.getFilesDir().getAbsolutePath();
        }
        File file = new File(baseFolder + "shareimage");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            AppManager.getSelectedPhoto().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }
        return file;

    }
}
