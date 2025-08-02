package com.example.resumematch.adapters;

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

import com.example.resumematch.R;
import com.example.resumematch.models.ResumeEntity;
import com.example.resumematch.activities.MatchScoreActivity;

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder> {

    List<ResumeEntity> resumeList;
    private Context context;

    public ResumeAdapter(List<ResumeEntity> resumeList) {
        this.resumeList = resumeList;
        Log.d("ResumeAdapter", "Adapter created with " + resumeList.size() + " resumes");
    }

    @NonNull
    @Override
    public ResumeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_resume, parent, false);
        Log.d("ResumeAdapter", "Creating view holder");
        return new ResumeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumeViewHolder holder, int position) {
        ResumeEntity resume = resumeList.get(position);
        Log.d("ResumeAdapter", "Binding resume at position " + position + ": " + resume.getId());
        holder.textResumeId.setText(resume.getId());
        holder.textResumeDate.setText(resume.getDate());
        holder.textMatchScore.setText(String.valueOf(resume.getMatchScore()));
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MatchScoreActivity.class);
            intent.putExtra("resumeId", resume.getId());
            intent.putExtra("jobId", resume.getJobId());
            intent.putExtra("matchScore", extractScoreFromMatch(resume.getMatchScore()));
            context.startActivity(intent);
        });
    }

    private int extractScoreFromMatch(String matchString) {
        try {
            // Extract number from "85% Match" format
            String numberStr = matchString.replaceAll("[^0-9]", "");
            return Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        Log.d("ResumeAdapter", "getItemCount called: " + resumeList.size());
        return resumeList.size();
    }

    public static class ResumeViewHolder extends RecyclerView.ViewHolder {
        TextView textResumeId, textResumeDate, textMatchScore;

        public ResumeViewHolder(@NonNull View itemView) {
            super(itemView);
            textResumeId = itemView.findViewById(R.id.textResumeId);
            textResumeDate = itemView.findViewById(R.id.textResumeDate);
            textMatchScore = itemView.findViewById(R.id.textMatchScore);
        }
    }
}
