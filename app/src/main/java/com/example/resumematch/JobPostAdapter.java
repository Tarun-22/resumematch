package com.example.resumematch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobPostAdapter extends RecyclerView.Adapter<JobPostAdapter.JobViewHolder> {

    private List<JobPost> jobList;

    public JobPostAdapter(List<JobPost> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_post, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobPost job = jobList.get(position);
        holder.title.setText(job.getTitle());
        holder.resumeCount.setText(job.getResumeCount() + " Resumes Scanned");
    }

    @Override
    public int getItemCount() {
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
