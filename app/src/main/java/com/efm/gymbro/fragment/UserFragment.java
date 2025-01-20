package com.efm.gymbro.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.efm.gymbro.activity.ProgramDetailsActivity;
import com.efm.gymbro.R;
import com.efm.gymbro.activity.UserDetailsActivity;
import com.efm.gymbro.model.Program;
import com.efm.gymbro.model.Session;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView userNameText;
    private TextView userDetailsText;
    private MaterialButton editProfileButton;
    private RecyclerView recentProgramsRecyclerView;
    private RecyclerView recentSessionsRecyclerView;
    private MaterialCardView statsCard;
    private TextView totalWorkoutsText;
    private TextView totalExercisesText;
    private TextView averageTimeText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadUserData();

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    private void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        userNameText = view.findViewById(R.id.user_name_text);
        userDetailsText = view.findViewById(R.id.user_details_text);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        recentProgramsRecyclerView = view.findViewById(R.id.recent_programs_recycler_view);
        recentSessionsRecyclerView = view.findViewById(R.id.recent_sessions_recycler_view);
        statsCard = view.findViewById(R.id.stats_card);
        totalWorkoutsText = view.findViewById(R.id.total_workouts_text);
        totalExercisesText = view.findViewById(R.id.total_exercises_text);
        averageTimeText = view.findViewById(R.id.average_time_text);
    }

    private void setupRecyclerViews() {
        // Setup Recent Programs RecyclerView
        recentProgramsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Setup Recent Sessions RecyclerView
        recentSessionsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        swipeRefreshLayout.setRefreshing(true);

        // Load user profile
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        updateUserProfile(userData);
                    }
                });

        // Load recent programs
        db.collection("programs")
                .whereEqualTo("creatorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Program> recentPrograms = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Program program = doc.toObject(Program.class);
                        if (program != null) {
                            program.setId(doc.getId());
                            recentPrograms.add(program);
                        }
                    }
                    updateRecentPrograms(recentPrograms);
                });

        // Load recent sessions
        db.collection("sessions")
                .whereEqualTo("userId", userId)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Session> recentSessions = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Session session = doc.toObject(Session.class);
                        if (session != null) {
                            session.setId(doc.getId());
                            recentSessions.add(session);
                        }
                    }
                    updateRecentSessions(recentSessions);
                    swipeRefreshLayout.setRefreshing(false);
                });

        // Load user stats
        loadUserStats(userId);
    }

    private void updateUserProfile(Map<String, Object> userData) {
        String name = (String) userData.get("name");
        String bodyType = (String) userData.get("bodyType");
        String goal = (String) userData.get("goal");

        userNameText.setText(name);
        userDetailsText.setText(String.format("%s • %s", bodyType, goal));
    }

    private void updateRecentPrograms(List<Program> programs) {
        RecentProgramsAdapter adapter = new RecentProgramsAdapter(programs, program -> {
            Intent intent = new Intent(getContext(), ProgramDetailsActivity.class);
            intent.putExtra("program_id", program.getId());
            startActivity(intent);
        });
        recentProgramsRecyclerView.setAdapter(adapter);
    }

    private void updateRecentSessions(List<Session> sessions) {
        RecentSessionsAdapter adapter = new RecentSessionsAdapter(sessions);
        recentSessionsRecyclerView.setAdapter(adapter);
    }

    private void loadUserStats(String userId) {
        db.collection("sessions")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalWorkouts = querySnapshot.size();
                    int totalExercises = 0;
                    long totalDuration = 0;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Session session = doc.toObject(Session.class);
                        if (session != null) {
                            totalExercises += session.getExerciseSets().size();
                            long duration = session.getEndTime().getTime() -
                                    session.getStartTime().getTime();
                            totalDuration += duration;
                        }
                    }

                    double averageMinutes = totalWorkouts > 0 ?
                            (totalDuration / (1000.0 * 60 * totalWorkouts)) : 0;

                    totalWorkoutsText.setText(String.valueOf(totalWorkouts));
                    totalExercisesText.setText(String.valueOf(totalExercises));
                    averageTimeText.setText(String.format(Locale.getDefault(),
                            "%.0f min", averageMinutes));
                });
    }

    private void refreshData() {
        loadUserData();
    }

    // Inner class for Recent Programs Adapter
    private static class RecentProgramsAdapter extends
            RecyclerView.Adapter<RecentProgramsAdapter.ViewHolder> {

        private final List<Program> programs;
        private final OnProgramClickListener listener;

        interface OnProgramClickListener {
            void onProgramClick(Program program);
        }

        RecentProgramsAdapter(List<Program> programs, OnProgramClickListener listener) {
            this.programs = programs;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent_program, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Program program = programs.get(position);
            holder.bind(program, listener);
        }

        @Override
        public int getItemCount() {
            return programs.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView programName;
            private final TextView exerciseCount;

            ViewHolder(View itemView) {
                super(itemView);
                programName = itemView.findViewById(R.id.programName);
                exerciseCount = itemView.findViewById(R.id.exerciseCount);
            }

            void bind(Program program, OnProgramClickListener listener) {
                programName.setText(program.getName());
                int count = program.getExercises() != null ?
                        program.getExercises().size() : 0;
                exerciseCount.setText(String.format(Locale.getDefault(),
                        "%d exercises", count));

                itemView.setOnClickListener(v -> listener.onProgramClick(program));
            }
        }
    }

    // Inner class for Recent Sessions Adapter
    private static class RecentSessionsAdapter extends
            RecyclerView.Adapter<RecentSessionsAdapter.ViewHolder> {

        private final List<Session> sessions;
        private final SimpleDateFormat dateFormat =
                new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        RecentSessionsAdapter(List<Session> sessions) {
            this.sessions = sessions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent_session, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Session session = sessions.get(position);
            holder.bind(session, dateFormat);
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView sessionDate;
            private final TextView sessionDetails;

            ViewHolder(View itemView) {
                super(itemView);
                sessionDate = itemView.findViewById(R.id.session_date);
                sessionDetails = itemView.findViewById(R.id.session_details);
            }

            void bind(Session session, SimpleDateFormat dateFormat) {
                sessionDate.setText(dateFormat.format(session.getStartTime()));

                int totalSets = session.getExerciseSets() != null ?
                        session.getExerciseSets().size() : 0;
                long durationMinutes = (session.getEndTime().getTime() -
                        session.getStartTime().getTime()) / (1000 * 60);

                sessionDetails.setText(String.format(Locale.getDefault(),
                        "%d sets • %d min", totalSets, durationMinutes));
            }
        }
    }
}