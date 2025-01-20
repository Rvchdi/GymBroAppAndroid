package com.efm.gymbro.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.R;
import com.efm.gymbro.adapter.SessionExerciseAdapter;
import com.efm.gymbro.model.Exercise;
import com.efm.gymbro.model.Program;
import com.efm.gymbro.model.Session;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SessionFragment extends Fragment {
    private static final String ARG_PROGRAM = "program";

    private RecyclerView exerciseList;
    private SessionExerciseAdapter adapter;
    private Button startButton, pauseButton, stopButton;
    private TextView timerText, programName;
    private FloatingActionButton addExerciseFab;
    private ExtendedFloatingActionButton saveSessionFab;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private boolean isTimerRunning = false;
    private long startTimeMillis = 0;
    private long elapsedTimeMillis = 0;
    private Program currentProgram;
    private List<Exercise> sessionExercises;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public static SessionFragment newInstance(Program program) {
        SessionFragment fragment = new SessionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROGRAM, program);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentProgram = getArguments().getParcelable(ARG_PROGRAM);
        }
        sessionExercises = new ArrayList<>();
        if (currentProgram != null && currentProgram.getExercises() != null) {
            sessionExercises.addAll(currentProgram.getExercises());
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        timerHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);
        initializeViews(view);
        setupRecyclerView();
        setupTimer();
        setupClickListeners();
        return view;
    }

    private void initializeViews(View view) {
        exerciseList = view.findViewById(R.id.session_exercise_list);
        startButton = view.findViewById(R.id.start_button);
        pauseButton = view.findViewById(R.id.pause_button);
        stopButton = view.findViewById(R.id.stop_button);
        timerText = view.findViewById(R.id.timer_text);
        programName = view.findViewById(R.id.program_name);
        addExerciseFab = view.findViewById(R.id.add_exercise_fab);
        saveSessionFab = view.findViewById(R.id.save_session_fab);

        if (currentProgram != null) {
            programName.setText(currentProgram.getName());
        }

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        saveSessionFab.setEnabled(false);
    }

    private void setupRecyclerView() {
        adapter = new SessionExerciseAdapter(sessionExercises, this::showExerciseDetails);
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseList.setAdapter(adapter);
    }

    private void setupTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    long currentTimeMillis = System.currentTimeMillis();
                    elapsedTimeMillis = currentTimeMillis - startTimeMillis;
                    updateTimerText();
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void setupClickListeners() {
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        stopButton.setOnClickListener(v -> stopSession());
        addExerciseFab.setOnClickListener(v -> showAddExerciseDialog());
        saveSessionFab.setOnClickListener(v -> saveSession());
    }

    private void startTimer() {
        if (!isTimerRunning) {
            if (startTimeMillis == 0) {
                startTimeMillis = System.currentTimeMillis() - elapsedTimeMillis;
            } else {
                startTimeMillis = System.currentTimeMillis() - elapsedTimeMillis;
            }

            isTimerRunning = true;
            timerHandler.post(timerRunnable);
            updateButtonStates(true);
        }
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            isTimerRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
            updateButtonStates(false);
        }
    }

    private void stopSession() {
        timerHandler.removeCallbacks(timerRunnable);
        isTimerRunning = false;
        saveSessionFab.setEnabled(true);
        updateButtonStates(false);
    }

    private void updateButtonStates(boolean isRunning) {
        startButton.setEnabled(!isRunning);
        pauseButton.setEnabled(isRunning);
        stopButton.setEnabled(isRunning || elapsedTimeMillis > 0);
        saveSessionFab.setEnabled(!isRunning && elapsedTimeMillis > 0);
    }

    private void updateTimerText() {
        int hours = (int) (elapsedTimeMillis / 3600000);
        int minutes = (int) ((elapsedTimeMillis % 3600000) / 60000);
        int seconds = (int) ((elapsedTimeMillis % 60000) / 1000);

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerText.setText(timeString);
    }

    private void saveSession() {
        if (currentProgram == null || startTimeMillis == 0) {
            showError("Cannot save session: Invalid session data");
            return;
        }

        Map<String, List<Session.ExerciseSet>> exerciseSets = adapter.getExerciseSets();
        if (exerciseSets.isEmpty()) {
            showError("Cannot save session: No exercises recorded");
            return;
        }

        Session session = new Session();
        session.setProgramId(currentProgram.getId());
        session.setUserId(auth.getCurrentUser().getUid());
        session.setStartTime(new Date(startTimeMillis));
        session.setEndTime(new Date(System.currentTimeMillis()));

        List<Session.ExerciseSet> allSets = new ArrayList<>();
        for (List<Session.ExerciseSet> sets : exerciseSets.values()) {
            allSets.addAll(sets);
        }
        session.setExerciseSets(allSets);

        saveSessionFab.setEnabled(false);
        db.collection("sessions")
                .add(session)
                .addOnSuccessListener(documentReference -> {
                    showSuccess("Session saved successfully");
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    showError("Failed to save session: " + e.getMessage());
                    saveSessionFab.setEnabled(true);
                });
    }

    private void showExerciseDetails(Exercise exercise) {
        ExerciseDetailsDialogFragment dialog = new ExerciseDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("exercise", exercise);
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "ExerciseDetails");
    }

    private void showAddExerciseDialog() {
        AddExerciseDialogFragment dialog = new AddExerciseDialogFragment();
        dialog.setExerciseSelectedListener(exercise -> {
            sessionExercises.add(exercise);
            adapter.notifyItemInserted(sessionExercises.size() - 1);
        });
        dialog.show(getParentFragmentManager(), "AddExercise");
    }

    private void showSuccess(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(requireContext().getColor(R.color.success_color))
                    .show();
        }
    }

    private void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTimerRunning) {
            pauseTimer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}