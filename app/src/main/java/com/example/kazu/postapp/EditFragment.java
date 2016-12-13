package com.example.kazu.postapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class EditFragment extends android.support.v4.app.Fragment {
    BootstrapEditText memo;
    Spinner spinner;
    Spinner spinner2;
    Spinner spinner3;
    Spinner spinner4;
    ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        memo = ((BootstrapEditText) view.findViewById(R.id.memo));
        memo.setTextSize(28.0f); //文字のフォントサイズ指定
        spinner = (Spinner) view.findViewById(R.id.spinner); //フォントの種類
        spinner2 = (Spinner) view.findViewById(R.id.spinner2); //フォントの色
        spinner3 = (Spinner) view.findViewById(R.id.spinner3); //場所の指定
        spinner4 = (Spinner) view.findViewById(R.id.spinner4); //ポストイットのカラー

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click();
            }
        });
        Log.d("editor", "created: "+memo);
    }

    public void click(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("データを送信しています");
        progressDialog.setMessage("しばらくお待ちください");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //ProgressDialog progress = new ProgressDialog(this);
        //progress.setTitle("Get page");
        params.put("message", getMemo());
        params.put("font", getSpinner(spinner));
        params.put("color", getSpinner(spinner2));
        params.put("position",getSpinner(spinner3));
        params.put("pcolor", getSpinner(spinner4));

        //String url = "http://192.168.11.16:9292/memo";
        //String url = "http://n302.herokuapp.com/memo";
        String url1 = "http://n302.herokuapp.com/maker";


        client.post(url1, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "postしたよ", Toast.LENGTH_LONG).show();
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

    public String getMemo(){
        Log.d("editor", "memo"+memo);
        return memo.getText().toString();
    }

    public String getSpinner(Spinner spinner){
        return (String)spinner.getSelectedItem();
    }
}
