/*
 * TODO: MAKE SURE TO HIDE AND CHANGE THE API KEY LATER
 */
package com.example.weatherapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.*;

public class MainActivity extends AppCompatActivity {
    TextView textView_currentTemp, textView_currentWeatherDescription;

    void run() {
        CityWeather cityWeather = new CityWeather("08852");

        Glide.with(this).load(R.drawable.rain).into((ImageView) findViewById(R.id.image_view));

        textView_currentTemp = findViewById(R.id.textView_currentTemp);
        textView_currentWeatherDescription = findViewById(R.id.textView_weatherDescription);

        textView_currentTemp.setText(cityWeather.getCurrentTemp() + "°");
        textView_currentWeatherDescription.setText(cityWeather.getCurrentWeatherDescription());

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

//        Window w = getWindow(); // in Activity's onCreate() for instance
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.run();

    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}

