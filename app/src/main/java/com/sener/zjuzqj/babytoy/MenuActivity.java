package com.sener.zjuzqj.babytoy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class MenuActivity extends Activity implements View.OnClickListener{

    public static final int NEW_GAME = 0;
    public static final int CONTINUE_GAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView imageView = (ImageView) findViewById(R.id.menu_background);

        Button beginButton = (Button) findViewById(R.id.begin_button);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        Button achievementButton = (Button) findViewById(R.id.achievement_button);

        beginButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        achievementButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.begin_button){
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("STATE", NEW_GAME);
            startActivity(intent);
        }
        else if(v.getId() == R.id.achievement_button){
            Intent intent = new Intent(this, AchievementActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.continue_button){

        }
    }
}
