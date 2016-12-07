package com.example.kazu.postapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by kazu on 2016/12/07.
 */
public class ContentFragment extends Fragment {
    ImageView imageview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageview = (ImageView)view.findViewById(R.id.imageView);
    }

    @Override
    public void onResume() {
        super.onResume();
        Picasso.with(getActivity())
                .load("http://n302.herokuapp.com/check").into(imageview);
    }
}
