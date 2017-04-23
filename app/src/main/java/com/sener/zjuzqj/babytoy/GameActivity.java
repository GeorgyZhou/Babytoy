package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class GameActivity extends Activity {

    private int progress = 0;
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ImageView imageView = (ImageView) findViewById(R.id.game_gif);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.game_process_bar);
        TextView textView = (TextView) findViewById(R.id.game_process_bubble);


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            
        }




        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.anim2).into(imageViewTarget);


    }

    private void updateProgressView(View view){
        PercentFrameLayout.LayoutParams params =(PercentFrameLayout.LayoutParams) view.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo layoutInfo = params.getPercentLayoutInfo();
        layoutInfo.leftMarginPercent= (float)(0.68 + progress / 100.0 * 0.2);
        view.requestLayout();
    }
}
