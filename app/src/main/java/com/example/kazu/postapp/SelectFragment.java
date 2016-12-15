package com.example.kazu.postapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.io.File;
import java.io.InputStream;

public class SelectFragment extends Fragment {
    private static final int REQUEST_GALLERY = 0;
    ImageView imageview = null;
    Bitmap img;
    BootstrapButton selectbutton;
    BootstrapButton imagebutton;
    Button blebutton;
    String imagePath = null;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
		TextView tv;

		private int service_id;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
		private boolean isBleSupported = false;
		private BluetoothAdapter.LeScanCallback leScanCallback;
		private BluetoothLeScanner bluetoothLeScanner;
		private ScanCallback scanCallback;
		private final String RASPI_UUID = "9E205570-1407-442A-A9BE-0E3AA7420A7A";
		// BLEスキャンのタイムアウト時間
		private static final long SCAN_TIME_MS = 10000;
		private android.os.Handler handler = new Handler();
		private boolean isGetServiceUUID = false;
		private boolean isBleScanning = false;



		//Bitmap bmp;
		@Nullable
		@Override
    TextView tv;

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
        blebutton = (Button) view.findViewById(R.id.bleButton);

        //確認用です
        tv = (TextView) view.findViewById(R.id.textView);

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
        blebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleScan();
            }
        });

				//BLEを使うための準備
				bluetoothManager = (BluetoothManager) .getSystemService(Context.BLUETOOTH_SERVICE);
				bluetoothAdapter = bluetoothManager.getAdapter();
				isBleSupported = (BluetoothAdapter != null);

				if(isBleSupported){
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
						initScanCallback();
					} else {
						initLeScanCallback();
					}
				} else {
					Toast.makeText(getApplicationContext(), "This device is not support BLE.", Toast.LENGTH_SHORT).show();
				}

				// 6.0以降はコメントアウトした処理をしないと初回はパーミッションがOFFになっています。
        // requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
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
                img = BitmapFactory.decodeStream(in);
                in.close();
                // 選択した画像を表示
                imageview.setImageBitmap(img);
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
        if (imagePath == null) {
            builder.create().show();
            return;
        }

        File myFile = new File(imagePath);
        if(myFile.exists()) System.out.println("あるよ");
        //ファイル名を表示
        System.out.println("ファイル名：" + myFile.getName());
        //ファイルの格納ディレクトリ名を表示
        System.out.println("格納ディレクトリ名：" + myFile.getParent());
        //ファイルのPATH名を表示
        System.out.println("PATH名：" + myFile.getAbsolutePath());
        System.out.println("－－－－－－－－－－－－－－－－");
        System.out.println(imagePath);

        //処理中のダイアログ表示
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("画像データを送信しています");
        progressDialog.setMessage("しばらくお待ちください");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //String url = "http://n302.herokuapp.com/tmp";
        String url = "http://n302.herokuapp.com/img_save";

            AsyncHttpClient client = new AsyncHttpClient();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    //BLEの実装部分です
		public int bleScan() {
			tv.setText("BLE通信開始ボタンが押されました");
			if (bluetoothAdapter.isEnabled()) {
				isGetServiceUUID = false;
				startBLEScan();
				if (isGetServiceUUID){
					return service_id;
				} else {
					return 0;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Please enable to Bluetooth function.", Toast.LENGTH_SHORT).show();
				return -1;
			}
		}

		private void initScanCallback() {
			scanCallback = new ScanCallback() {
				@Override
				public void onBatchScanResults(List<ScanResult> results) {
					super.onBatchScanResults(results);
					for (ScanResult result : results) {
						scanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
					}
				}

				@Override
				public void onScanResult(int callbackType, ScanResult result) {
					super.onScanResult(callbackType, result);
					scanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
				}

				@Override
				public void onScanFailed(int errorCode) {
					super.onScanFailed(errorCode);
				}
			};
		}

		private void initLeScanCallback() {
			leScanCallback = new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
					scanResult(device, rssi, scanRecord);
				}
			};
		}

		@SuppressWarnings("deprecation")
		private void startBLEScan() {
			if (!isBleScanning) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(isBleScanning){
							Toast.makeText(getApplicationContext(), "Time out BLE scan.", Toast.LENGTH_SHORT).show();
							stopBLEScan();
							isBleScanning = false;
						}
					}
				}, SCAN_TIME_MS);
				isBleScanning = true;
				judgeStartBLEScan();
			}
		}

		@SuppressWarnings("deprecation")
		private void judgeStartBLEScan() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				bluetoothLeScanner.startScan(scanCallback);
			} else {
				bluetoothAdapter.startLeScan(leScanCallback);
			}
			isBleScanning = true;
		}

		@SuppressWarnings("deprecation")
		private void stopBLEScan() {
			if(isBleScanning) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					bluetoothLeScanner.stopScan(scanCallback);
				} else {
					bluetoothAdapter.stopLeScan(leScanCallback);
				}
			}
			isBleScanning = false;
		}

		private void scanResult(BluetoothDevice device, int rssi, final byte[] scanRecord) {
			if(scanRecord.length > 30) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String uuid  = getUUID(scanRecord);
						String major = getMajor(scanRecord);
						// スキャンしたパケットのUUIDが指定したものであればグローバル変数に格納してスキャン終了
						if(uuid.equals(RASPI_UUID)) {
							service_id = Integer.parseInt(major);
							isGetServiceUUID = true;
							Toast.makeText(getApplicationContext(), "Get RASPI device UUID.\nFinish scan.", Toast.LENGTH_SHORT).show();
							stopBLEScan();
							isBleScanning = false;
							Toast.makeText(getApplicationContext(), "Finish BLE scan successful.", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "Get Unknown device UUID.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}

		private String getUUID(byte[] scanRecord) {
			String uuid = IntToHex2(scanRecord[9] & 0xff)
				+ IntToHex2(scanRecord[10] & 0xff)
				+ IntToHex2(scanRecord[11] & 0xff)
				+ IntToHex2(scanRecord[12] & 0xff)
				+ "-"
				+ IntToHex2(scanRecord[13] & 0xff)
				+ IntToHex2(scanRecord[14] & 0xff)
				+ "-"
				+ IntToHex2(scanRecord[15] & 0xff)
				+ IntToHex2(scanRecord[16] & 0xff)
				+ "-"
				+ IntToHex2(scanRecord[17] & 0xff)
				+ IntToHex2(scanRecord[18] & 0xff)
				+ "-"
				+ IntToHex2(scanRecord[19] & 0xff)
				+ IntToHex2(scanRecord[20] & 0xff)
				+ IntToHex2(scanRecord[21] & 0xff)
				+ IntToHex2(scanRecord[22] & 0xff)
				+ IntToHex2(scanRecord[23] & 0xff)
				+ IntToHex2(scanRecord[24] & 0xff);
			return uuid;
		}

		private String getMajor(byte[] scanRecord) {
			String hexMajor = IntToHex2(scanRecord[25] & 0xff) + IntToHex2(scanRecord[26] & 0xff);
			return String.valueOf(Integer.parseInt(hexMajor, 16));
		}

		// 16進2桁に変換
		@SuppressLint("DefaultLocale")
		private String IntToHex2(int i) {
			char hex_2[]     = { Character.forDigit((i >> 4) & 0x0f, 16), Character.forDigit(i & 0x0f, 16)  };
			String hex_2_str = new String(hex_2);
			return hex_2_str.toUpperCase();
		}
    public int bleScan() {
        tv.setText("BLE通信開始ボタンが押されました");
        return 1;
    }
}
