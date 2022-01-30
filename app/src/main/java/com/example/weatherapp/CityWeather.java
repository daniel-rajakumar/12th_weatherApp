package com.example.weatherapp;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.IntStream;

class CityWeather {
    String apiKey;
    JSONObject[] jsonObjects;
    JSONObject jsonObject_geo,
            jsonObject_metric_oneCall, jsonObject_metric_history,
            jsonObject_imperial_oneCall, jsonObject_imperial_history;

    TreeMap<Integer, JSONObject> current_metric = new TreeMap<>();
    TreeMap<Integer, JSONObject> current_imperial = new TreeMap<>();
    ArrayList<JSONObject> daily_metric = new ArrayList<>();
    ArrayList<JSONObject> daily_imperial = new ArrayList<>();

    ArrayList<HourlyWeather> list_hourly = new ArrayList<>();
    ArrayList<DailyWeather> list_daily = new ArrayList<>();
    RecyclerView recyclerView_hourly, recyclerView_daily;


    static int instanceCount = 0;

    TextView textView_currentTemp,
            textView_currentWeatherDescription,
            textView_cityName,
            textView_currentDate,
            textView_currentHumidity,
            textView_currentFeelsLike,
            textView_lat_lon,
            textView_windSpeed,
            textView_tomorrowTempMin ;

    EditText editText_ZipCode;
    Button button_search;
    ImageView imageView_currentWeatherIcon, imageView_backgroundGif;
    MotionLayout motionLayout;




