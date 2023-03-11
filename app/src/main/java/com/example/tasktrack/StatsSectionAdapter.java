package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class StatsSectionAdapter extends RecyclerView.Adapter<StatsSectionAdapter.ViewHolder>{
    // Data Adapter Class
    // USed for working with the recycle view list element
    private Context context;
    private int rowID;
    private ArrayList<sectionTypes> sections;
    private StatsViewPageAdapter adapter;
    private Stats stats;
    private ArrayList<TaskObj> tasks;


    RecycleViewInterface recycleViewInterface;


    public StatsSectionAdapter(Context context, int rowID, ArrayList<TaskObj> tasks, ArrayList<sectionTypes> sections,  Stats stats,  RecycleViewInterface recycleViewInterface) {
        this.context = context;
        this.rowID = rowID;
        this.sections = sections;
        this.recycleViewInterface = recycleViewInterface;
        this.stats = stats;
        this.tasks = tasks;
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
        ArrayList<graphs> titles = new ArrayList<>();
        switch (sections.get(pos)){
            case taskCompletion:
                titles = new ArrayList<>();
                titles.add(graphs.completeVsIncompletePie);
                titles.add(graphs.tagsPie);
                adapter = new StatsViewPageAdapter(context, R.layout.graph_views, titles, tasks, stats,recycleViewInterface);
                holder.sectionHeading.setText("Completion Stats");
                break;
            case taskTags:
                titles = new ArrayList<>();
                titles.add(graphs.tagsPie);
                titles.add(graphs.completeVsIncompletePie);
                adapter = new StatsViewPageAdapter(context, R.layout.graph_views, titles, tasks, stats,recycleViewInterface);
                holder.sectionHeading.setText("Tag Stats");
                break;
            case taskDifficulty:
                titles = new ArrayList<>();
                titles.add(graphs.difficultyBar);
                adapter = new StatsViewPageAdapter(context, R.layout.graph_views, titles, tasks, stats,recycleViewInterface);
                holder.sectionHeading.setText("Difficulty Stats");
                break;
            case taskTime:
                titles = new ArrayList<>();
                titles.add(graphs.onTimeScatterPlot);
                titles.add(graphs.onTimePie);
                adapter = new StatsViewPageAdapter(context, R.layout.graph_views, titles, tasks, stats,recycleViewInterface);
                holder.sectionHeading.setText("Timing Stats");
                break;
        }
        holder.viewPager.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ViewPager2 viewPager;
        public TextView sectionHeading;


        public ViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewPager);
            sectionHeading = itemView.findViewById(R.id.sectionHeading);

        }

    }
}
