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
    TextView textView_currentTemp,
            textView_currentWeatherDescription,
            textView_cityName,
            textView_currentDate,
            textView_currentHumidity,
            textView_currentVisibility,
            textView_lat_lon,
            textView_windSpeed;
    EditText editText_ZipCode;
    Button button_search;
    AtomicReference<CityWeather> cityWeather;
    ImageView textView_currentWeatherIcon;



    void run() {
        cityWeather = new AtomicReference<>(new CityWeather(this, "08852"));


        textView_currentTemp = findViewById(R.id.textView_currentTemp);
        textView_currentWeatherDescription = findViewById(R.id.textView_weatherDescription);
        textView_cityName = findViewById(R.id.textView_cityName);
        textView_windSpeed = findViewById(R.id.textView_currentWindSpeed_1);
        textView_currentDate = findViewById(R.id.textView_currentDate);
        textView_currentHumidity = findViewById(R.id.textView_currentHumidity_1);
        textView_currentVisibility = findViewById(R.id.textView_currentVisibility_1);
        textView_currentWeatherIcon = findViewById(R.id.imageView_weatherIcon);
        textView_lat_lon = findViewById(R.id.textView_lat_lon);

        updateInfo();

        editText_ZipCode = findViewById(R.id.editText_ZipCode);
        button_search = findViewById(R.id.button_search);

        button_search.setOnClickListener(e -> {
            cityWeather.set(new CityWeather(this, editText_ZipCode.getText().toString()));
            if (cityWeather.get().isSuccess()){
                updateInfo();
            }
        });


    }

    void updateInfo(){
        textView_currentTemp.setText(cityWeather.get().getCurrentTemp() + "°");
        textView_currentWeatherDescription.setText(cityWeather.get().getCurrentWeatherDescription());
//        textView_currentWeatherDescription.setText("rain");
        textView_cityName.setText(cityWeather.get().getCityName());
        textView_windSpeed.setText(cityWeather.get().getCurrentWindSpeed() + " mph");
        textView_currentDate.setText(cityWeather.get().getCurrentDate());
        textView_currentHumidity.setText(cityWeather.get().getCurrentHumidity() + "%");
        textView_currentVisibility.setText(cityWeather.get().getCurrentVisibility() + " mi");
        textView_lat_lon.setText(String.format("(%s, %s)", cityWeather.get().getLat(), cityWeather.get().getLon()));
        Glide.with(this).load(cityWeather.get().getCurrentIcon()).into(textView_currentWeatherIcon);
        Glide.with(this).load(cityWeather.get().getCurrentGif()).into((ImageView) findViewById(R.id.image_view));
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

