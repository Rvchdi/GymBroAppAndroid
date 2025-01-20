package com.efm.gymbro.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.R;
import com.efm.gymbro.model.Exercise;
import com.efm.gymbro.model.Session.ExerciseSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionExerciseAdapter extends RecyclerView.Adapter<SessionExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;
    private Map<String, List<ExerciseSet>> exerciseSets;
    private OnExerciseClickListener clickListener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public SessionExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.clickListener = listener;
        this.exerciseSets = new HashMap<>();
        initializeExerciseSets();
    }

    private void initializeExerciseSets() {
        for (Exercise exercise : exercises) {
            List<ExerciseSet> sets = new ArrayList<>();
            for (int i = 0; i < exercise.getSets(); i++) {
                sets.add(new ExerciseSet(
                        exercise.getId(),
                        exercise.getName(),
                        i + 1,
                        0.0, // default weight
                        0    // default reps
                ));
            }
            exerciseSets.put(exercise.getId(), sets);
        }
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public Map<String, List<ExerciseSet>> getExerciseSets() {
        return exerciseSets;
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView detailsText;
        private ViewGroup setsContainer;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            detailsText = itemView.findViewById(R.id.exercise_details);
            setsContainer = itemView.findViewById(R.id.sets_container);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onExerciseClick(exercises.get(position));
                }
            });
        }

        void bind(Exercise exercise) {
            nameText.setText(exercise.getName());
            String details = exercise.getType() != null ? exercise.getType() + " • " : "";
            details += String.format("%d sets × %s", exercise.getSets(), exercise.getReps());
            detailsText.setText(details);

            // Create set inputs
            setsContainer.removeAllViews();
            List<ExerciseSet> sets = exerciseSets.get(exercise.getId());

            for (ExerciseSet set : sets) {
                View setView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_exercise_set, setsContainer, false);


                TextView setNumber = setView.findViewById(R.id.set_number_text);
                EditText weightInput = setView.findViewById(R.id.weight_input_edit);
                EditText repsInput = setView.findViewById(R.id.reps_input_edit);
                CheckBox completedCheckbox = setView.findViewById(R.id.set_completed_checkbox);

                setNumber.setText("Set " + set.getSetNumber());
                weightInput.setText(set.getWeight() > 0 ? String.valueOf(set.getWeight()) : "");
                repsInput.setText(set.getReps() > 0 ? String.valueOf(set.getReps()) : "");
                completedCheckbox.setChecked(set.isCompleted());

                // Weight input listener
                weightInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            set.setWeight(s.length() > 0 ? Double.parseDouble(s.toString()) : 0);
                        } catch (NumberFormatException e) {
                            weightInput.setText("");
                            set.setWeight(0);
                        }
                    }
                });

                // Reps input listener
                repsInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            set.setReps(s.length() > 0 ? Integer.parseInt(s.toString()) : 0);
                        } catch (NumberFormatException e) {
                            repsInput.setText("");
                            set.setReps(0);
                        }
                    }
                });

                // Completed checkbox listener
                completedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                        set.setCompleted(isChecked));

                setsContainer.addView(setView);
            }
        }
    }
}