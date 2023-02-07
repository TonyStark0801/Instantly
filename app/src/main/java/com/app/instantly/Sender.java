package com.app.instantly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.util.Objects;

public class Sender extends AppCompatActivity {
    String IP ="";
    String PORT = "";
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP,serverPort;
    TextView connectionStatus;
    TextView OutMessage,InputMessage;
    Button btnSend;
    Button btnSelect;
    byte[] bytes;
    String FileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        serverIP = findViewById(R.id.ServerIP);
        serverPort = findViewById(R.id.ServerPort);
        connectionStatus = findViewById(R.id.ConnectionStatus);
        OutMessage = findViewById(R.id.OutputMessage);
        InputMessage = findViewById(R.id.InputMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSelect = findViewById(R.id.btnSelectFile);
        OutMessage.setText("");
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String[] TOKENS = extras.getStringArray("key");
            if(Objects.equals(TOKENS[0], "HOTSPOT")) {
                connectWifi(TOKENS[1],TOKENS[2]);
                IP+=TOKENS[3];
                PORT+=TOKENS[4];
            }
            else {
                IP+=TOKENS[1];
                PORT+=TOKENS[2];
            }
            Thread1 = new Thread(new Thread1());
            Thread1.start();
        }

        serverIP.setText("Server IP: "+IP);
        serverPort.setText("PORT: "+PORT);
        connectionStatus.setText(R.string.NotConnected);

        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        btnSelect.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(Sender.this, btnSelect);
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

        });
        btnSend.setOnClickListener(v -> {

            if (!FileName.isEmpty()) {
                Thread3 = new Thread(new Thread3(FileName,bytes));
                Thread3.start();
            }
        });
    }


    protected  void onActivityResult(int reqCode , int resCode ,Intent data){
        if(reqCode == 100 && resCode == RESULT_OK && data!= null){
            Uri uri = data.getData();
            FileName = FileActivity.getFileName(this,uri);
            try {
                bytes = FileActivity.getBytes(this, uri);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(this, FileName, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode,resCode,data);
    }





    //Connect the Sender device to Receiver's hotspot
    private void connectWifi(String SSID, String PASSWORD) {
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(String.valueOf(SSID));
        builder.setWpa2Passphrase(String.valueOf(PASSWORD));
        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

        NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder();
        requestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        requestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
        NetworkRequest request = requestBuilder.build();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.d("HotSpot", "Connected to hotspot " + SSID);
                Toast.makeText(Sender.this, "Connected to Hotspot "+SSID, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d("HotSpot", "Failed to connect to hotspot " + SSID);
                Toast.makeText(Sender.this, "Failed to connect to hotspot "+SSID, Toast.LENGTH_SHORT).show();
            }
        });
    }



    protected InputStream is;
    protected  OutputStream os;
    protected DataOutputStream dataOS;
    protected  DataInputStream dataIS;


    //Connected
    class Thread1 implements Runnable {
        Socket socket=null;
        public void run() {
            try {
                socket = new Socket(IP, Integer.parseInt(PORT));
                os = socket.getOutputStream();
                is = socket.getInputStream();
                dataOS = new DataOutputStream(os);
                dataIS = new DataInputStream(is);
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));
                Thread2 = new Thread(new Thread2());
                Thread2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String fileName =  dataIS.readUTF();
                    if (fileName!= null) {
                        runOnUiThread(() -> OutMessage.append("Receiver: " + fileName + "\n"));
                    } else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    class Thread3 implements Runnable {
        private String fileName;
        private byte[] bytes;
        Thread3(String fileName, byte[]bytes) {
            this.fileName = fileName;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            try {
                dataOS.writeUTF(fileName);
                dataOS.writeInt(bytes.length);
                dataOS.write(bytes);

                dataOS.flush();
                runOnUiThread(() -> {
                    OutMessage.append("Sender: " + fileName + "\n");
                    InputMessage.setText("");
                });


            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }


    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    public void onDestroy() {
        super.onDestroy();
        try {

            if (dataIS != null) {
                dataIS.close();
            }
            if (dataOS != null) {
                dataOS.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}