package com.example.resumematch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumematch.R;
import com.example.resumematch.models.JobEntity;
import com.example.resumematch.activities.JobSelectionActivity;

import java.util.List;

public class JobSelectionAdapter extends RecyclerView.Adapter<JobSelectionAdapter.ViewHolder> {

    private List<JobEntity> job_list;
    private Context context;
    private JobSelectionActivity activity;

    public JobSelectionAdapter(List<JobEntity> job_list, JobSelectionActivity activity) {
        this.job_list = job_list;
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
        JobEntity job = job_list.get(position);
        
        holder.job_title.setText(job.getTitle());
        holder.textJobDescription.setText(job.getDescription());
        holder.resume_count.setText(job.getResumeCount() + " resumes");
        
        holder.itemView.setOnClickListener(v -> {
            activity.onJobSelected(job);
        });
    }

    @Override
    public int getItemCount() {
        return job_list.size();
    }

    public void updateJobList(List<JobEntity> newJobList) {
        this.job_list = newJobList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView job_title, textJobDescription, resume_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            job_title = itemView.findViewById(R.id.textJobTitle);
            textJobDescription = itemView.findViewById(R.id.textJobDescription);
            resume_count = itemView.findViewById(R.id.textResumeCount);
        }
    }
} 