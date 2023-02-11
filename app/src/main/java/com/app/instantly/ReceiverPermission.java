package com.app.instantly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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


public class ReceiverPermission extends AppCompatActivity {
    Button location;
    TextView locationReasonTxt;

    Button locationService;
    TextView locationServiceTxt;

    Button file;
    TextView fileReasonTxt;
    LocationManager lm ;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    Intent i;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_permission);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}



        i = new Intent(getApplicationContext(),WifiOrHotspot.class);

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                && (gps_enabled && network_enabled))
        {
            startActivity(i);
        }


        location = findViewById(R.id.locationBtn);
        locationReasonTxt = findViewById(R.id.locationReasonTxt);

        locationService = findViewById(R.id.locationService);
        locationServiceTxt = findViewById(R.id.locationServiceReason);

        file = findViewById(R.id.FileBtn);
        fileReasonTxt = findViewById(R.id.fileReasontxt);

        final int  LOCATION_REQUEST_CODE = 1;
        final int  FILE_REQUEST_CODE = 2;
        final int  LOCATION_SERVICE_REQUEST_CODE = 3;



        //Initially Invisible
        setInvisible(LOCATION_REQUEST_CODE);
        setInvisible(FILE_REQUEST_CODE);
        setInvisible(LOCATION_SERVICE_REQUEST_CODE);




        /* Location SenderPermission */
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            location.setVisibility(View.VISIBLE);
            locationReasonTxt.setVisibility(View.VISIBLE);
            location.setOnClickListener(v -> {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this).setTitle("SenderPermission Needed").setMessage("Storage SenderPermission is need to look for the files, so you can select it.\",LOCATION_PERMISSION_CODE );\n")
                            .setPositiveButton("OK", (dialog, which) -> {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", ((dialog, which) -> {
                                dialog.dismiss();
                            }))
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
        /*Location Service check*/
        if(!gps_enabled && !network_enabled) {
            setVisible(3);
            locationService.setOnClickListener(v->{
                this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            });
        }

        }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onResume() {
        LocationManager lm  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                &&(gps_enabled && network_enabled))
        {
            startActivity(new Intent(getApplicationContext(),WifiOrHotspot.class));
            finish();
        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) setInvisible(1); else setVisible(1);
        if(Environment.isExternalStorageManager())  setInvisible(2); else setVisible(2);
        if(gps_enabled && network_enabled) setInvisible(3); else setVisible(3);
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
                locationService.setVisibility(View.GONE);
                locationServiceTxt.setVisibility(View.GONE);
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
                locationService.setVisibility(View.VISIBLE);
                locationServiceTxt.setVisibility(View.VISIBLE);
        }
    }


}