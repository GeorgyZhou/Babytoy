package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView imageView = (ImageView)findViewById(R.id.game_result);
        Button returnButton = (Button)findViewById(R.id.return_menu_btn);


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        int state = getIntent().getIntExtra("STATE", -1);
        if(state == 0){
            imageView.setImageResource(R.drawable.game_win);
        }else if(state == 1){
            imageView.setImageResource(R.drawable.game_lose);
        }
    }
}
