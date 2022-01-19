package com.example.margoapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<Patient> list;

    public MyAdapter(Context context, ArrayList<Patient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Patient patient = list.get(position);
        holder.roomNumber.setText(patient.getRoomNumber());
        holder.bedNumber.setText(patient.getBedNumber());
        holder.condition.setText(patient.getCondition());

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);


        if (holder.condition.getText().toString().contains("OK")) {
            holder.condition.setTextColor(Color.GREEN);
        }else if (holder.condition.getText().toString().contains("LEAK")){
            holder.condition.setTextColor(Color.RED);
            holder.condition.startAnimation(anim);
        }else {
            holder.condition.setTextColor(Color.GRAY);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView roomNumber, bedNumber, condition;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            roomNumber = itemView.findViewById(R.id.roomNumber);
            bedNumber = itemView.findViewById(R.id.bedNumber);
            condition = itemView.findViewById(R.id.condition);
        }

    }

}
