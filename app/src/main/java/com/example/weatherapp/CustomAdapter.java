package com.example.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ArrayList<HourlyWeather> list;
    Context context;

    public CustomAdapter(ArrayList<HourlyWeather> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_weather_card, parent, false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.textView_time.setText(list.get(position).getTime());
        holder.textView_temp.setText(list.get(position).getTemp() + "°");
        Glide.with(context).load(list.get(position).getWeatherIcon()).into(holder.imageView_icon);

        MarginLayoutParams layoutParams = (MarginLayoutParams) holder.cardView_weatherCard.getLayoutParams();


        if (list.get(position).isCurrent()){
            Glide.with(context).load(R.drawable.gif_snow).into(holder.imageView_background);
            layoutParams.setMargins(0, 0, 0, 0);
            holder.imageView_icon.getLayoutParams().width = 175;
            holder.imageView_icon.getLayoutParams().height = 175;
            holder.textView_time.setTextSize(19);
            holder.textView_temp.setTextSize(20);
            holder.cardView_weatherCard.setStrokeColor(ContextCompat.getColor(context, R.color.hourly_card_border));
            holder.cardView_weatherCard.setStrokeWidth(5);
        } else {
            Glide.with(context).load(R.color.hourly_card_background).into(holder.imageView_background);
//            layoutParams.setMargins(15, 15, 15, 15);
            layoutParams.setMargins(20, 20, 20, 20);
            holder.imageView_icon.getLayoutParams().width = 125;
            holder.imageView_icon.getLayoutParams().height = 125;
            holder.textView_time.setTextSize(13);
            holder.textView_temp.setTextSize(14);
            holder.cardView_weatherCard.setStrokeColor(ContextCompat.getColor(context, R.color.hourly_card_border));
            holder.cardView_weatherCard.setStrokeWidth(3);
        }

        holder.cardView_weatherCard.requestLayout();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_temp, textView_time;
        ImageView imageView_icon, imageView_background;
        MaterialCardView cardView_weatherCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_temp = itemView.findViewById(R.id.textView_temp);
            this.textView_time = itemView.findViewById(R.id.textView_time);
            this.imageView_icon = itemView.findViewById(R.id.imageView_icon);
            this.imageView_background = itemView.findViewById(R.id.imageView_background);
            this.cardView_weatherCard = itemView.findViewById(R.id.cardView_WeatherCard);
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