    CityWeather(MainActivity mainActivity, String zip) {
        list_hourly.clear();
        list_daily.clear();
        apiKey = mainActivity.getResources().getString(R.string.api_key);

        instanceCount++;
        jsonObject_geo = getJsonFromUrl("http://api.openweathermap.org/geo/1.0/zip?zip=" + zip + ",US&appid=" + apiKey)[0];

        if (jsonObject_geo == null){
            Toast.makeText(mainActivity, "Not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        assignVariables(mainActivity);

        try {
            jsonObjects = getJsonFromUrl(
                    MessageFormat.format("https://api.openweathermap.org/data/2.5/onecall?lat={0}&lon={1}&units=metric&appid={2}", jsonObject_geo.get("lat"), jsonObject_geo.get("lon"), apiKey),
                    MessageFormat.format("https://api.openweathermap.org/data/2.5/onecall?lat={0}&lon={1}&units=imperial&appid={2}", jsonObject_geo.get("lat"), jsonObject_geo.get("lon"), apiKey),
                    MessageFormat.format("http://api.openweathermap.org/data/2.5/onecall/timemachine?lat={0}&lon={1}&dt={2}&units=metric&appid={3}", jsonObject_geo.get("lat"), jsonObject_geo.get("lon"), Long.toString(Instant.now().getEpochSecond()), apiKey),
                    MessageFormat.format("http://api.openweathermap.org/data/2.5/onecall/timemachine?lat={0}&lon={1}&dt={2}&units=imperial&appid={3}", jsonObject_geo.get("lat"), jsonObject_geo.get("lon"), Long.toString(Instant.now().getEpochSecond()), apiKey)
            );

            jsonObject_metric_oneCall   = jsonObjects[0];
            jsonObject_imperial_oneCall = jsonObjects[1];
            jsonObject_metric_history   = jsonObjects[2];
            jsonObject_imperial_history = jsonObjects[3];


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
                int weatherGif = getGif(entry.getValue().getJSONArray("weather").getJSONObject(0).get("main").toString());

                list_hourly.add(new HourlyWeather(temp, time, weatherIcon, weatherGif,  entry.getKey() == 0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i < daily_imperial.size(); i++){
            try {
                String temp_low =  daily_imperial.get(i).getJSONObject("temp").get("min").toString();
                String temp_high = daily_imperial.get(i).getJSONObject("temp").get("max").toString();
                String description = daily_imperial.get(i).getJSONArray("weather").getJSONObject(0).get("main").toString();
                String time = new SimpleDateFormat("EE").format(
                        new Date(1000 * Long.parseLong(daily_imperial.get(i).get("dt").toString()))
                );
                int weatherIcon = getIcon(daily_imperial.get(i).getJSONArray("weather").getJSONObject(0).get("main").toString());

                list_daily.add(new DailyWeather(time, temp_low, temp_high, description, weatherIcon));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerView_hourly.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_daily.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));


        if (instanceCount == 1) {
            recyclerView_hourly.addItemDecoration(new CustomAdapter_hourly.HorizontalSpaceItemDecoration(60));
            recyclerView_daily.addItemDecoration(new CustomAdapter_daily.VerticalSpaceItemDecoration(60));
        }

        recyclerView_hourly.setAdapter(new CustomAdapter_hourly(list_hourly, mainActivity));
        recyclerView_daily.setAdapter(new CustomAdapter_daily(list_daily, mainActivity));










        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {

            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) { }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                if (motionLayout.getProgress() <= 0.5) {
                    textView_currentTemp.setText(String.format("%s°", getCurrentTemp()));
                    textView_currentWeatherDescription.setText(getCurrentWeatherDescription());
                    Glide.with(mainActivity).load(getCurrentIcon()).into(imageView_currentWeatherIcon);
                    Glide.with(mainActivity).load(getCurrentGif()).into(imageView_backgroundGif);
                    textView_currentHumidity.setText(String.format("%s%%", getCurrentHumidity()));
                    textView_currentFeelsLike.setText(String.format("%s°", getCurrentFeelsLike()));
                    textView_windSpeed.setText(String.format("%s mph", getCurrentWindSpeed()));
                    updateAlpha(1 - progress);
                } else {
                    textView_currentTemp.setText(String.format("%s°", getTomorrowTempMax()));
                    textView_currentWeatherDescription.setText(getTomorrowWeatherDescription());
                    Glide.with(mainActivity).load(getTomorrowIcon()).into(imageView_currentWeatherIcon);
                    Glide.with(mainActivity).load(getTomorrowGif()).into(imageView_backgroundGif);
                    textView_currentHumidity.setText(String.format("%s%%", getTomorrowHumidity()));
                    textView_currentFeelsLike.setText(String.format("%s°", getTomorrowFeelsLike()));
                    textView_windSpeed.setText(String.format("%s mph", getTomorrowWindSpeed()));
                    updateAlpha(progress);
                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                updateAlpha(1);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) { }

            void updateAlpha(float progress){
                imageView_backgroundGif.setAlpha(progress);
            }
        });


        updateInfo(mainActivity);
    }


    void setUp_current() throws JSONException {
        JSONArray jsonArray_hourly_metric, jsonArray_hourly_imperial;

        jsonArray_hourly_metric = jsonObject_metric_history.getJSONArray("hourly");
        jsonArray_hourly_imperial = jsonObject_imperial_history.getJSONArray("hourly");

        for (int i = jsonArray_hourly_metric.length() - 1; i < jsonArray_hourly_metric.length(); i++) {
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

        for (int i = 1; i < jsonArray_metric_daily.length(); i++) {
            daily_metric.add(jsonArray_metric_daily.getJSONObject(i));
            daily_imperial.add(jsonArray_imperial_daily.getJSONObject(i));
        }
    }


    /**
     * Takes a list of urls, runs in parallel, and returns the data as JSONObject[urls.length].
     * @param urls
     * @return JsonObject[]
     */
    private JSONObject[] getJsonFromUrl(String... urls) {
        final JSONObject[] jsonObject = new JSONObject[urls.length];
        Thread[] threads = new Thread[urls.length];

        IntStream.range(0, urls.length).forEach(i ->
            threads[i] = new Thread(() -> {
            try {
                jsonObject[i] = new JSONObject(
                        new Scanner(
                                new URL(urls[i]).openStream()
                        ).nextLine());

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }));

        for (Thread thread: threads) thread.start();
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
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


    public String getCurrentFeelsLike(){
        try {
            return current_imperial.get(0).get("feels_like").toString();
        } catch (JSONException e) { return null; }
    }

    public String getTomorrowFeelsLike(){
        try {
            return daily_imperial.get(0).getJSONObject("feels_like").get("day").toString();
        } catch (JSONException e) { return null; }
    }


    public String getCurrentHumidity(){
        try {
            return current_imperial.get(0).get("humidity").toString();
        } catch (JSONException e) { return null; }
    }


    public String getTomorrowHumidity(){
        try {
            return daily_imperial.get(0).get("humidity").toString();
        } catch (JSONException e) { return null; }
    }

    public String getCurrentWindSpeed(){
        try {
            return current_imperial.get(0).get("wind_speed").toString();
        } catch (JSONException e) { return  null; }
    }

    public String getTomorrowWindSpeed(){
        try {
            return daily_imperial.get(0).get("wind_speed").toString();
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

    public String getTomorrowTempMax() {
        try {
            return daily_imperial.get(1).getJSONObject("temp").get("max").toString();
        } catch (JSONException e) { return null; }
    }

    public String getTomorrowTempMin(){
        try {
            return daily_imperial.get(1).getJSONObject("temp").get("min").toString();
        } catch (JSONException e) { return null; }
    }


    public String getCurrentWeatherDescription(){
        try {
            return current_metric.get(0).getJSONArray("weather").getJSONObject(0).get("description").toString();
        } catch (JSONException e) { return null; }
    }

    public String getTomorrowWeatherDescription(){
        try {
            return daily_imperial.get(1).getJSONArray("weather").getJSONObject(0).get("description").toString();
        } catch (JSONException e) { return null; }
    }


    public int getCurrentIcon(){
        try {
            return getIcon(current_imperial.get(0).getJSONArray("weather").getJSONObject(0).get("main").toString());
        } catch (JSONException e) { return -1; }
    }

    public int getTomorrowIcon(){
        try {
            return getIcon(daily_imperial.get(1).getJSONArray("weather").getJSONObject(0).get("main").toString());
        } catch (JSONException e) { return -1; }
    }



    public int getTomorrowGif(){
        try {
            return getGif(daily_imperial.get(1).getJSONArray("weather").getJSONObject(0).get("main").toString());
        } catch (JSONException e) { return -1; }
    }

    public int getCurrentGif(){
        try {
            return getGif(current_imperial.get(0).getJSONArray("weather").getJSONObject(0).get("main").toString());
        } catch (JSONException e) { return -1; }
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
            case "thunderstorm": return R.drawable.gif_thunder;
            case "drizzle": return R.drawable.gif_dizzle;
            case "rain": return R.drawable.gif_rain;
            case "snow": return R.drawable.gif_snow;
            case "clear": return R.drawable.gif_clear;
            case "clouds": return R.drawable.gif_cloud;
            default: return R.drawable.gif_mist;
        }
    }

    void assignVariables(MainActivity mainActivity){
        textView_currentTemp = mainActivity.findViewById(R.id.textView_currentTemp);
        textView_currentWeatherDescription = mainActivity.findViewById(R.id.textView_weatherDescription);
        textView_cityName = mainActivity.findViewById(R.id.textView_cityName);
        textView_windSpeed = mainActivity.findViewById(R.id.textView_currentWindSpeed_1);
        textView_currentDate = mainActivity.findViewById(R.id.textView_currentDate);
        textView_currentHumidity = mainActivity.findViewById(R.id.textView_currentHumidity_1);
        textView_currentFeelsLike = mainActivity.findViewById(R.id.textView_currentFeelsLike_1);
        textView_lat_lon = mainActivity.findViewById(R.id.textView_lat_lon);
        textView_tomorrowTempMin = mainActivity.findViewById(R.id.textView_tomorrowTempMin);

        editText_ZipCode = mainActivity.findViewById(R.id.editText_ZipCode);

        button_search = mainActivity.findViewById(R.id.button_search);

        imageView_backgroundGif = mainActivity.findViewById(R.id.imageVIew_backgroundGif);
        imageView_currentWeatherIcon = mainActivity.findViewById(R.id.imageView_weatherIcon);

        recyclerView_hourly = mainActivity.findViewById(R.id.listView_hourlyWeather);
        recyclerView_daily = mainActivity.findViewById(R.id.listView_dailyWeather);

        motionLayout = mainActivity.findViewById(R.id.motionLayout);
    }



    void updateInfo(MainActivity mainActivity){
        textView_currentTemp.setText(String.format("%s°", getCurrentTemp()));
        textView_currentWeatherDescription.setText(getCurrentWeatherDescription());
        textView_cityName.setText(getCityName());
        textView_windSpeed.setText(String.format("%s mph", getCurrentWindSpeed()));
        textView_currentDate.setText(getCurrentDate());
        textView_currentHumidity.setText(String.format("%s%%", getCurrentHumidity()));
        textView_currentFeelsLike.setText(String.format("%s°", getCurrentFeelsLike()));
        textView_lat_lon.setText(String.format("(%s, %s)", getLat(), getLon()));
        textView_tomorrowTempMin.setText(String.format("%s°", getTomorrowTempMin()));
        Glide.with(mainActivity).load(getCurrentIcon()).into(imageView_currentWeatherIcon);
        Glide.with(mainActivity).load(getCurrentGif()).into(imageView_backgroundGif);
    }


}
