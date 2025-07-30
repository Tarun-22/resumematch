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

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder> {

    List<Resume> resumeList;
    private Context context;

    public ResumeAdapter(List<Resume> resumeList) {
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
        Resume resume = resumeList.get(position);
        Log.d("ResumeAdapter", "Binding resume at position " + position + ": " + resume.getId());
        holder.textResumeId.setText(resume.getId());
        holder.textResumeDate.setText(resume.getDate());
        holder.textMatchScore.setText(String.valueOf(resume.getMatch()));
        
        // Add click listener to navigate to match score
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MatchScoreActivity.class);
            intent.putExtra("resumeId", resume.getId());
            intent.putExtra("matchScore", resume.getMatch());
            context.startActivity(intent);
        });
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
