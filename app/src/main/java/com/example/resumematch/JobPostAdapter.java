package com.example.resumematch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class JobPostAdapter extends RecyclerView.Adapter<JobPostAdapter.ViewHolder> {

    private List<JobPost> jobList;
    private Context context;
    private OnJobDeleteListener deleteListener;

    public interface OnJobDeleteListener {
        void onJobDelete(JobPost job, int position);
    }

    public JobPostAdapter(List<JobPost> jobList) {
        this.jobList = jobList;
    }

    public void setOnJobDeleteListener(OnJobDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JobPost job = jobList.get(position);
            
            if (job != null) {
                holder.textJobTitle.setText(job.getTitle() != null ? job.getTitle() : "Untitled Job");
                holder.textJobDescription.setText(job.getDescription() != null ? job.getDescription() : "No description");
                holder.textResumeCount.setText(job.getResumeCount() + " applications");
                
                // Set click listener for the entire item
                holder.itemView.setOnClickListener(v -> {
                    try {
                        Log.d("JobPostAdapter", "Job clicked: " + job.getId());
                        
                        // Navigate to job details or edit
                        Intent intent = new Intent(context, EditJobActivity.class);
                        intent.putExtra("jobId", job.getId());
                        intent.putExtra("jobTitle", job.getTitle());
                        intent.putExtra("jobDescription", job.getDescription());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e("JobPostAdapter", "Error navigating to edit job: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                
                // Set click listener for View Applications button
                holder.buttonViewApplications.setOnClickListener(v -> {
                    Intent intent = new Intent(context, JobApplicationsActivity.class);
                    intent.putExtra("jobId", job.getId());
                    intent.putExtra("jobTitle", job.getTitle());
                    context.startActivity(intent);
                });

                // Set click listener for Delete button
                holder.buttonDeleteJob.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onJobDelete(job, position);
                    }
                });
            } else {
                Log.e("JobPostAdapter", "Job at position " + position + " is null");
            }
        } catch (Exception e) {
            Log.e("JobPostAdapter", "Error binding view holder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobList(List<JobPost> newJobList) {
        this.jobList = newJobList;
        notifyDataSetChanged();
    }

    public void removeJob(int position) {
        if (position >= 0 && position < jobList.size()) {
            jobList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textJobTitle, textJobDescription, textResumeCount;
        android.widget.Button buttonViewApplications, buttonDeleteJob;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textJobTitle = itemView.findViewById(R.id.textJobTitle);
            textJobDescription = itemView.findViewById(R.id.textJobDescription);
            textResumeCount = itemView.findViewById(R.id.textResumeCount);
            buttonViewApplications = itemView.findViewById(R.id.buttonViewApplications);
            buttonDeleteJob = itemView.findViewById(R.id.buttonDeleteJob);
        }
    }
}
