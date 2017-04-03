package com.example.ryne.qr_device_scanner;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import data.JSONDeviceParser;
import model.Device;

public class QRScanner extends AppCompatActivity {
    private Toolbar toolBar;
    private Button btScan;
    private ImageView imQrSCanner;
    private ImageView imInventory;
    private ImageView imWareHouse;
    private JSONDeviceParser jsonDeviceParser = new JSONDeviceParser();
    private Device device = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // this.startActivity(new Intent(this,Inventory.class));
        initToolBar();
        final Activity activity = this;
        imQrSCanner = (ImageView)findViewById(R.id.qrScanner);
        imInventory = (ImageView)findViewById(R.id.inventory);
        imWareHouse = (ImageView)findViewById(R.id.wareHouse);
        imQrSCanner.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                 intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                 intentIntegrator.setPrompt("Scanning");
                 intentIntegrator.setCameraId(0);
                 intentIntegrator.setBeepEnabled(true);
                 intentIntegrator.setBarcodeImageEnabled(false);
                 intentIntegrator.initiateScan();
             }
         });
        imInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRScanner.this, Inventory.class);
                startActivity(intent);
            }
        });
        imWareHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRScanner.this, WareHouse.class);
                startActivity(intent);
            }
        });

    }
    public void initToolBar(){
        toolBar = (Toolbar) findViewById(R.id.toolBarQRSCanner);
        toolBar.setNavigationIcon(R.drawable.qrcode);
        toolBar.setTitle("Device Scanner");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                mediaPlayer.start();
                DataTask dataTask = new DataTask();
                dataTask.execute("device_informations/2017D001CNTT1");
            }else{
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                mediaPlayer.start();
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                DataTask dataTask = new DataTask();
                dataTask.execute(result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private class DataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = HttpHandler.makeServiceCall(params[0]);
            return data;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Toast.makeText(QRScanner.this,jsonString,Toast.LENGTH_LONG).show();
            //create device object
            device = jsonDeviceParser.getDeviceData(jsonString);
            //pass to DeviceInformation activity
            Intent intent = new Intent(QRScanner.this, DeviceInformation.class);
            intent.putExtra("objDevice", device);
            startActivity(intent);
        }
    }

}
