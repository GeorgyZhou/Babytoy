package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GameActivity extends Activity {

    private int state_progress;
    private int game_state;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "GAME_ACTIVITY";
    private static final long TIME_OUT = 5000;
    private static final String MAC_ADDRESS_1 = "98:D3:35:70:F1:C2";
    private static final String MAC_ADDRESS_2 = "20:16:05:09:26:10";
    private static final String mUUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int CONNECT_FINISH = 0;
    public static final int SUCCESS_PLAY = 3;
    public static final int PROMPT = 4;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread = null;
    private ConnectedThread mConnectedThread = null;
    private ProgressBar progressBar;
    private ImageView imageView;
    private ImageView correctImageView;
    private TextView textView;
    private Handler mHandler;
    private Integer defaultAnimation;
    private ImageView promptImageView;
    private Button firstButton;
    private Button secondButton;
    private Timer promptTimer;
    private Random random;
    private SharedPreferences sharedPreferences;
    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        int tmp = getIntent().getIntExtra("CHOICE", 0);
        switch(tmp){
            case MenuActivity.NEW_GAME:
                game_state = 0;
                state_progress = 0;
                break;
            case MenuActivity.CONTINUE_GAME:
                game_state = sharedPreferences.getInt("GAME_STATE", 0);
                state_progress = sharedPreferences.getInt("STATE_PROGRESS", 0);
                Log.i(TAG, "[Data Read]: game_state: " + game_state + "; state_progress: "+ state_progress);
                break;
        }

        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.what){
                    case CONNECT_FINISH:
                        communicateWithDevice((BluetoothSocket)msg.obj);
                        break;
                    case MESSAGE_READ:
                        String info = (String)msg.obj;
                        switch(info){

                            case "C":
                                Log.i(TAG, "C Signal Received");
                                if (game_state == 0) {
                                    updateAnimation(R.drawable.anim2);
                                    updateProgressView(0);
                                }
                                break;
                            case "D":
                                Log.i(TAG, "D Signal Received");
                                if (game_state == 0) {
                                    updateAnimation(R.drawable.anim3);
                                    updateProgressView(0);
                                }
                                break;
                            case "E":
                                Log.i(TAG, "E Signal Received");
                                if (game_state == 0) {
                                    updateAnimation(R.drawable.anim4);
                                    updateProgressView(0);
                                }
                                break;
                            case "F":
                                Log.i(TAG, "F Signal Received");
                                if(game_state == 1) {
                                    updateAnimation(R.drawable.anim6);
                                    updateProgressView(0);
                                }
                                break;
                            case "G":
                                Log.i(TAG, "G Signal Received");
                                if (game_state == 1) {
                                    updateProgressView(0);
                                    updateAnimation(R.drawable.anim6);
                                }
                                break;
                            case "H":
                                Log.i(TAG, "H Signal Received");
                                if (game_state == 1){
                                    updateAnimation(R.drawable.anim7);
                                    updateProgressView(0);
                                }
                                break;
                            default:
                                Log.i(TAG, "[Invalid Signal]: " + info);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:

                        break;
                    case SUCCESS_PLAY:
                        updateAnimation(defaultAnimation);
                        break;
                    case PROMPT:
                        Log.i(TAG, "SENT MESSAGE TO UI---"+msg.arg1 + ":" + msg.arg2);
                        pause();
                        if(msg.arg1 == 0){
                            switch(msg.arg2){
                                case 0:
                                    promptImageView.setBackgroundResource(R.drawable.knowledge0);
                                    break;
                                case 1:
                                    promptImageView.setBackgroundResource(R.drawable.knowledge1);
                                    break;
                                case 2:
                                    promptImageView.setBackgroundResource(R.drawable.knowledge2);
                                    break;
                                case 3:
                                    promptImageView.setBackgroundResource(R.drawable.knowledge3);
                                    break;
                                case 4:
                                    promptImageView.setBackgroundResource(R.drawable.knowledge4);
                                    break;
                            }
                            promptImageView.setVisibility(View.VISIBLE);
                        }else if(msg.arg1 == 1){
                            switch(msg.arg2){
                                case 0:
                                    firstButton.setBackgroundResource(R.drawable.question1a);
                                    secondButton.setBackgroundResource(R.drawable.question1b);
                                    break;
                                case 1:
                                    firstButton.setBackgroundResource(R.drawable.question2a);
                                    secondButton.setBackgroundResource(R.drawable.question2b);
                                    break;
                                case 2:
                                    firstButton.setBackgroundResource(R.drawable.question3a);
                                    secondButton.setBackgroundResource(R.drawable.question3b);
                                    break;
                                case 3:
                                    firstButton.setBackgroundResource(R.drawable.question4a);
                                    secondButton.setBackgroundResource(R.drawable.question4b);
                                    break;
                                case 4:
                                    firstButton.setBackgroundResource(R.drawable.question5a);
                                    secondButton.setBackgroundResource(R.drawable.question5b);
                                    break;
                            }
                            questionId = msg.arg2;
                            firstButton.setVisibility(View.VISIBLE);
                            secondButton.setVisibility(View.VISIBLE);
                        }


                }
                return false;
            }
        });

        imageView = (ImageView) findViewById(R.id.game_gif);
        progressBar = (ProgressBar) findViewById(R.id.game_process_bar);
        textView = (TextView) findViewById(R.id.game_process_bubble);

        correctImageView = (ImageView) findViewById(R.id.correct_image_view);
        promptImageView = (ImageView) findViewById(R.id.prompt_image_view);
        firstButton = (Button) findViewById(R.id.first_choice_btn);
        secondButton = (Button) findViewById(R.id.second_choice_btn);

        randomQuestion();

        if(game_state == 0){
            defaultAnimation = R.drawable.anim1;
        }
        else if(game_state == 1) {
            defaultAnimation = R.drawable.anim5;
        }
        updateAnimation(defaultAnimation);
        updateProgressView(3);

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
    protected void onDestroy(){
        super.onDestroy();
        if (mConnectedThread != null)
            mConnectThread.cancel();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("GAME_STATE", game_state);
        editor.putInt("STATE_PROGRESS", state_progress);
        Log.i(TAG, "[Data Saved]: game_state: " + game_state + "; state progress: " + state_progress);
        editor.apply();
    }

    private void pause(){
        promptTimer.cancel();
        promptTimer.purge();
    }

    private void resume(){
        promptTimer = new Timer(true);
        promptTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();
                mHandler.obtainMessage(PROMPT, random.nextInt(2), random.nextInt(5))
                        .sendToTarget();
            }
        }, 6000 + random.nextInt(2000));
    }

    private void randomQuestion(){
        random = new Random();

        resume();

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);

                switch(questionId){
                    case 0:
                        correctImageView.setBackgroundResource(R.drawable.q1_wrong);
                        correctImageView.setVisibility(View.VISIBLE);
                        resume();
                        break;
                    case 1:
                        correctImageView.setBackgroundResource(R.drawable.q2_wrong);
                        correctImageView.setVisibility(View.VISIBLE);
                        resume();
                        break;
                    case 2:
                        correctImageView.setBackgroundResource(R.drawable.q3_wrong);
                        correctImageView.setVisibility(View.VISIBLE);
                        resume();
                        break;
                    case 3:
                        correctImageView.setBackgroundResource(R.drawable.q_correct);
                        correctImageView.setVisibility(View.VISIBLE);
                        updateProgressView(1);
                        break;
                    case 4:
                        correctImageView.setBackgroundResource(R.drawable.q_correct);
                        correctImageView.setVisibility(View.VISIBLE);
                        updateProgressView(1);
                        break;
                }
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);
                switch(questionId){
                    case 0:
                        correctImageView.setBackgroundResource(R.drawable.q_correct);
                        correctImageView.setVisibility(View.VISIBLE);
                        updateProgressView(1);
                        break;
                    case 1:
                        correctImageView.setBackgroundResource(R.drawable.q_correct);
                        correctImageView.setVisibility(View.VISIBLE);
                        updateProgressView(1);
                        break;
                    case 2:
                        correctImageView.setBackgroundResource(R.drawable.q_correct);
                        correctImageView.setVisibility(View.VISIBLE);
                        updateProgressView(1);
                        break;
                    case 3:
                        correctImageView.setBackgroundResource(R.drawable.q4_wrong);
                        correctImageView.setVisibility(View.VISIBLE);
                        resume();
                        break;
                    case 4:
                        correctImageView.setBackgroundResource(R.drawable.q5_wrong);
                        correctImageView.setVisibility(View.VISIBLE);
                        resume();
                        break;
                }
            }
        });

        promptImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                resume();
            }
        });

        correctImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctImageView.setVisibility(View.INVISIBLE);
                resume();
            }
        });
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

    private void communicateWithDevice(BluetoothSocket mSocket){
        ConnectedThread communicationThread = new ConnectedThread(mSocket);
        communicationThread.start();
    }

    private void connectToDevice(){
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        int flag = 0;
        for (BluetoothDevice device : bondedDevices) {
            Log.i(TAG, "[DEVICE ADDRESS]: " + device.getAddress());
            if ( game_state == 0 && device.getAddress().equals(MAC_ADDRESS_1)) {
                mConnectThread = new ConnectThread(device);
                mConnectThread.start();
                flag = 1;
                break;
            }
            else if( game_state == 1 && device.getAddress().equals(MAC_ADDRESS_2)){
                mConnectThread.cancel();
                mConnectThread = new ConnectThread(device);
                mConnectThread.start();
                flag = 1;
                break;
            }
        }
        if(flag == 0){
            Log.e(TAG, "Fail to Connect: [NO DEVICE WITH MAC ADDRESS FOUND]!!");
        }
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;

        private ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
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
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                if(game_state == 0)
                    for (int i = 0; i < 5; i++)
                        mConnectedThread.write("A".getBytes("US-ASCII"));
                else if(game_state == 1)
                    for (int i = 0; i < 5; i++)
                        mConnectedThread.write("B".getBytes("US-ASCII"));
            } catch(IOException e){
                Log.e(TAG, "Fail to connect: " + e.getMessage());
                try{
                    mmSocket.close();
                } catch(IOException closeException){
                    Log.e(TAG, "Fail to close socket: " + e.getMessage());
                }
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch(IOException e){
                Log.e(TAG, "Fail to close socket: " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "[STREAM GET FAILED]: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            if(mmSocket.isConnected())
                Log.i(TAG, "Connection Established");
            else
                Log.e(TAG, "Socket is not established");
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String str = new String(buffer);
                    str = str.substring(0, bytes);
                    Log.i(TAG, "[MESSAGE RECEIVED]: " + str);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, str)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "[READ FAILED]: " + e.getMessage());
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    private void updateProgressView(int type){
        switch(type){
            case 1:
                state_progress += 50;

                Log.i(TAG, "state: " + game_state);
                break;
            case 0:
                state_progress += 2;
                break;
            case 3:
                Log.i(TAG, "[Load View from Storage]");
                break;
        }
        if(state_progress >= 100){
            game_state++;
            state_progress = 0;
            Log.i(TAG, "state: " + game_state);
            if(game_state == 2){
                game_state = 0;
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("STATE", 0);
                startActivity(intent);
            }else if(game_state == 1){
                if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled())
                    connectToDevice();
                defaultAnimation = R.drawable.anim5;
                updateAnimation(defaultAnimation);

            }
        }

        Log.i(TAG, "STATE PROGRESS: " + state_progress);

        // Update TextView Location
        PercentFrameLayout.LayoutParams params =(PercentFrameLayout.LayoutParams) textView.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo layoutInfo = params.getPercentLayoutInfo();
        layoutInfo.leftMarginPercent= (float)(0.68 + state_progress / 100.0 * 0.2);
        String textString = Integer.toString(state_progress);
        textView.setText(textString);
        textView.requestLayout();

        // update progress bar
        progressBar.setProgress(state_progress);
    }

    private void updateAnimation(Integer resId){
        if(resId != R.drawable.anim1 && resId != R.drawable.anim5) {

            Glide.with(this).load(resId).listener(new RequestListener<Integer, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    GifDrawable drawable = (GifDrawable) resource;
                    GifDecoder decoder = drawable.getDecoder();
                    int duration = 0;

                    for (int i = 0; i < drawable.getFrameCount(); i++) {
                        duration += decoder.getDelay(i);
                    }

                    //switch back
                    mHandler.sendEmptyMessageDelayed(SUCCESS_PLAY, duration * 5);

                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.NONE).into(new GlideDrawableImageViewTarget(imageView, 5));
        }else{
             Glide.with(this).load(resId).diskCacheStrategy(DiskCacheStrategy.NONE).into(new GlideDrawableImageViewTarget(imageView));
        }
    }
}
