package com.example.kazu.postapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.squareup.picasso.Picasso;

public class ContentFragment extends Fragment {
    ImageView imageview;
    ProgressDialog progressDialog;
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
        //Button getbutton = (Button) view.findViewById(R.id.GetButton);
        BootstrapButton refreshbutton = (BootstrapButton) view.findViewById(R.id.RefreshButton);
        BootstrapButton updatebutton = (BootstrapButton) view.findViewById(R.id.UpdateButton);
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateclick();
            }
        });
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshclick();
            }
        });
    }

    @Override
     public void onResume() {
        super.onResume();
        /*Picasso.with(getActivity())
                .load("http://n302.herokuapp.com/check").into(imageview);*/
    }


    public void updateclick() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("画像を更新しています");
        progressDialog.setMessage("しばらくお待ちください");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        System.out.println("画像表示ですぞ");
        AsyncHttpClient client = new AsyncHttpClient();
        String[] allowedContentTypes = new String[]{"image/png", "image/jpeg"};

        client.get("http://n302.herokuapp.com/check",
                new BinaryHttpResponseHandler(allowedContentTypes) {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                        imageview.setImageBitmap(bitmap);
                        Toast.makeText(getActivity(), "更新したぞ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData, Throwable error) {

                    }
                });
    }

    public void  refreshclick() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("初期化中です");
        progressDialog.setMessage("しばらくお待ちください");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        System.out.println("リフレッシュ\n");
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://n302.herokuapp.com/refresh";
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "リフレッシュ", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onFinish() {
                // POST終了後にGETを行い描画する
//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                Uri uri = Uri.parse("http://n302.herokuapp.com/check");
//                Uri.Builder builder = uri.buildUpon();
//                GetTask task = new GetTask(imageView);
//                task.execute(builder);
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
