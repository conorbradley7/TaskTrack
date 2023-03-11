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

public class TaskSectionAdapter extends RecyclerView.Adapter<TaskSectionAdapter.ViewHolder>{
    private Context context;
    private int rowID;
    private ArrayList<TasksPageActivity.taskSectionTypes> sections;
    private DataAdapter adapter;
    private ArrayList<TaskObj> tasks, incompleteTasks, completeTasks, todoTasks;


    RecycleViewInterface recycleViewInterface;


    public TaskSectionAdapter(Context context, int rowID, ArrayList<TaskObj> tasks, ArrayList<TaskObj> incompleteTasks, ArrayList<TaskObj> completeTasks, ArrayList<TaskObj> todoTasks, ArrayList<TasksPageActivity.taskSectionTypes> sections, RecycleViewInterface recycleViewInterface) {
        this.context = context;
        this.rowID = rowID;
        this.sections = sections;
        this.recycleViewInterface = recycleViewInterface;
        this.tasks = tasks;
        this.incompleteTasks = incompleteTasks;
        this.completeTasks = completeTasks;
        this.todoTasks = todoTasks;
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
        switch (sections.get(pos)){
            case todo:
                adapter = new DataAdapter(context, R.layout.row_layout, todoTasks,recycleViewInterface);
                holder.taskSectionHeading.setText("ToDo Tasks");
                break;
            case complete:
                adapter = new DataAdapter(context, R.layout.completed_task_layout, completeTasks,recycleViewInterface);
                holder.taskSectionHeading.setText("Completed Tasks");
                break;
            case incomplete:
                adapter = new DataAdapter(context, R.layout.incomplete_task_layout, incompleteTasks,recycleViewInterface);
                holder.taskSectionHeading.setText("Incomplete Tasks");
                break;
        }

        holder.task_items.setLayoutManager(layoutManager);
        holder.task_items.setItemAnimator(new DefaultItemAnimator());
        holder.task_items.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView task_items;
        public TextView taskSectionHeading;



        public ViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            task_items = itemView.findViewById(R.id.task_items);
            taskSectionHeading = itemView.findViewById(R.id.taskSectionHeading);
        }

    }
}
