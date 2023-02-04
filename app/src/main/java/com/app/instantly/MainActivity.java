package com.app.instantly;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button sendBtn = findViewById(R.id.sendBtn);
        Button receiveBtn = findViewById(R.id.receiveBtn);
        Intent permissionActivity = new Intent(getApplicationContext(), SenderPermission.class);


        sendBtn.setOnClickListener(view -> {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, sendBtn);
            popupMenu.getMenuInflater().inflate(R.menu.file_type, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.audio:
                        fileIntent.setType("audio/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return  true;
                    case R.id.image:
                        fileIntent.setType("image/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    case R.id.video:
                        fileIntent.setType("video/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.files:
                        fileIntent.setType("*/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }

            });
            popupMenu.show();
//                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if(ContextCompat.checkSelfPermission(
//                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
//                        && Environment.isExternalStorageManager()
//                        && wifiManager.isWifiEnabled()) startActivity(new Intent(getApplicationContext(), CameraHandler.class));
//                else {
//                    permissionActivity.putExtra("key","SENDER");
//                    startActivity(permissionActivity);
//                }


        });



        receiveBtn.setOnClickListener(view ->{
            permissionActivity.putExtra("key","RECEIVER");
            startActivity(new Intent(this, WifiOrHotspot.class));
        });
    }

    @Override
    protected  void onActivityResult(int reqCode , int resCode ,Intent data){
        if(reqCode == 100 && resCode == RESULT_OK && data!= null){
            Uri uri = data.getData();
            Toast.makeText(this, getFileName(uri), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode,resCode,data);
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}


