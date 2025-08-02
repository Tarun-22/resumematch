package com.example.resumematch.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.models.JobPost;
import com.example.resumematch.activities.JobApplicationsActivity;
import com.example.resumematch.activities.EditJobActivity;

import java.util.List;

public class job_post_adapter extends RecyclerView.Adapter<job_post_adapter.ViewHolder> {

    private List<JobPost> job_list;
    private Context context;
    private OnJobDeleteListener delete_listener;

    public interface OnJobDeleteListener {
        void onJobDelete(JobPost job, int position);
    }

    public job_post_adapter(List<JobPost> job_list) {
        this.job_list = job_list;
    }

    public void job_delete_listener(OnJobDeleteListener listener) {
        this.delete_listener = listener;
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
            JobPost job = job_list.get(position);
            
            if (job != null) {
                holder.job_title.setText(job.getTitle() != null ? job.getTitle() : "Untitled Job");
                holder.jd.setText(job.getDescription() != null ? job.getDescription() : "No description");
                holder.resume_count.setText(job.getResumeCount() + " applications");
                
                // Set click listener for the entire item
                holder.itemView.setOnClickListener(v -> {
                    try {
                        Log.d("JobPostAdapter", "Job clicked: " + job.getId());
                        
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
                
                holder.view_applications.setOnClickListener(v -> {
                    Intent intent = new Intent(context, JobApplicationsActivity.class);
                    intent.putExtra("jobId", job.getId());
                    intent.putExtra("jobTitle", job.getTitle());
                    context.startActivity(intent);
                });

                holder.delete_job.setOnClickListener(v -> {
                    if (delete_listener != null) {
                        delete_listener.onJobDelete(job, position);
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
        return job_list.size();
    }

    public void updateJobList(List<JobPost> newJobList) {
        this.job_list = newJobList;
        notifyDataSetChanged();
    }

    public void removeJob(int position) {
        if (position >= 0 && position < job_list.size()) {
            job_list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView job_title, jd, resume_count;
        Button view_applications, delete_job;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            job_title = itemView.findViewById(R.id.textJobTitle);
            jd = itemView.findViewById(R.id.textJobDescription);
            resume_count = itemView.findViewById(R.id.textResumeCount);
            view_applications = itemView.findViewById(R.id.buttonViewApplications);
            delete_job = itemView.findViewById(R.id.buttonDeleteJob);
        }
    }
}
