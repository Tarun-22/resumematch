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

import java.util.List;

public class JobPostAdapter extends RecyclerView.Adapter<JobPostAdapter.JobViewHolder> {

    private List<JobPost> jobList;
    private Context context;

    public JobPostAdapter(List<JobPost> jobList) {
        this.jobList = jobList;
        Log.d("JobPostAdapter", "Adapter created with " + jobList.size() + " jobs");
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_job_post, parent, false);
        Log.d("JobPostAdapter", "Creating view holder");
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobPost job = jobList.get(position);
        Log.d("JobPostAdapter", "Binding job at position " + position + ": " + job.getTitle());
        holder.title.setText(job.getTitle());
        holder.resumeCount.setText(job.getResumeCount() + " Resumes Scanned");
        
        // Add click listener to navigate to resume list
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResumeListActivity.class);
            intent.putExtra("jobId", job.getId());
            intent.putExtra("jobTitle", job.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("JobPostAdapter", "getItemCount called: " + jobList.size());
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, resumeCount;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textJobTitle);
            resumeCount = itemView.findViewById(R.id.textResumeCount);
        }
    }
}
