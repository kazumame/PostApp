package com.example.kazu.postapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.entity.mime.content.FileBody;

public class SelectFragment extends Fragment {
    private static final int REQUEST_GALLERY = 0;
    ImageView imageview = null;
    BootstrapButton selectbutton;
    BootstrapButton imagebutton;
    String imagePath = null;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    //Bitmap bmp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  AlertDialog.Builder クラスのインスタンスを生成
        builder = new AlertDialog.Builder(getActivity());
        //  ダイアログタイトル、表示メッセージ、ボタンを設定
        builder.setTitle(R.string.dlg_title);
        builder.setMessage(R.string.dlg_msg);

        imageview = (ImageView) view.findViewById(R.id.selectImageView);
        selectbutton = (BootstrapButton) view.findViewById(R.id.selectButton);
        imagebutton = (BootstrapButton) view.findViewById(R.id.imageButton);
        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postGallery();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if(requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                // ContentResolver経由でファイルパスを取得
                ContentResolver cr = getActivity().getContentResolver();
                String[] columns = {
                        MediaStore.Images.Media.DATA
                };
                Cursor c = cr.query(data.getData(), columns, null, null, null);
                int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                c.moveToFirst();
                imagePath = c.getString(column_index);
                Log.v("test", "path=" + imagePath);
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 選択した画像を表示
                //imageview.setImageBitmap(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);

    }

    public void postGallery() {
        if(imagePath == null) {
            builder.create().show();
            return;
        }
        File myFile = new File(imagePath);
        AsyncHttpClient client = new AsyncHttpClient();
        /* if(myFile.exists()) System.out.println("あるよ");*/
        //ファイル名を表示
        System.out.println("ファイル名：" + myFile.getName());
        //ファイルの格納ディレクトリ名を表示
        System.out.println("格納ディレクトリ名：" + myFile.getParent());
        //ファイルのPATH名を表示
        System.out.println("PATH名：" + myFile.getAbsolutePath());
        System.out.println("－－－－－－－－－－－－－－－－");

        System.out.println(imagePath);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("画像データを送信しています");
        progressDialog.setMessage("しばらくお待ちください");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        //String url = "http://n302.herokuapp.com/tmp";
        String url = "http://n302.herokuapp.com/img_save";

        try {
            RequestParams params = new RequestParams("img", myFile);
            client.post(url, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    Toast.makeText(getActivity(), "画像をpostしたよ", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    //progressDialog.dismiss();
                    System.out.println("POSTできてねーぞ");
                }

                @Override
                public void onFinish() {
                }


                @Override
                public void onRetry(int retryNo) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
