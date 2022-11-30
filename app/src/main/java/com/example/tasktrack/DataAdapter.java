package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
    // Data Adapter Class
    // USed for working with the recycle view list element
    private Context context;
    private int rowID;
    private ArrayList<String> taskTitles;


    public DataAdapter(Context context, int rowID, ArrayList<String> taskTitles) {
        this.context = context;
        this.rowID = rowID;

        this.taskTitles = taskTitles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout and return the tree view
        View v = LayoutInflater.from(this.context).inflate(this.rowID, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        // populate or bind the viewholder fields with data
        // takes in an integer pos marking the index of the list.
        // populates that list item with appropriate name, image and position of player
        holder.rowTitle.setText(taskTitles.get(pos));

    }

    @Override
    public int getItemCount() {
        return taskTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView rowTitle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.rowTitle);
        }

    }
}
