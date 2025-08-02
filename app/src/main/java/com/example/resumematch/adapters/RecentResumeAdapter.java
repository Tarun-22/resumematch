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

    private List<ResumeEntity> resume_list;
    private Context context;
    private OnResumeDeleteListener delete_listener;

    public interface OnResumeDeleteListener {
        void onResumeDelete(ResumeEntity resume, int position);
    }

    public RecentResumeAdapter(List<ResumeEntity> resume_list) {
        this.resume_list = resume_list;
    }

    public void delete_resume_listener(OnResumeDeleteListener listener) {
        this.delete_listener = listener;
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
        ResumeEntity resume = resume_list.get(position);
        
        holder.resume_id.setText("Resume #" + resume.getId().substring(0, 8));
        holder.resume_date.setText(resume.getDate());
        holder.match_score.setText(resume.getMatchScore() + "% Match");
        holder.job_title.setText("Applied for: " + resume.getJobTitle());
        
        int matchScore = Integer.parseInt(resume.getMatchScore().replace("%", ""));
        if (matchScore >= 80) {
            holder.match_score.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (matchScore >= 60) {
            holder.match_score.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.match_score.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MatchScoreActivity.class);
            intent.putExtra("resumeId", resume.getId());
            intent.putExtra("matchScore", Integer.parseInt(resume.getMatchScore().replace("%", "")));
            intent.putExtra("resumeText", resume.getResumeText());
            intent.putExtra("jobTitle", resume.getJobTitle());
            intent.putExtra("date", resume.getDate());
            intent.putExtra("photoPath", resume.getPhotoPath());

            if (resume.getExtractedDataJson() != null && !resume.getExtractedDataJson().isEmpty()) {
                try {
                    org.json.JSONObject extractedData = new org.json.JSONObject(resume.getExtractedDataJson());
                    
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

                    intent.putExtra("skillScore", extractedData.optInt("skillScore", 0));
                    intent.putExtra("experienceScore", extractedData.optInt("experienceScore", 0));
                    intent.putExtra("availabilityScore", extractedData.optInt("availabilityScore", 0));
                    intent.putExtra("educationScore", extractedData.optInt("educationScore", 0));
                    intent.putExtra("distanceScore", extractedData.optInt("distanceScore", 0));

                    intent.putExtra("feedback", extractedData.optString("feedback", ""));
                    intent.putExtra("recommendations", extractedData.optString("recommendations", ""));

                } catch (Exception e) {
                    android.util.Log.e("RecentResumeAdapter", "Error parsing extracted data: " + e.getMessage());
                }
            }

            context.startActivity(intent);
        });

        holder.delete_resume.setOnClickListener(v -> {
            if (delete_listener != null) {
                delete_listener.onResumeDelete(resume, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resume_list.size();
    }

    public void updateResumeList(List<ResumeEntity> newResumeList) {
        this.resume_list = newResumeList;
        notifyDataSetChanged();
    }

    public void removeResume(int position) {
        if (position >= 0 && position < resume_list.size()) {
            resume_list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resume_id, resume_date, match_score, job_title;
        android.widget.Button delete_resume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resume_id = itemView.findViewById(R.id.textResumeId);
            resume_date = itemView.findViewById(R.id.textResumeDate);
            match_score = itemView.findViewById(R.id.textMatchScore);
            job_title = itemView.findViewById(R.id.textJobTitle);
            delete_resume = itemView.findViewById(R.id.buttonDeleteResume);
        }
    }
} 