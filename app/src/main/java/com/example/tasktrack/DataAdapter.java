package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
    // Data Adapter Class
    // USed for working with the recycle view list element
    private Context context;
    private int rowID;
    private ArrayList<TaskObj> taskObjs;
    public ArrayList<String> taskTitlesList;
    public ArrayList<String> taskMoreDetailsList;
    public ArrayList<String> taskTagsList;
    public ArrayList<String> taskDatesList;

    RecycleViewInterface recycleViewInterface;



    public DataAdapter(Context context, int rowID, ArrayList<TaskObj> taskObjs, RecycleViewInterface recycleViewInterface) {
        this.context = context;
        this.rowID = rowID;
        this.taskObjs = taskObjs;
        this.taskTitlesList = new ArrayList<String>();
        this.taskMoreDetailsList = new ArrayList<String>();
        this.taskTagsList = new ArrayList<String>();
        this.taskDatesList = new ArrayList<String>();
        this.recycleViewInterface = recycleViewInterface;



        for (int i=0; i < taskObjs.size(); i++){
            String rowTitle = taskObjs.get(i).getTitle();
            String rowDetails = taskObjs.get(i).getMoreDetails();
            String rowTag = taskObjs.get(i).getTag();
            String rowDate = taskObjs.get(i).getDate();

            if (rowTitle==null){rowTitle="";}
            if (rowDetails==null){rowDetails="";}
            if (rowTag==null){rowTag="";}
            if (rowDate==null){rowDate="";}


            System.out.println("title: " + rowTitle+
                    "\ndetails: " + rowDetails +
                    "\ntag: " + rowTag +
                    "\ndate: "+rowDate);

            System.out.println(taskTitlesList+"=====<");
            taskTitlesList.add(rowTitle);
            taskMoreDetailsList.add(rowDetails);
            taskTagsList.add(rowTag);
            taskDatesList.add(rowDate);

        }
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
        // populate or bind the viewholder fields with data
        // takes in an integer pos marking the index of the list.
        // populates that list item with appropriate name, image and position of player
        holder.rowTitle.setText(taskTitlesList.get(pos));
        holder.rowTag.setText(taskTagsList.get(pos));

    }

    @Override
    public int getItemCount() {
        return taskObjs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView rowTitle;
        public TextView rowTag;


        public ViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.time);
            rowTag = itemView.findViewById(R.id.tasksList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // call the RecycleView Interface Method
                    if (recycleViewInterface != null){
                        //get pos
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            recycleViewInterface.onTaskItemClick(position);
                        }
                    }
                }
            });

        }

    }
}
