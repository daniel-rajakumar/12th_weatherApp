package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    String zip = "08852";
    String apiKey = "5f05d53229dbaaa68c7dca9bf5eacb09";
    JSONObject jsonObject_geo, jsonObject_oneCall;

    void run() throws JSONException {
        String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip  + ",US&appid=" + apiKey;

        jsonObject_geo = getJsonFromUrl(link);

        Log.d("data", jsonObject_geo.toString());
        Log.d("data", "lat: " + jsonObject_geo.get("lat"));
        Log.d("data", "lon: " + jsonObject_geo.get("lon"));

        link = "https://api.openweathermap.org/data/2.5/onecall"
                + "?lat=" + jsonObject_geo.get("lat")
                + "&lon=" + jsonObject_geo.get("lon")
                + "&exclude=" + "daily"
                + "&units=" + "metric"
                + "&appid=" + apiKey;


        jsonObject_oneCall = getJsonFromUrl(link);
        Weather weather = new Weather(
                jsonObject_oneCall.getJSONObject("current").get("temp").toString(),
                jsonObject_oneCall.getJSONObject("current").getJSONArray("weather").getJSONObject(0).get("description").toString()
        );

        Log.d("data", "weather: " + weather.getDescription() + " | temp: " + weather.getTemp());


        for (int i = 0; i < 4; i++){
            weather = new Weather(
                    jsonObject_oneCall.getJSONArray("hourly").getJSONObject(i).get("temp").toString(),
                    jsonObject_oneCall.getJSONArray("hourly").getJSONObject(i).getJSONArray("weather").getJSONObject(0).get("description").toString()
            );

            Log.d("data", i + ") " + weather);

        }

//        Log.d("data", link);
    }

    JSONObject getJsonFromUrl(String url) {
        final JSONObject[] jsonObject = new JSONObject[1];
        Thread thread = new Thread(() -> {
            try {
                jsonObject[0] = new JSONObject(
                                    new Scanner(
                                        new URL(url).openStream()
                                    ).nextLine());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return jsonObject[0];
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try { this.run(); } catch (JSONException e) { e.printStackTrace();
        }
    }

}

class Weather {
    String description;
    String temp;

    Weather(String temp, String description){
        this.description = description;
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public String getTemp() {
        return temp;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Weather{");
        sb.append("description='").append(description).append('\'');
        sb.append(", temp='").append(temp).append('\'');
        sb.append('}');
        return sb.toString();
    }
}