package com.efm.gymbro;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.efm.gymbro.model.Exercise;

public class ExerciseDetailsDialogFragment extends DialogFragment {
    private Exercise exercise;

    public static ExerciseDetailsDialogFragment newInstance(Exercise exercise) {
        ExerciseDetailsDialogFragment fragment = new ExerciseDetailsDialogFragment();
        Bundle args = new Bundle();
        // You might need to make Exercise implement Parcelable or Serializable
        args.putParcelable("exercise", (Parcelable) exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exercise = getArguments().getParcelable("exercise");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exercise_details, null);

        // Initialize views
        TextView nameTextView = view.findViewById(R.id.exerciseNameTextView);
        TextView typeTextView = view.findViewById(R.id.exerciseTypeTextView);
        TextView levelTextView = view.findViewById(R.id.exerciseLevelTextView);
        TextView categoryTextView = view.findViewById(R.id.exerciseCategoryTextView);
        TextView equipmentTextView = view.findViewById(R.id.exerciseEquipmentTextView);
        TextView musclesTextView = view.findViewById(R.id.exerciseMusclesTextView);
        TextView setsRepsTextView = view.findViewById(R.id.exerciseSetsRepsTextView);

        // Set exercise details
        nameTextView.setText(exercise.getName());
        typeTextView.setText(exercise.getType());
        levelTextView.setText(exercise.getLevel());
        categoryTextView.setText(exercise.getCategory());
        equipmentTextView.setText(TextUtils.join(", ", exercise.getEquipment()));
        musclesTextView.setText(TextUtils.join(", ", exercise.getTargetMuscles()));
        setsRepsTextView.setText(String.format("%d sets × %s", exercise.getSets(), exercise.getReps()));

        builder.setView(view)
                .setPositiveButton("Close", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}