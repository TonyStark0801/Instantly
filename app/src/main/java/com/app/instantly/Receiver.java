package com.app.instantly;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


import android.graphics.Bitmap;
import android.widget.ImageView;

@SuppressLint("SetTextI18n")
public class Receiver extends AppCompatActivity {
    ServerSocket serverSocket;
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    byte[] bytes;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    TextView connectionStatus;

    public static String SERVER_IP = "";
    public static final String SERVER_PORT = "8080";
    String message;
    String SSID="";
    String PASSWORD="";
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        connectionStatus = findViewById(R.id.tvConnectionStatus);

        Bundle extras = getIntent().getExtras();
        String val = extras.getString("key");
        if(Objects.equals(val,"HOTSPOT")){
            createHotspot();
            SERVER_IP = getLocalIpAddress();
        }
        else{
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            try {
                SERVER_IP = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            generateQrCode("WIFI"+":"+SERVER_IP+":"+SERVER_PORT);
        }


        Thread1 = new Thread(new Thread1());
        Thread1.start();
        btnSend.setOnClickListener(v -> {
            if (!fileName.isEmpty()) {
                Thread3 = new Thread(new Thread3(fileName,bytes));
                Thread3.start();
            }
            else {
                Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            }
        });
    }




    protected OutputStream os;
    protected InputStream is;
    protected  DataInputStream dataIS;
    protected DataOutputStream  dataOS;

    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket ;
            try {
                serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
                runOnUiThread(() -> {
                    connectionStatus.setText(R.string.NotConnected);
                    tvIP.setText("IP: " + SERVER_IP);
                    tvPort.setText("Port: " + SERVER_PORT);
                });
                socket = serverSocket.accept();
                socket.setReuseAddress(true);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                dataOS = new DataOutputStream(os);
                dataIS = new DataInputStream(is);
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));
                Thread2 =new Thread(new Thread2());
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
                    if(fileName!=null){
                        runOnUiThread(() -> tvMessages.append("Sender: "+fileName+"\n"));
                        int length = dataIS.readInt();                    // read length of incoming message
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), fileName);
                        if(length>0) {
                            byte[] fileByte = new byte[length];
                            dataIS.readFully(fileByte, 0, fileByte.length); // read the message
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                fos.write(fileByte);
                                Log.d("Saved", "File Created: ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                    else{
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }

//                    FileOutputStream fos = new FileOutputStream("received_file.png");
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = is.read(buffer)) != -1) {
//                        fos.write(buffer, 0, bytesRead);
//                    }
//                    fos.flush();
//                    fos.close();
//                    is.close();
//                    if (fileName!= null) {
//                        runOnUiThread(() -> OutMessage.append("Server: " + fileName + "\n"));
//                    }
//                    Toast.makeText(Receiver.this, "File saved with name ", Toast.LENGTH_SHORT).show();
                }  catch (IOException e) {
                throw new RuntimeException(e);
            }
            }
        }



    }
    class Thread3 implements Runnable{
        private String fileName;
        private byte[] bytes;
        Thread3(String fileName, byte[]bytes) {
            this.fileName = fileName;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            ByteArrayInputStream bais;
            try {


                dataOS.writeUTF(fileName);
                dataOS.flush();
                runOnUiThread(() -> {
                    tvMessages.append("client: " + fileName + "\n");
                    etMessage.setText("");
                });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }




    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void generateQrCode(String data){
            ImageView qrCodeIV = findViewById(R.id.IVQrcode);
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int dimen = Math.min(width, height);
            dimen = dimen * 3 / 4;
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, dimen);
            bitmap = qrgEncoder.getBitmap(0);
            qrCodeIV.setImageBitmap(bitmap);
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
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (dataIS != null) {
                dataIS.close();
            }
            if (dataOS != null) {
                dataOS.close();
            }
        }
            catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createHotspot(){

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                SSID +=reservation.getSoftApConfiguration().getSsid();
                PASSWORD += reservation.getSoftApConfiguration().getPassphrase();
                Log.d("HotSpot", "Hotspot started SSID: " + SSID + " Password: " + PASSWORD);
                Toast.makeText(Receiver.this, "Hotspot started SSID: " + SSID + " Password: " + PASSWORD, Toast.LENGTH_SHORT).show();
                generateQrCode("HOTSPOT"+":"+SSID+":"+PASSWORD+":"+SERVER_IP+":"+SERVER_PORT);
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d("HotSpot", "Hotspot stopped");
                Toast.makeText(Receiver.this, "Hotspot stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d("HotSpot", "Failed to start hotspot: " + reason);
                Toast.makeText(Receiver.this, "Failed to start hotspot", Toast.LENGTH_SHORT).show();
            }
        }, new Handler());

    }

}

