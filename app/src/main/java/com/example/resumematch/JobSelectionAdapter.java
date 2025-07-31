package com.example.resumematch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobSelectionAdapter extends RecyclerView.Adapter<JobSelectionAdapter.ViewHolder> {

    private List<JobEntity> jobList;
    private Context context;
    private JobSelectionActivity activity;

    public JobSelectionAdapter(List<JobEntity> jobList, JobSelectionActivity activity) {
        this.jobList = jobList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobEntity job = jobList.get(position);
        
        holder.textJobTitle.setText(job.getTitle());
        holder.textJobDescription.setText(job.getDescription());
        holder.textResumeCount.setText(job.getResumeCount() + " resumes");
        
        // Set click listener to select this job
        holder.itemView.setOnClickListener(v -> {
            activity.onJobSelected(job);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobList(List<JobEntity> newJobList) {
        this.jobList = newJobList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textJobTitle, textJobDescription, textResumeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textJobTitle = itemView.findViewById(R.id.textJobTitle);
            textJobDescription = itemView.findViewById(R.id.textJobDescription);
            textResumeCount = itemView.findViewById(R.id.textResumeCount);
        }
    }
} 