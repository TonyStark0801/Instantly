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
import android.widget.Button;
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
    String IP ="";
    String PORT = "";
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP,serverPort;
    TextView connectionStatus;
    TextView Message;
    Button btnSend;
    Button btnSelect;
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
        btnSend = findViewById(R.id.btnSend);
        btnSelect = findViewById(R.id.btnSelectFile);
        Message.setText("");

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

        //Sending File Btn Functionality
        btnSend.setOnClickListener(v -> {

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
                    throw e;
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
        Socket socket=null;
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

    //Receiving Files Thread
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String fileName = dataIS.readUTF();

                    if (fileName != null) {
                        byte[] buffer = new byte[1024];
                        Log.d("d",fileName);
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), fileName);
                        if(!file.exists()){
                            try{
                                boolean created = file.createNewFile();
                                if(!created) {
                                    throw new IOException("Could not create file: " + fileName);
                                }
                            }
                            catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(() -> Message.append("Receiver: " + fileName + "\n"));
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            int len;
                            while ((len = dataIS.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
//                                Log.d("Received Data Size", String.valueOf(len));
//                                Log.d("G", String.valueOf(fos.getChannel().size()));

                            }
                            fos.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Thread1 = new Thread(new Sender.Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                   e.printStackTrace();
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
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setMax((int) finalTotalBytes);
                        progressBar.setProgress(0);
                    });
                    while ((len = inputStream.read(buffer)) > 0) {
                        dataOS.write(buffer, 0, len);
                        bytesSent += len;

                        // Update the progress bar during the file transfer
                        long finalBytesSent = bytesSent;
                        runOnUiThread(() -> {
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setProgress((int) finalBytesSent);
                        });
                        Log.d("G", String.valueOf(dataOS.size()));
                    }
                    dataOS.flush();
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    inputStream.close();
                }
                runOnUiThread(() -> Message.append("Sender: " + fileName + "\n"));
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