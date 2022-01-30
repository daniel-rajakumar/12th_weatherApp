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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.*;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    Button button_search;
    EditText editText_zip;
    CityWeather cityWeather;



    void run() {
        editText_zip = findViewById(R.id.editText_ZipCode);
        button_search = findViewById(R.id.button_search);

        cityWeather = new CityWeather(this, "08852");

        button_search.setOnClickListener(e -> new CityWeather(this, editText_zip.getText().toString()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make fully Android Transparent Status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

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

