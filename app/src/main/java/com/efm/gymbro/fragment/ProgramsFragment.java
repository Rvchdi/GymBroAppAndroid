package com.efm.gymbro.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.efm.gymbro.activity.CreateProgramActivity;
import com.efm.gymbro.adapter.ProgramAdapter;
import com.efm.gymbro.activity.ProgramDetailsActivity;
import com.efm.gymbro.R;
import com.efm.gymbro.model.Program;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ProgramsFragment extends Fragment {
    private View rootView;
    private View emptyStateView;
    private RecyclerView myProgramsRecyclerView;
    private RecyclerView featuredProgramsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton createProgramFab;
    private MaterialCardView createProgramCard;
    private MaterialCardView exploreProgramsCard;
    private ProgramAdapter programAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_programs, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();
        setupRecyclerViews();
        setupSwipeRefresh();
        setupClickListeners();

        // Load initial data
        loadUserPrograms();
    }

    private void initializeViews() {
        emptyStateView = rootView.findViewById(R.id.emptyStateView);
        myProgramsRecyclerView = rootView.findViewById(R.id.myProgramsRecyclerView);
        featuredProgramsRecyclerView = rootView.findViewById(R.id.featuredProgramsRecyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        createProgramFab = rootView.findViewById(R.id.addProgramFab);
        createProgramCard = rootView.findViewById(R.id.createProgramCard);
        exploreProgramsCard = rootView.findViewById(R.id.exploreProgramsCard);
    }

    private void setupRecyclerViews() {
        programAdapter = new ProgramAdapter(
                // Display listener
                this::showProgramDetails,
                // Delete listener
                this::deleteProgram,
                // Start program listener
                this::startProgram
        );
        myProgramsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        myProgramsRecyclerView.setAdapter(programAdapter);

        // Set up Featured Programs RecyclerView with horizontal layout
        LinearLayoutManager featuredLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        featuredProgramsRecyclerView.setLayoutManager(featuredLayoutManager);
        // TODO: Set up featured programs adapter
    }

    private void startProgram(Program program) {
        // Create new instance of SessionFragment using the factory method
        SessionFragment sessionFragment = SessionFragment.newInstance(program);

        // Replace current fragment with SessionFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sessionFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadUserPrograms);
    }

    private void setupClickListeners() {
        createProgramFab.setOnClickListener(v -> navigateToCreateProgram());
        createProgramCard.setOnClickListener(v -> navigateToCreateProgram());
        exploreProgramsCard.setOnClickListener(v -> navigateToExplorePrograms());
    }

    private void navigateToCreateProgram() {
        startActivity(new Intent(requireContext(), CreateProgramActivity.class));
    }

    private void navigateToExplorePrograms() {
        // TODO: Implement navigation to explore programs
        showError("Explore Programs feature coming soon!");
    }

    private void loadUserPrograms() {
        if (!isAdded()) return;

        String userId = mAuth.getCurrentUser().getUid();
        swipeRefreshLayout.setRefreshing(true);

        db.collection("programs")
                .whereEqualTo("creatorId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<Program> programs = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Program program = document.toObject(Program.class);
                        if (program != null) {
                            program.setId(document.getId());
                            programs.add(program);
                        }
                    }
                    updateUI(programs);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);
                    showError("Error loading programs: " + e.getMessage());
                });
    }

    private void deleteProgram(Program program) {
        if (program.getId() == null) return;

        db.collection("programs")
                .document(program.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    programAdapter.removeProgram(program);
                    showError("Program deleted successfully");
                    if (programAdapter.getItemCount() == 0) {
                        showEmptyState();
                    }
                })
                .addOnFailureListener(e -> showError("Error deleting program: " + e.getMessage()));
    }

    private void updateUI(List<Program> programs) {
        swipeRefreshLayout.setRefreshing(false);

        if (programs.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            programAdapter.updatePrograms(programs);
        }
    }

    private void showEmptyState() {
        if (emptyStateView != null && myProgramsRecyclerView != null) {
            emptyStateView.setVisibility(View.VISIBLE);
            myProgramsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (emptyStateView != null && myProgramsRecyclerView != null) {
            emptyStateView.setVisibility(View.GONE);
            myProgramsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showProgramDetails(Program program) {
        // TODO: Navigate to program details
        Intent intent = new Intent(requireContext(), ProgramDetailsActivity.class);
        intent.putExtra("program_id", program.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserPrograms(); // Refresh programs when returning to fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to views
        rootView = null;
        emptyStateView = null;
        myProgramsRecyclerView = null;
        featuredProgramsRecyclerView = null;
        swipeRefreshLayout = null;
        createProgramFab = null;
        createProgramCard = null;
        exploreProgramsCard = null;
        programAdapter = null;
    }
}