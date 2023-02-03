package com.app.instantly;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendBtn = findViewById(R.id.sendBtn);
        Button receiveBtn = findViewById(R.id.receiveBtn);
        Intent permissionActivity = new Intent(getApplicationContext(), SenderPermission.class);









        ActivityResultLauncher<Intent>  m  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                        Intent i = result.getData();
                        Uri uri = i.getData();
                        File file = new File( uri.getPath());
                        String strFileName = file.getAbsolutePath();
                        Toast.makeText(this,strFileName, Toast.LENGTH_SHORT).show();
                    }
                });

        sendBtn.setOnClickListener(view -> {
//            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
//            PopupMenu popupMenu = new PopupMenu(MainActivity.this, sendBtn);
//            popupMenu.getMenuInflater().inflate(R.menu.file_type, popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(menuItem -> {
//                switch (menuItem.getItemId()){
//                    case R.id.audio:
//                        fileIntent.setType("audio/*");
//                        try {
//                            m.launch(fileIntent);
//                        } catch (ActivityNotFoundException e) {
//                            // Handle the exception, for example, by showing a Toast message to the user
//                            Toast.makeText(this, "No app found to handle this request.", Toast.LENGTH_LONG).show();
//                        }
//                        return  true;
//                    case R.id.image:
//                        fileIntent.setType("image/*");
//                        try {
//                            m.launch(fileIntent);
//                        } catch (ActivityNotFoundException e) {
//                            // Handle the exception, for example, by showing a Toast message to the user
//                            Toast.makeText(this, "No app found to handle this request.", Toast.LENGTH_LONG).show();
//                        }
//
//                        return true;
//                    case R.id.video:
//                        fileIntent.setType("video/*");
//                        try {
//                            m.launch(fileIntent);
//                        } catch (ActivityNotFoundException e) {
//                            // Handle the exception, for example, by showing a Toast message to the user
//                            Toast.makeText(this, "No app found to handle this request.", Toast.LENGTH_LONG).show();
//                        }
//                        return true;
//                    case R.id.files:
//                        fileIntent.setType("*/*");
//                        try {
//                            m.launch(fileIntent);
//                        } catch (ActivityNotFoundException e) {
//                            // Handle the exception, for example, by showing a Toast message to the user
//                            Toast.makeText(this, "No app found to handle this request.", Toast.LENGTH_LONG).show();
//                        }
//                        return true;
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
//                }
//
//            });
//            popupMenu.show();
//
////                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
////                if(ContextCompat.checkSelfPermission(
////                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
////                        && Environment.isExternalStorageManager()
////                        && wifiManager.isWifiEnabled()) startActivity(new Intent(getApplicationContext(), CameraHandler.class));
////                else {
////                    permissionActivity.putExtra("key","SENDER");
////                    startActivity(permissionActivity);
////                }
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            try{
                startActivityForResult(Intent.createChooser(i , "Select A File"),100);
            }catch (Exception e){
                Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
            }





        });




        receiveBtn.setOnClickListener(view ->{
            permissionActivity.putExtra("key","RECEIVER");
            startActivity(new Intent(this, WifiOrHotspot.class));
        });



    }

    public void hi(){

    }

    @Override
    protected  void onActivityResult(int reqCode , int resCode ,Intent data){
        if(reqCode == 100 && resCode == RESULT_OK && data!= null){
            Uri uri = data.getData();
            String path  = uri.getPath();
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode,resCode,data);
    }
}


