package com.sener.zjuzqj.babytoy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView imageView = (ImageView)findViewById(R.id.game_result);
        int state = getIntent().getIntExtra("STATE", -1);
        if(state == 0){

        }else if(state == 1){

        }
    }
}
