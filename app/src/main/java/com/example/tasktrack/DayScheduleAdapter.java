package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.ViewHolder>{
    private Context context;
    private int rowID;
    private ArrayList<HourBin> hourBins;
    private DataAdapter adapter;


    RecycleViewInterface recycleViewInterface;


    public DayScheduleAdapter(Context context, int rowID, ArrayList<HourBin> hourBins, RecycleViewInterface recycleViewInterface) {
        this.context = context;
        this.rowID = rowID;
        this.recycleViewInterface = recycleViewInterface;
        this.hourBins = hourBins;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout and return the tree view
        View v = LayoutInflater.from(this.context).inflate(this.rowID, parent, false);
        return new ViewHolder(v, recycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false);


        ArrayList<TaskObj> binTasks = hourBins.get(pos).getHourTasks();

        adapter = new DataAdapter(context, R.layout.row_layout, binTasks,recycleViewInterface);

        holder.binHeading.setText("Hour " + String.valueOf(pos+1));
        holder.bin_task_items.setLayoutManager(layoutManager);
        holder.bin_task_items.setItemAnimator(new DefaultItemAnimator());
        holder.bin_task_items.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return hourBins.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView bin_task_items;
        public TextView binHeading;



        public ViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            bin_task_items = itemView.findViewById(R.id.bin_task_items);
            binHeading = itemView.findViewById(R.id.binHeading);
        }

    }
}
