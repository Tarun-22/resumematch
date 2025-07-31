package com.example.resumematch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Intent;
import com.example.resumematch.ResumeDetailsActivity;

public class RecentResumeAdapter extends RecyclerView.Adapter<RecentResumeAdapter.ViewHolder> {

    private List<ResumeEntity> resumeList;
    private Context context;

    public RecentResumeAdapter(List<ResumeEntity> resumeList) {
        this.resumeList = resumeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_resume, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResumeEntity resume = resumeList.get(position);
        
        holder.textResumeId.setText("Resume #" + resume.getId().substring(0, 8));
        holder.textResumeDate.setText(resume.getDate());
        holder.textMatchScore.setText(resume.getMatchScore() + "% Match");
        holder.textJobTitle.setText("Applied for: " + resume.getJobTitle());
        
        // Set color based on match score
        int matchScore = Integer.parseInt(resume.getMatchScore().replace("%", ""));
        if (matchScore >= 80) {
            holder.textMatchScore.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (matchScore >= 60) {
            holder.textMatchScore.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.textMatchScore.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        
        // Set click listener to view resume details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResumeDetailsActivity.class);
            intent.putExtra("resumeId", resume.getId());
            intent.putExtra("jobTitle", resume.getJobTitle());
            intent.putExtra("resumeDate", resume.getDate());
            intent.putExtra("matchScore", resume.getMatchScore());
            intent.putExtra("resumeContent", resume.getResumeText());
            // TODO: Add matched and missing keywords from database
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resumeList.size();
    }

    public void updateResumeList(List<ResumeEntity> newResumeList) {
        this.resumeList = newResumeList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textResumeId, textResumeDate, textMatchScore, textJobTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textResumeId = itemView.findViewById(R.id.textResumeId);
            textResumeDate = itemView.findViewById(R.id.textResumeDate);
            textMatchScore = itemView.findViewById(R.id.textMatchScore);
            textJobTitle = itemView.findViewById(R.id.textJobTitle);
        }
    }
} 