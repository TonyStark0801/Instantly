package com.app.instantly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;


public class SenderPermission extends AppCompatActivity {
    Button location;
    TextView locationReasonTxt;

    Button wifi;
    TextView wifiReasonTxt;

    Button file;
    TextView fileReasonTxt;
    WifiManager wifiManager;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                && wifiManager.isWifiEnabled())
        {
            startActivity(new Intent(getApplicationContext(), CameraHandler.class));
        }


        location = findViewById(R.id.locationBtn);
        locationReasonTxt = findViewById(R.id.locationReasonTxt);

        wifi = findViewById(R.id.wifiBtn);
        wifiReasonTxt = findViewById(R.id.wifiReasonTxt);

        file = findViewById(R.id.FileBtn);
        fileReasonTxt = findViewById(R.id.fileReasontxt);

        final int  LOCATION_REQUEST_CODE = 1;
        final int  FILE_REQUEST_CODE = 2;
        final int  WIFI_REQUEST_CODE = 3;



        //Initially Invisible
        setInvisible(LOCATION_REQUEST_CODE);
        setInvisible(FILE_REQUEST_CODE);
        setInvisible(WIFI_REQUEST_CODE);

        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.Cancel,null)
                    .show();
        }



        /* Location SenderPermission */
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            location.setVisibility(View.VISIBLE);
            locationReasonTxt.setVisibility(View.VISIBLE);
            location.setOnClickListener(v -> {









                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this).setTitle("SenderPermission Needed").setMessage("Storage SenderPermission is need to look for the files, so you can select it.\",LOCATION_PERMISSION_CODE );\n")
                            .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE))
                            .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                            .create().show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                }
            });
        }


        /*File SenderPermission*/
        if( !Environment.isExternalStorageManager() ){
            setVisible(2);
            file.setOnClickListener(v->{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                }

            });
        }


        /*Wifi SenderPermission*/
        if(!wifiManager.isWifiEnabled()){
            setVisible(WIFI_REQUEST_CODE);
            wifi.setOnClickListener(v->{
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panelIntent);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onResume() {
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                && wifiManager.isWifiEnabled())
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                if (Objects.equals(value, "SENDER"))  startActivity(new Intent(getApplicationContext(), CameraHandler.class));
                else startActivity(new Intent(getApplicationContext(), WifiOrHotspot.class));
            }
            finish();
        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) setInvisible(1); else setVisible(1);
        if(Environment.isExternalStorageManager())  setInvisible(2); else setVisible(2);
        if(wifiManager.isWifiEnabled()) setInvisible(3); else setVisible(3);
        super.onResume();
    }

    private void setInvisible(int code){
        switch (code){
            case  1 :
                location.setVisibility(View.GONE);
                locationReasonTxt.setVisibility(View.GONE);
            case 2 :
                file.setVisibility(View.GONE);
                fileReasonTxt.setVisibility(View.GONE);
            case 3 :
                wifi.setVisibility(View.GONE);
                wifiReasonTxt.setVisibility(View.GONE);
        }
    }
    private void setVisible(int code){
        switch (code){
            case  1 :
                location.setVisibility(View.VISIBLE);
                locationReasonTxt.setVisibility(View.VISIBLE);
            case 2 :
                file.setVisibility(View.VISIBLE);
                fileReasonTxt.setVisibility(View.VISIBLE);
            case 3 :
                wifi.setVisibility(View.VISIBLE);
                wifiReasonTxt.setVisibility(View.VISIBLE);
        }
    }


}