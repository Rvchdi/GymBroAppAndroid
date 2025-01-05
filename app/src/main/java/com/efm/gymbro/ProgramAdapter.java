package com.efm.gymbro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.model.Program;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {
    private List<Program> programs = new ArrayList<>();
    private final OnProgramClickListener displayListener;
    private final OnProgramClickListener deleteListener;

    public interface OnProgramClickListener {
        void onProgramClick(Program program);
    }

    public ProgramAdapter(OnProgramClickListener displayListener, OnProgramClickListener deleteListener) {
        this.displayListener = displayListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_program, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        Program program = programs.get(position);
        holder.bind(program);
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
        notifyDataSetChanged();
    }

    public void removeProgram(Program program) {
        int position = programs.indexOf(program);
        if (position != -1) {
            programs.remove(position);
            notifyItemRemoved(position);
        }
    }

    class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final TextView programTitle;
        private final TextView programDetails;
        private final MaterialButton displayButton;
        private final MaterialButton deleteButton;

        ProgramViewHolder(View itemView) {
            super(itemView);
            programTitle = itemView.findViewById(R.id.programTitle);
            programDetails = itemView.findViewById(R.id.programDetails);
            displayButton = itemView.findViewById(R.id.displayButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Program program) {
            programTitle.setText(program.getName());
            programDetails.setText(getExerciseCountText(program));

            displayButton.setOnClickListener(v -> displayListener.onProgramClick(program));
            deleteButton.setOnClickListener(v -> deleteListener.onProgramClick(program));
        }

        private String getExerciseCountText(Program program) {
            int exerciseCount = program.getExercises() != null ? program.getExercises().size() : 0;
            return exerciseCount + " exercises";
        }
    }
}