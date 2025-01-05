package com.efm.gymbro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.model.Exercise;

import java.util.List;

public class ProgramExerciseAdapter extends RecyclerView.Adapter<ProgramExerciseAdapter.ViewHolder> {
    private final List<Exercise> exercises;

    public ProgramExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_program_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView exerciseName;
        private final TextView exerciseDetails;
        private final TextView setsReps;
        private final TextView muscles;

        ViewHolder(View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDetails = itemView.findViewById(R.id.exerciseDetails);
            setsReps = itemView.findViewById(R.id.setsReps);
            muscles = itemView.findViewById(R.id.muscles);
        }

        void bind(Exercise exercise) {
            exerciseName.setText(exercise.getName());
            exerciseDetails.setText(String.format("%s • %s", exercise.getType(), exercise.getLevel()));
            setsReps.setText(String.format("%d sets × %s", exercise.getSets(), exercise.getReps()));
            muscles.setText(String.join(", ", exercise.getTargetMuscles()));
        }
    }
}