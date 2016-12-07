package com.example.kazu.postapp;


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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by kazu on 2016/12/07.
 */
public class EditFragment extends android.support.v4.app.Fragment {
     EditText memo;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        memo = ((EditText) view.findViewById(R.id.memo));

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
        Log.d("memo", "click: "+this.memo);
        final String memo = this.memo.getText().toString();
        RequestParams params = new RequestParams();
        System.out.println(memo + " 確認だよ");
        params.put("message", memo);

        //String url = "http://192.168.11.16:9292/memo";
        String url = "http://n302.herokuapp.com/memo";

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), memo + "をpostしたよ", Toast.LENGTH_LONG).show();
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
}
