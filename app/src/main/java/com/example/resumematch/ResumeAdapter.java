package com.example.resumematch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder> {

    List<Resume> resumeList;

    public ResumeAdapter(List<Resume> resumeList) {
        this.resumeList = resumeList;
    }

    @NonNull
    @Override
    public ResumeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resume, parent, false);
        return new ResumeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumeViewHolder holder, int position) {
        Resume resume = resumeList.get(position);
        holder.textResumeId.setText(resume.getId());
        holder.textResumeDate.setText(resume.getDate());
        holder.textMatchScore.setText(String.valueOf(resume.getMatch()));
    }

    @Override
    public int getItemCount() {
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
