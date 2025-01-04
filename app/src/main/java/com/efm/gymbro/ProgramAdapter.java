package com.efm.gymbro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.model.Program;

import java.util.ArrayList;
import java.util.List;

class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {
    private List<Program> programs = new ArrayList<>();

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_program, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
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

    static class ProgramViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView exerciseCountText;

        public ProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.programName);
            exerciseCountText = itemView.findViewById(R.id.exerciseCount);
        }

        void bind(Program program) {
            nameText.setText(program.getName());
            exerciseCountText.setText(program.getExercises().size() + " exercises");
        }
    }
}