package com.efm.gymbro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import com.efm.gymbro.model.Exercise;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private List<Exercise> exercises;
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
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

        holder.card.setOnClickListener(v -> {
            exercise.setSelected(!exercise.isSelected());
            holder.checkBox.setChecked(exercise.isSelected());
            listener.onExerciseClick(exercise);
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
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