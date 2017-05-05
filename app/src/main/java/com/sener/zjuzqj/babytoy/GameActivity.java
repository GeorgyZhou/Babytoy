package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GameActivity extends Activity {

    private int state_progress;
    private int game_state;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "GAME_ACTIVITY";
    private static final long TIME_OUT = 5000;
    private static final String MAC_ADDRESS_1 = "";
    private static final String MAC_ADDRESS_2 = "";
    private static final String mUUID = "16fd2706-8baf-433b-82eb-8c7fada847da";
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int CONNECT_FINISH = 0;
    public static final int SUCCESS_PLAY = 3;
    public static final int PROMPT = 4;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textView;
    private Handler mHandler;
    private Integer defaultAnimation;
    private ImageView promptImageView;
    private Button firstButton;
    private Button secondButton;
    private Timer promptTimer;
    private Random random;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game_state = getIntent().getIntExtra("STATE", 0);
        state_progress = getIntent().getIntExtra("PROGRESS", 0);

        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.what){
                    case CONNECT_FINISH:
                        communicateWithDevice((BluetoothSocket)msg.obj);
                        break;
                    case MESSAGE_READ:
                        String info = new String((byte[])msg.obj);

                        updateProgressView(0);
                        switch(info){
                            case "LA":
                                updateAnimation(R.drawable.anim2);
                            case "SA":
                                updateAnimation(R.drawable.anim3);
                            case "GA":
                                updateAnimation(R.drawable.anim4);
                            case "LB":
                                updateAnimation(R.drawable.anim6);
                            case "SB":
                                updateAnimation(R.drawable.anim6);
                            case "GB":
                                updateAnimation(R.drawable.anim7);
                            default:
                                if(game_state == 0){

                                } else if(game_state == 1){

                                }
                        }
                        break;
                    case MESSAGE_WRITE:

                        break;
                    case SUCCESS_PLAY:
                        updateAnimation(defaultAnimation);
                        break;
                    case PROMPT:
                        pause();
                        if(msg.arg1 == 0){
                            String name = "knowledge" + msg.arg2 + ".png";
                            promptImageView.setVisibility(View.VISIBLE);
                            promptImageView.setImageResource(getResource(name));
                        }else if(msg.arg1 == 1){
                            String name1 =  "question" + msg.arg2 + "a.png";
                            String name2 = "question" + msg.arg2 + "b.png";
                            firstButton.setVisibility(View.VISIBLE);
                            secondButton.setVisibility(View.VISIBLE);
                            firstButton.setBackgroundResource(getResource(name1));
                            secondButton.setBackgroundResource(getResource(name2));
                        }


                }
                return false;
            }
        });

        imageView = (ImageView) findViewById(R.id.game_gif);
        progressBar = (ProgressBar) findViewById(R.id.game_process_bar);
        textView = (TextView) findViewById(R.id.game_process_bubble);

        promptImageView = (ImageView) findViewById(R.id.prompt_image_view);
        firstButton = (Button) findViewById(R.id.first_choice_btn);
        secondButton = (Button) findViewById(R.id.second_choice_btn);

        randomQuestion();

        defaultAnimation = R.drawable.anim1;

        updateAnimation(defaultAnimation);

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


    private int getResource(String imageName) {
        Context ctx = getBaseContext();
        return getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
    }

    private void pause(){
        promptTimer.cancel();
    }

    private void resume(){
        promptTimer = new Timer();
        promptTimer.schedule(task, 5000, 80000 + random.nextInt(20000));
    }

    private void randomQuestion(){
        random = new Random();

        task  = new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();
                mHandler.obtainMessage(PROMPT, random.nextInt(1), random.nextInt(4))
                        .sendToTarget();
            }
        };

        resume();

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);

                updateProgressView(1);
                resume();
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);

                resume();
            }
        });

        promptImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
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
        CommunicationThread communicationThread = new CommunicationThread(mSocket);
        communicationThread.start();
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
            } catch(IOException e){
                Log.e(TAG, "Fail to connect: " + e.getMessage());
                try{
                    mmSocket.close();
                } catch(IOException closeException){
                    Log.e(TAG, "Fail to close socket: " + e.getMessage());
                }
                return;
            }

            mHandler.obtainMessage(CONNECT_FINISH, mmSocket)
                    .sendToTarget();
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch(IOException e){
                Log.e(TAG, "Fail to close socket: " + e.getMessage());
            }
        }
    }

    private class CommunicationThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private CommunicationThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

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
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    private void updateProgressView(int type){
        switch(type){
            case 1:
                state_progress += 10;
                break;
            case 0:
                state_progress += 2;
                break;
        }
        if(state_progress >= 100){
            game_state++;
            if(game_state == 2){
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("STATE", 0);
                startActivity(intent);
            }else if(game_state == 1){
                state_progress = 0;
                defaultAnimation = R.drawable.anim5;
            }
        }

        // Update TextView Location
        PercentFrameLayout.LayoutParams params =(PercentFrameLayout.LayoutParams) textView.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo layoutInfo = params.getPercentLayoutInfo();
        layoutInfo.leftMarginPercent= (float)(0.68 + state_progress / 100.0 * 0.2);
        textView.requestLayout();

        // update progress bar
        progressBar.setProgress(state_progress);
    }

    private void updateAnimation(Integer resId){
        if(resId != R.drawable.anim1 && resId != R.drawable.anim4) {

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
            }).into(new GlideDrawableImageViewTarget(imageView, 5));
        }else{
            Glide.with(this).load(resId).into(new GlideDrawableImageViewTarget(imageView));
        }
    }
}
