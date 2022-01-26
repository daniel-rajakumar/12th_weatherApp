package com.example.weatherapp;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CustomAdapter_2 extends RecyclerView.Adapter<CustomAdapter_2.ViewHolder> {

    ArrayList<DailyWeather> list;
    Context context;

    public CustomAdapter_2(ArrayList<DailyWeather> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomAdapter_2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_weather_card_2, parent, false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter_2.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getWeatherIcon()).into(holder.imageView_dailyWeatherIcon);
        holder.TextView_dailyTempLow.setText(list.get(position).getTemp_low());
        holder.textView_dailyTempHigh.setText(list.get(position).getTemp_high());
        holder.textView_dailyMainDescription.setText(list.get(position).getDescription());
        holder.textView_dailyTime.setText(list.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_dailyTime,
                textView_dailyMainDescription,
                textView_dailyTempHigh,
                TextView_dailyTempLow;

        ImageView imageView_dailyWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_dailyTime = itemView.findViewById(R.id.textView_dailyTime);
            imageView_dailyWeatherIcon = itemView.findViewById(R.id.imageView_dailyWeatherIcon);
            textView_dailyMainDescription = itemView.findViewById(R.id.textView_dailyMainDescription);
            textView_dailyTempHigh = itemView.findViewById(R.id.textView_dailyTempHigh);
            TextView_dailyTempLow = itemView.findViewById(R.id.textView_dailyTempLow);
        }
    }

    public static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalSpaceHeight;

        public HorizontalSpaceItemDecoration(int horizontalSpaceHeight) {
            this.horizontalSpaceHeight = horizontalSpaceHeight;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.right = horizontalSpaceHeight;
            }
        }
    }

}

