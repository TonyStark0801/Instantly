package com.app.instantly;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

        try {
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        generateQrCode(SERVER_IP+":"+SERVER_PORT);

        Thread1 = new Thread(new Thread1());
        Thread1.start();
        btnSend.setOnClickListener(v -> {
            message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                Thread3=new Thread(new Thread3(message,bytes));
                Thread3.start();
            }
            else{
                Toast.makeText(this, "Can't Send Empty message", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }


    DataOutputStream output;
    DataInputStream input;
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
                output = new DataOutputStream(socket.getOutputStream());
                input=new DataInputStream(socket.getInputStream());
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));
                Thread2 =new Thread(new Thread2(socket));
                Thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

    }
    class Thread2 implements Runnable {
        Socket socket;
        Thread2(Socket socket){
            this.socket=socket;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readUTF();
                    if (message!= null) {
                        runOnUiThread(() -> tvMessages.append("client:" + message + " "));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class Thread3 implements Runnable{
        private String message;
        private byte[] bytes;
        Thread3(String message, byte[] bytes){
            this.message= message;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            try {
                output.writeUTF(message);
//                    f_output.writeObject(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                tvMessages.append("Server: "+message+"\n");
                etMessage.setText("");
            });
        }
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
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

