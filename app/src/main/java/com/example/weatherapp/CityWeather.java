package com.example.weatherapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class CityWeather {
    String apiKey = "5f05d53229dbaaa68c7dca9bf5eacb09";
    JSONObject jsonObject_geo, jsonObject_oneCall, jsonObject_history;
    String lat, lon;

    TreeMap<Integer, JSONObject> current = new TreeMap<>();
    ArrayList<JSONObject> daily = new ArrayList<>();

    CityWeather(String zip) {
        String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip + ",US&appid=" + apiKey;
        jsonObject_geo = getJsonFromUrl(link);

        try {
            lat = jsonObject_geo.get("lat").toString();
            lon = jsonObject_geo.get("lon").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        link = "https://api.openweathermap.org/data/2.5/onecall"
                + "?lat=" + lat
                + "&lon=" + lon
//                + "&exclude=" + "daily"
                + "&units=" + "metric"
                + "&appid=" + apiKey;
        jsonObject_oneCall = getJsonFromUrl(link);

        link = "http://api.openweathermap.org/data/2.5/onecall/timemachine"
                + "?lat=" + lat
                + "&lon=" + lon
                + "&dt=" + Instant.now().getEpochSecond()
                + "&units=" + "metric"
                + "&appid=" + apiKey;
        jsonObject_history = getJsonFromUrl(link);

        try {
            setUp_current();
            setUp_daily();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        print();
    }

    void setUp_current() throws JSONException {
        JSONArray jsonArray_hourly;

        jsonArray_hourly = jsonObject_history.getJSONArray("hourly");
        for (int i = 0; i < jsonArray_hourly.length(); i++) {
            current.put(i - jsonArray_hourly.length(), jsonArray_hourly.getJSONObject(i));
        }

        current.put(0, jsonObject_oneCall.getJSONObject("current"));

        jsonArray_hourly = jsonObject_oneCall.getJSONArray("hourly");
        for (int i = 0; i < jsonArray_hourly.length(); i++) {
            current.put(i + 1, jsonArray_hourly.getJSONObject(i));
        }
    }

    void setUp_daily() throws JSONException {
        JSONArray jsonArray_daily = jsonObject_oneCall.getJSONArray("daily");

        for (int i = 0; i < jsonArray_daily.length(); i++) {
            daily.add(jsonArray_daily.getJSONObject(i));
        }
    }

    void print() {
        Log.d("data", "Current: ");
        for (Map.Entry<Integer, JSONObject> entry : current.entrySet()) {
//            Log.d("data", i + " -> " + daily.get(i));
            try {
                Log.d("data", entry.getKey() + " -> " + new Date(1000 * Long.parseLong(entry.getValue().get("dt").toString())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("data", "...");
        Log.d("data", "...");
        Log.d("data", "...");
        Log.d("data", "...");
        Log.d("data", "Daily: ");
        for (int i = 0; i < daily.size(); i++) {
//            Log.d("data", i + " -> " + daily.get(i));
            try {
                Log.d("data", i + " -> " + new Date(1000 * Long.parseLong(daily.get(i).get("dt").toString())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonObject[0];
    }


}
