package com.jackchen.day_21;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private LoadingView loading_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading_view = (LoadingView) findViewById(R.id.loading_view);


        // 3秒之后让动画隐藏
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading_view.setVisibility(View.GONE);
            }
        } , 3000) ;
    }
}
