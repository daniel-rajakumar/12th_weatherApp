package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String zip = "08852";
    String apiKey = "5f05d53229dbaaa68c7dca9bf5eacb09";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip  + ",US&appid=" + apiKey;
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert url != null;
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            Thread thread = new Thread(() -> {
                try {
                    connection.connect();

                    // Let’s read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JSONObject json = new JSONObject(reader.readLine());

                    Log.d("link", "code: " + json.get("zip"));
                } catch (IOException | JSONException e) {
                    Log.d("link", "code: fail ");
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (IOException e) {
            Log.d("link", "code: fails");
            e.printStackTrace();
        }

    }
}