package com.app.instantly;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Sender extends AppCompatActivity {
    ProgressBar progressBar;
    TextView Percent;
    String IP ="";
    String PORT = "";
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP,serverPort;
    TextView connectionStatus;
    TextView Message;
    ImageView cancel;
    Button btnSelect;
    Socket socket=null;

    byte[] bytes;
    String FileName=null;
    InputStream inputStream = null;
    long size = 0;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        serverIP = findViewById(R.id.ServerIP);
        serverPort = findViewById(R.id.ServerPort);
        connectionStatus = findViewById(R.id.ConnectionStatus);
        Message = findViewById(R.id.OutputMessage);
        cancel = findViewById(R.id.cancel_button);
        btnSelect = findViewById(R.id.btnSelectFile);
        progressBar = findViewById(R.id.progressBar);
//        Percent = findViewById(R.id.percent);
//        Percent.setVisibility(View.GONE);
        Message.setText("");

        //Initially Invisible
        setProgressBarInvisible();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String[] TOKENS = extras.getStringArray("key");
            IP+=TOKENS[1];
            PORT+=TOKENS[2];
            Thread1 = new Thread(new Thread1());
            Thread1.start();
        }

        serverIP.setText("Server IP: "+IP);
        serverPort.setText("PORT: "+PORT);
        connectionStatus.setText(R.string.NotConnected);

        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        //Initialize file manager to Select files
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

        cancel.setOnClickListener(v->{

        });

    }


    //Getting URI from File manager
    protected  void onActivityResult(int reqCode , int resCode ,Intent data){
        if(reqCode == 100 && resCode == RESULT_OK && data!= null) {
            Uri uri = data.getData();
            FileName = FileActivity.getFileName(this, uri);
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
                size = fileDescriptor.getLength();
//                Log.d("File size: ", String.valueOf(size));
//                Toast.makeText(this, String.valueOf(size), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fileDescriptor != null) {
                        fileDescriptor.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                inputStream = this.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (FileName!=null) {
                try{
                    Thread3 = new Thread(new Thread3(FileName,size,inputStream));
                    Thread3.start();
                }catch (Exception e){
                }
            }
            else {
                Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, FileName, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode,resCode,data);
    }


    //Connect the Sender device to Receiver's hotspot
//    private void connectWifi(String SSID, String PASSWORD) {
//        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
//        builder.setSsid(String.valueOf(SSID));
//        builder.setWpa2Passphrase(String.valueOf(PASSWORD));
//        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
//
//        NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder();
//        requestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
//        requestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
//        NetworkRequest request = requestBuilder.build();
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
//            @Override
//            public void onAvailable(Network network) {
//                super.onAvailable(network);
//                Log.d("HotSpot", "Connected to hotspot " + SSID);
//                Toast.makeText(Sender.this, "Connected to Hotspot "+SSID, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUnavailable() {
//                super.onUnavailable();
//                Log.d("HotSpot", "Failed to connect to hotspot " + SSID);
//                Toast.makeText(Sender.this, "Failed to connect to hotspot "+SSID, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }



    //Input and output Stream
    protected DataOutputStream dataOS;
    protected  DataInputStream dataIS;


    //Connecting to Server/Receiver Socket
    class Thread1 implements Runnable {

        public void run() {
            try {
                socket = new Socket(IP, Integer.parseInt(PORT));
                dataOS = new DataOutputStream(socket.getOutputStream());
                dataIS = new DataInputStream(socket.getInputStream());
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));
                Thread2 = new Thread(new Thread2());
                Thread2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    /*Receiving Files*/
    class Thread2 implements Runnable {
        @Override
        public void run() {
            int lod =1;
            while (true) {
                try {
                    String fileName = dataIS.readUTF();
                    long fileSize = dataIS.readLong();

                    Log.d("Dost",fileName);

                    if(fileName !=null){
                        try {
                            runOnUiThread(() -> Message.append("Sender: " + fileName + "\n"));
                        } catch (Exception e) {
                            Toast.makeText(Sender.this, "Cannot Write the file name", Toast.LENGTH_SHORT).show();
                        }
                        byte[] buffer = new byte[1024];

                        long bytesReceived = 0;
                        long totalBytes =inputStream.available();


                        runOnUiThread(() -> {
                            progressBar.setMax((int) totalBytes);
                            progressBar.setProgress(0);
                        });


                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), fileName);
                        if (file.exists()) {
                            int i = 1;
                            String newFileName;
                            while (file.exists()) {
                                newFileName = i + "_" + fileName;
                                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), newFileName);
                                i++;
                            }
                        }


                        try (FileOutputStream fos = new FileOutputStream(file,false)) {
                            int len = 0;
                            while (fileSize > 0 && (len = dataIS.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1){
                                fos.write(buffer,0,len);
                                fileSize -= len;
                                bytesReceived += len;
                                runOnUiThread(Sender.this::setProgressBarVisible);
                                long finalBytesReceived = bytesReceived;
//                                String percent = (int) (finalBytesReceived / totalBytes) * 100 +"%";
                                runOnUiThread(() -> {
//                                    Percent.setText(percent);
                                    progressBar.setProgress((int) finalBytesReceived);
                                });

                                Log.d("Testing", "running    "+len);
                            }
                            Log.d("Done", "Stopped");
                            fos.flush();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } catch (IOException e) {
                    //
                }

            }
        }
    }

    //Sending File Thread
    class Thread3 implements Runnable {
        private  String fileName;
        private InputStream inputStream;
        private  long size;
        Thread3(String fileName, long size,InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
            this.size = size;
        }

        @Override
        public void run() {
            try {
                dataOS.writeUTF(fileName);
                dataOS.writeLong(size);
                Log.d("d",fileName);
                byte[] buffer = new byte[1024];

                try {
                    int len;
                    long totalBytes = 0;
                    long bytesSent = 0;
                    totalBytes =inputStream.available();


                    long finalTotalBytes = totalBytes;

                    runOnUiThread(() -> {
                        progressBar.setMax((int) finalTotalBytes);
                        progressBar.setProgress(0);
                    });
                    runOnUiThread(() -> Message.append("Sender: " + fileName + "\n"));
                    while (size>0 && (len = inputStream.read(buffer,0,(int) Math.min(buffer.length,size))) > 0) {
                        dataOS.write(buffer, 0, len);
                        bytesSent += len;
                        size-=len;

                        // Update the progress bar during the file transfer
                        long finalBytesSent = bytesSent;
                        runOnUiThread(Sender.this::setProgressBarVisible);
//                        String percent = (finalBytesSent / totalBytes) * 100 +"%";
//                        runOnUiThread(()->{
//                            Percent.setText(percent);
//                        });
                        runOnUiThread(() -> {
                            progressBar.setProgress((int) finalBytesSent);
                        });

                    }
                    dataOS.flush();
                }catch (IOException e) {
                    Toast.makeText(Sender.this, "Can't Send File. Try Again", Toast.LENGTH_SHORT).show();
                }
                finally {
                    inputStream.close();
                }

                runOnUiThread(Sender.this::setProgressBarInvisible);

            } catch (IOException e) {
                Toast.makeText(Sender.this, "Can't Send file. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public  void setProgressBarInvisible(){
        progressBar.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
//        Percent.setVisibility(View.GONE);
    }
    public  void  setProgressBarVisible(){
        progressBar.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
//        Percent.setVisibility(View.VISIBLE);
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