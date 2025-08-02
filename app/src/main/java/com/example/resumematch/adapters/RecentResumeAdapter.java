package com.example.resumematch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Intent;
import com.example.resumematch.R;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.activities.MatchScoreActivity;

public class RecentResumeAdapter extends RecyclerView.Adapter<RecentResumeAdapter.ViewHolder> {

    private List<ResumeEntity> resumeList;
    private Context context;
    private OnResumeDeleteListener deleteListener;

    public interface OnResumeDeleteListener {
        void onResumeDelete(ResumeEntity resume, int position);
    }

    public RecentResumeAdapter(List<ResumeEntity> resumeList) {
        this.resumeList = resumeList;
    }

    public void setOnResumeDeleteListener(OnResumeDeleteListener listener) {
        this.deleteListener = listener;
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
            Intent intent = new Intent(context, MatchScoreActivity.class);
            intent.putExtra("resumeId", resume.getId());
            intent.putExtra("matchScore", Integer.parseInt(resume.getMatchScore().replace("%", "")));
            intent.putExtra("resumeText", resume.getResumeText());
            intent.putExtra("jobTitle", resume.getJobTitle());
            intent.putExtra("date", resume.getDate());
            intent.putExtra("photoPath", resume.getPhotoPath());

            // Parse stored extracted data from JSON
            if (resume.getExtractedDataJson() != null && !resume.getExtractedDataJson().isEmpty()) {
                try {
                    org.json.JSONObject extractedData = new org.json.JSONObject(resume.getExtractedDataJson());
                    
                    // Add extracted candidate data
                    intent.putExtra("candidateName", extractedData.optString("candidateName", ""));
                    intent.putExtra("candidateEmail", extractedData.optString("email", ""));
                    intent.putExtra("candidatePhone", extractedData.optString("phone", ""));
                    intent.putExtra("candidateAddress", extractedData.optString("address", ""));
                    intent.putExtra("candidateCity", extractedData.optString("city", ""));
                    intent.putExtra("candidateState", extractedData.optString("state", ""));
                    intent.putExtra("candidateZipCode", extractedData.optString("zipCode", ""));
                    intent.putExtra("candidateTitle", extractedData.optString("currentTitle", ""));
                    intent.putExtra("experienceYears", extractedData.optInt("experienceYears", 0));
                    intent.putExtra("education", extractedData.optString("education", ""));
                    intent.putExtra("availability", extractedData.optString("availability", ""));
                    intent.putExtra("availabilityDetails", extractedData.optString("availabilityDetails", ""));
                    intent.putExtra("transportation", extractedData.optString("transportation", ""));
                    intent.putExtra("startDate", extractedData.optString("startDate", ""));
                    intent.putExtra("workAuthorization", extractedData.optString("workAuthorization", ""));
                    intent.putExtra("emergencyContact", extractedData.optString("emergencyContact", ""));
                    intent.putExtra("emergencyPhone", extractedData.optString("emergencyPhone", ""));
                    intent.putExtra("references", extractedData.optString("references", ""));
                    intent.putExtra("previousRetailExperience", extractedData.optString("previousRetailExperience", ""));
                    intent.putExtra("languages", extractedData.optString("languages", ""));
                    intent.putExtra("certifications", extractedData.optString("certifications", ""));

                    // Add category scores
                    intent.putExtra("skillScore", extractedData.optInt("skillScore", 0));
                    intent.putExtra("experienceScore", extractedData.optInt("experienceScore", 0));
                    intent.putExtra("availabilityScore", extractedData.optInt("availabilityScore", 0));
                    intent.putExtra("educationScore", extractedData.optInt("educationScore", 0));
                    intent.putExtra("distanceScore", extractedData.optInt("distanceScore", 0));

                    // Add feedback and recommendations
                    intent.putExtra("feedback", extractedData.optString("feedback", ""));
                    intent.putExtra("recommendations", extractedData.optString("recommendations", ""));

                } catch (Exception e) {
                    android.util.Log.e("RecentResumeAdapter", "Error parsing extracted data: " + e.getMessage());
                }
            }

            context.startActivity(intent);
        });

        // Set click listener for Delete button
        holder.buttonDeleteResume.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onResumeDelete(resume, position);
            }
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

    public void removeResume(int position) {
        if (position >= 0 && position < resumeList.size()) {
            resumeList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textResumeId, textResumeDate, textMatchScore, textJobTitle;
        android.widget.Button buttonDeleteResume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textResumeId = itemView.findViewById(R.id.textResumeId);
            textResumeDate = itemView.findViewById(R.id.textResumeDate);
            textMatchScore = itemView.findViewById(R.id.textMatchScore);
            textJobTitle = itemView.findViewById(R.id.textJobTitle);
            buttonDeleteResume = itemView.findViewById(R.id.buttonDeleteResume);
        }
    }
} 