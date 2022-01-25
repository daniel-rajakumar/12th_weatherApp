package com.example.weatherapp;

import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class CityWeather {
    String apiKey = "5f05d53229dbaaa68c7dca9bf5eacb09";
    JSONObject jsonObject_geo,
            jsonObject_metric_oneCall, jsonObject_metric_history,
            jsonObject_imperial_oneCall, jsonObject_imperial_history;
    Geocoder geocoder;

    TreeMap<Integer, JSONObject> current_metric = new TreeMap<>();
    TreeMap<Integer, JSONObject> current_imperial = new TreeMap<>();
    ArrayList<JSONObject> daily_metric = new ArrayList<>();
    ArrayList<JSONObject> daily_imperial = new ArrayList<>();

    ArrayList<HourlyWeather> list = new ArrayList<>();
    RecyclerView recyclerView;

    static int instanceCount = 0;
    boolean success = false;


    CityWeather(MainActivity mainActivity, String zip) {
        list.clear();
        instanceCount++;
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
        String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip + ",US&appid=" + apiKey;
        jsonObject_geo = getJsonFromUrl(link);

        Log.d("data", link + " -> " + jsonObject_geo);
        if (jsonObject_geo == null){
            Toast.makeText(mainActivity, "Not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        success = true;

        try {
            link = "https://api.openweathermap.org/data/2.5/onecall"
                    + "?lat=" + jsonObject_geo.get("lat").toString()
                    + "&lon=" + jsonObject_geo.get("lon").toString()
    //                + "&exclude=" + "daily"
                    + "&units=" + "metric"
                    + "&appid=" + apiKey;
            jsonObject_metric_oneCall = getJsonFromUrl(link);

            link = "https://api.openweathermap.org/data/2.5/onecall"
                    + "?lat=" + jsonObject_geo.get("lat").toString()
                    + "&lon=" + jsonObject_geo.get("lon").toString()
    //                + "&exclude=" + "daily"
                    + "&units=" + "imperial"
                    + "&appid=" + apiKey;
            jsonObject_imperial_oneCall = getJsonFromUrl(link);


            link = "http://api.openweathermap.org/data/2.5/onecall/timemachine"
                    + "?lat=" + jsonObject_geo.get("lat").toString()
                    + "&lon=" + jsonObject_geo.get("lon").toString()
                    + "&dt=" + Instant.now().getEpochSecond()
                    + "&units=" + "metric"
                    + "&appid=" + apiKey;
            jsonObject_metric_history = getJsonFromUrl(link);

            Log.d("data", link + " -> " + jsonObject_metric_history);

            link = "http://api.openweathermap.org/data/2.5/onecall/timemachine"
                    + "?lat=" + jsonObject_geo.get("lat").toString()
                    + "&lon=" + jsonObject_geo.get("lon").toString()
                    + "&dt=" + Instant.now().getEpochSecond()
                    + "&units=" + "imperial"
                    + "&appid=" + apiKey;
            jsonObject_imperial_history = getJsonFromUrl(link);
            Log.d("data", link + " -> " + jsonObject_imperial_history);


            setUp_current();
            setUp_daily();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (Map.Entry<Integer, JSONObject> entry : current_imperial.entrySet()) {
            try {
                String temp = entry.getValue().get("temp").toString();
                String time = new SimpleDateFormat("hh:mm").format(
                                new Date(1000 * Long.parseLong(entry.getValue().get("dt").toString()))
                              );
                int weatherIcon = getIcon(entry.getValue().getJSONArray("weather").getJSONObject(0).get("main").toString());

                list.add(new HourlyWeather(temp, time, weatherIcon, entry.getKey() == 0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerView = mainActivity.findViewById(R.id.listView_hourlyWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        if (instanceCount == 1) {
            recyclerView.addItemDecoration(new CustomAdapter.HorizontalSpaceItemDecoration(60));
        }
        recyclerView.setAdapter(new CustomAdapter(list, mainActivity));

//        print_windSpeed();
//        print();
    }

    void print_windSpeed(){
        try {
            Log.d("data", "windSpeed -> history: imperial: " + jsonObject_imperial_history.getJSONObject("current").get("wind_speed") + " mile/hr");
            Log.d("data", "windSpeed -> oneCall: imperial: " + jsonObject_imperial_oneCall.getJSONObject("current").get("wind_speed") + " mile/hr");
            Log.d("data", "windSpeed -> history: metric:   " + jsonObject_metric_history.getJSONObject("current").get("wind_speed") + " meter/sec");
            Log.d("data", "windSpeed -> oneCall: metric:   " + jsonObject_metric_oneCall.getJSONObject("current").get("wind_speed") + " meter/sec");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void setUp_current() throws JSONException {
        JSONArray jsonArray_hourly_metric, jsonArray_hourly_imperial;

        jsonArray_hourly_metric = jsonObject_metric_history.getJSONArray("hourly");
        jsonArray_hourly_imperial = jsonObject_imperial_history.getJSONArray("hourly");
        for (int i = jsonArray_hourly_metric.length() - 2; i < jsonArray_hourly_metric.length(); i++) {
            current_metric.put(i - jsonArray_hourly_metric.length(), jsonArray_hourly_metric.getJSONObject(i));
            current_imperial.put(i - jsonArray_hourly_imperial.length(), jsonArray_hourly_imperial.getJSONObject(i));
        }

        current_metric.put(0, jsonObject_metric_oneCall.getJSONObject("current"));
        current_imperial.put(0, jsonObject_imperial_oneCall.getJSONObject("current"));

        jsonArray_hourly_metric = jsonObject_metric_oneCall.getJSONArray("hourly");
        jsonArray_hourly_imperial = jsonObject_imperial_oneCall.getJSONArray("hourly");
            for (int i = 1; i < 13; i++) {
            current_metric.put(i + 1, jsonArray_hourly_metric.getJSONObject(i));
            current_imperial.put(i + 1, jsonArray_hourly_imperial.getJSONObject(i));
        }
    }

    void setUp_daily() throws JSONException {
        JSONArray jsonArray_metric_daily = jsonObject_metric_oneCall.getJSONArray("daily");
        JSONArray jsonArray_imperial_daily = jsonObject_imperial_oneCall.getJSONArray("daily");

        for (int i = 0; i < jsonArray_metric_daily.length(); i++) {
            daily_metric.add(jsonArray_metric_daily.getJSONObject(i));
            daily_imperial.add(jsonArray_imperial_daily.getJSONObject(i));
        }
    }

    void print() {
        Log.d("data", "Current: ");
        for (Map.Entry<Integer, JSONObject> entry : current_metric.entrySet()) {
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
        for (int i = 0; i < daily_metric.size(); i++) {
//            Log.d("data", i + " -> " + daily.get(i));
            try {
                Log.d("data", i + " -> " + new Date(1000 * Long.parseLong(daily_metric.get(i).get("dt").toString())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private JSONObject getJsonFromUrl(String url) {
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

    public String getLat(){
        try {
            return jsonObject_geo.get("lat").toString();
        } catch (JSONException e) { return null; }
    }


    public String getLon(){
        try {
            return jsonObject_geo.get("lon").toString();
        } catch (JSONException e) { return null; }
    }


    public String getCityName(){
        try {
            return jsonObject_geo.get("name").toString();
        } catch (JSONException e) { return null; }
    }


    public String getCurrentVisibility(){
        try {
            return round(Double.parseDouble(current_imperial.get(0).get("visibility").toString()) * 0.000621371, 2) + "";
        } catch (JSONException e) { return null; }
    }


    public String getCurrentHumidity(){
        try {
            return current_imperial.get(0).get("humidity").toString();
        } catch (JSONException e) { return null; }
    }

    public String getCurrentWindSpeed(){
        try {
            return current_imperial.get(0).get("wind_speed").toString();
        } catch (JSONException e) { return  null; }
    }

    public String getCurrentDate(){
        try {
            return new SimpleDateFormat("EEEE, MMM d — h:mm a").format(
                        new Date(1000 * Long.parseLong(current_metric.get(0).get("dt").toString()))
                   );
        } catch (JSONException e) { return null; }
    }

    public String getCurrentTemp() {
        try {
            return current_imperial.get(0).get("temp").toString();
        } catch (JSONException e) { return null; }
    }

    public String getCurrentWeatherDescription(){
        try {
            return current_metric.get(0).getJSONArray("weather").getJSONObject(0).get("description").toString();
        } catch (JSONException e) { return null; }
    }

    public double round(double num, int place){
        return (double) Math.round(num * Math.pow(10, place)) / Math.pow(10, place);
    }

    public int getCurrentIcon(){
        try {
            return getIcon(current_metric.get(0).getJSONArray("weather").getJSONObject(0).get("main").toString());
        } catch (JSONException e) { return -1; }
    }

    public int getCurrentGif(){
//        try {
//            return getGif(current_metric.get(0).getJSONArray("weather").getJSONObject(0).get("main").toString());
//        } catch (JSONException e) { return -1; }
        return R.drawable.gif_snow;
    }

    public int getIcon(String mainDescription){
        switch (mainDescription.toLowerCase()) {
            case "thunderstorm": return R.drawable.icon_thunder;
            case "drizzle": return R.drawable.icon_heavyrain;
            case "rain": return R.drawable.icon_rain;
            case "snow": return R.drawable.icon_snow;
            case "clear": return R.drawable.icon_sunny;
            case "clouds": return R.drawable.icon_cloudy;
            default: return R.drawable.icon_mist;
        }
    }

    public int getGif(String mainDescription){
        switch (mainDescription.toLowerCase()) {
            case "thunderstorm": return R.drawable.icon_thunder;
            case "drizzle": return R.drawable.icon_heavyrain;
            case "rain": return R.drawable.gif_rain;
            case "snow": return R.drawable.gif_snow;
            case "clear": return R.drawable.icon_sunny;
            case "clouds": return R.drawable.icon_cloudy;
            default: return R.drawable.icon_mist;
        }
    }

    public boolean isSuccess(){
        return success;
    }

}
