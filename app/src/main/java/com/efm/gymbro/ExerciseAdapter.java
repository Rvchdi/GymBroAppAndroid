package com.efm.gymbro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.efm.gymbro.model.Exercise;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import java.util.Set;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private List<Exercise> exercises;
    private OnExerciseClickListener listener;
    private Set<Exercise> selectedExercises = new HashSet<>();  // Add this

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, boolean isSelected);  // Update interface
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.checkBox.setText(exercise.getName());
        holder.checkBox.setChecked(exercise.isSelected());
        holder.muscleText.setText(String.join(", ", exercise.getTargetMuscles()));
        holder.detailsText.setText(String.format("%s • %s • %dx%s",
                exercise.getType(), exercise.getLevel(), exercise.getSets(), exercise.getReps()));

        View.OnClickListener clickListener = v -> {
            boolean newState = !exercise.isSelected();
            exercise.setSelected(newState);
            holder.checkBox.setChecked(newState);
            if (newState) {
                selectedExercises.add(exercise);
            } else {
                selectedExercises.remove(exercise);
            }
            listener.onExerciseClick(exercise, newState);
        };

        holder.card.setOnClickListener(clickListener);
        holder.checkBox.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public List<Exercise> getSelectedExercises() {
        return new ArrayList<>(selectedExercises);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView muscleText;
        private TextView detailsText;
        private MaterialCardView card;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.exerciseCheckbox);
            muscleText = view.findViewById(R.id.muscleText);
            detailsText = view.findViewById(R.id.detailsText);
            card = view.findViewById(R.id.exerciseCard);
        }
    }
}