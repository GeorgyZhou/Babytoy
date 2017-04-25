package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.clj.fastble.BleManager;
import com.clj.fastble.scan.ListScanCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GameActivity extends Activity {

    private int progress = 0;
    private int game_state = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "GAME_ACTIVITY";
    private static final long TIME_OUT = 5000;
    private static final String MAC_ADDRESS_1 = "";
    private static final String MAC_ADDRESS_2 = "";
    private static final String mUUID = "16fd2706-8baf-433b-82eb-8c7fada847da";
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int state = getIntent().getIntExtra("STATE", 0);

        ImageView imageView = (ImageView) findViewById(R.id.game_gif);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.game_process_bar);
        TextView textView = (TextView) findViewById(R.id.game_process_bubble);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.anim2).into(imageViewTarget);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.e(TAG, "No bluetooth adapter found!");
        }
        else{
            if(! mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else{
                connectToDevice();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                connectToDevice();
            }
        }
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;
            try{
                UUID mmUUID = UUID.fromString(mUUID);
                tmp = device.createRfcommSocketToServiceRecord(mmUUID);
            } catch (Exception e){
                Log.e(TAG, "Create Connection Failed: " + device.getAddress() +
                        "---Error: " + e.getMessage());
            }
            mmSocket = tmp;
        }

        @Override
        public void run(){
            mBluetoothAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
            } catch(IOException e){
                Log.e(TAG, "Fail to connect: " + e.getMessage());
                try{
                    mmSocket.close();
                } catch(IOException closeException){
                    Log.e(TAG, "Fail to close socket: " + e.getMessage());
                }
                return;
            }

            manageConnectSocket();
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch(IOException e){
                Log.e(TAG, "Fail to close socket: " + e.getMessage());
            }
        }
    }

    private class ManageThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ManageThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    private void connectToDevice(){
        BluetoothDevice device;
        if ( 0 <= game_state && game_state <= 3) {
            device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS_1);
        }
        else{
            device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS_2);
        }
        ConnectThread connectThread = new ConnectThread(device);
        connectThread.start();

    }


    private void updateProgressView(View view){
        PercentFrameLayout.LayoutParams params =(PercentFrameLayout.LayoutParams) view.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo layoutInfo = params.getPercentLayoutInfo();
        layoutInfo.leftMarginPercent= (float)(0.68 + progress / 100.0 * 0.2);
        view.requestLayout();
    }
}
