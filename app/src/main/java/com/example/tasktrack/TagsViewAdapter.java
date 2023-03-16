package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagsViewAdapter extends RecyclerView.Adapter<TagsViewAdapter.ViewHolder>{
    // Data Adapter Class
    // USed for working with the recycle view list element
    private Context context;
    private int rowID;
    private ArrayList<String> tags = new ArrayList<>();
    RecycleViewInterface recycleViewInterface;



    public TagsViewAdapter(Context context, int rowID, ArrayList<String> tags, RecycleViewInterface recycleViewInterface) {
        this.context = context;
        this.rowID = rowID;
        this.tags = tags;
        this.recycleViewInterface = recycleViewInterface;

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
        holder.rowTag.setText(tags.get(pos));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView rowTag;

        public ViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);

            rowTag = itemView.findViewById(R.id.rowTag);

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
