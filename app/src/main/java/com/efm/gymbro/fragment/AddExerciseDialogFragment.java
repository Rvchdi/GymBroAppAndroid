package com.efm.gymbro.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.efm.gymbro.adapter.ExerciseAdapter;
import com.efm.gymbro.R;
import com.efm.gymbro.model.Exercise;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AddExerciseDialogFragment extends DialogFragment {
    private static final String TAG = "AddExerciseDialog";
    private OnExerciseSelectedListener listener;
    private List<Exercise> allExercises;
    private ExerciseAdapter adapter;
    private FirebaseFirestore db;
    private RecyclerView exerciseList;
    private View progressBar;

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(Exercise exercise);
    }

    public void setExerciseSelectedListener(OnExerciseSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and ProgressBar
        exerciseList = dialogView.findViewById(R.id.exercise_list);
        progressBar = dialogView.findViewById(R.id.progress_bar);
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize exercises list
        allExercises = new ArrayList<>();

        // Set up adapter with click listener
        adapter = new ExerciseAdapter(allExercises, (view, exercise) -> {
            if (listener != null) {
                listener.onExerciseSelected(exercise);
                dismiss();
            }
        });
        exerciseList.setAdapter(adapter);

        // Load all exercises from Firebase
        loadExercises();

        // Setup search functionality
        SearchView searchView = dialogView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterExercises(newText);
                return true;
            }
        });

        builder.setView(dialogView)
                .setTitle("Add Exercise")
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (exerciseList != null) {
            exerciseList.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void loadExercises() {
        setLoading(true);
        Log.d(TAG, "Starting to load exercises from Firestore");

        db.collection("exercises")
                .get()
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        allExercises.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "Document ID: " + document.getId());
                            try {
                                Exercise exercise = document.toObject(Exercise.class);
                                exercise.setId(document.getId());
                                allExercises.add(exercise);
                                Log.d(TAG, "Added exercise: " + exercise.getName());
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to Exercise: " + e.getMessage());
                                Log.e(TAG, "Document data: " + document.getData().toString());
                            }
                        }
                        Log.d(TAG, "Total exercises loaded: " + allExercises.size());
                        adapter.notifyDataSetChanged();

                        // Show message if no exercises found
                        if (allExercises.isEmpty()) {
                            // TODO: Show empty state view
                            Log.w(TAG, "No exercises found in database");
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Error loading exercises: " + (e != null ? e.getMessage() : "Unknown error"));
                        // Show error message to user
                        if (getContext() != null) {
                            // TODO: Show error state view
                        }
                    }
                });
    }

    private void filterExercises(String query) {
        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            if (exercise.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(exercise);
            }
        }
        adapter.updateList(filteredList);
    }
}