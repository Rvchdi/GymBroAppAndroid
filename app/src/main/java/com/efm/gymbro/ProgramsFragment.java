package com.efm.gymbro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.model.Program;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProgramsFragment extends Fragment {
    private RecyclerView myProgramsRecyclerView;
    private RecyclerView featuredProgramsRecyclerView;
    private ProgramAdapter programAdapter;
    private FirebaseFirestore db;
    private String currentUserId;
    private MaterialCardView createProgramCard;
    private MaterialCardView exploreProgramsCard;
    private ExtendedFloatingActionButton createProgramFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserPrograms();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programs, container, false);
        initializeViews(view);
        setupClickListeners();
        setupRecyclerViews();
        return view;
    }

    private void initializeViews(View view) {
        myProgramsRecyclerView = view.findViewById(R.id.myProgramsRecyclerView);
        featuredProgramsRecyclerView = view.findViewById(R.id.featuredProgramsRecyclerView);
        createProgramCard = view.findViewById(R.id.createProgramCard);
        exploreProgramsCard = view.findViewById(R.id.exploreProgramsCard);
        createProgramFab = view.findViewById(R.id.createProgramFab);
    }

    private void setupClickListeners() {
        createProgramCard.setOnClickListener(v -> navigateToCreateProgram());
        exploreProgramsCard.setOnClickListener(v -> navigateToExplorePrograms());
        createProgramFab.setOnClickListener(v -> navigateToCreateProgram());
    }

    private void setupRecyclerViews() {
        myProgramsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        programAdapter = new ProgramAdapter(
                program -> navigateToProgramDetails(program),
                program -> confirmAndDeleteProgram(program)
        );
        myProgramsRecyclerView.setAdapter(programAdapter);
        loadUserPrograms();
    }

    private void loadUserPrograms() {
        Log.d("Programs", "Loading programs for user: " + currentUserId);
        db.collection("programs")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Program> programs = new ArrayList<>();
                    Log.d("Programs", "Found " + snapshot.size() + " programs");

                    for(DocumentSnapshot doc : snapshot.getDocuments()) {
                        try {
                            Program program = doc.toObject(Program.class);
                            if(program != null) {
                                program.setId(doc.getId());
                                programs.add(program);
                                Log.d("Programs", "Added program: " + program.getName());
                            }
                        } catch (Exception e) {
                            Log.e("Programs", "Error converting document: " + doc.getId(), e);
                        }
                    }

                    if (programs.isEmpty()) {
                        Log.d("Programs", "No programs found");
                        // Optional: Show empty state
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }

                    programAdapter.setPrograms(programs);
                    programAdapter.notifyDataSetChanged(); // Force refresh
                })
                .addOnFailureListener(e -> {
                    Log.e("Programs", "Error loading programs: ", e);
                    showError("Failed to load programs");
                });
    }

    private void showEmptyState(boolean show) {
        // Add a TextView in your layout for empty state
        View emptyView = getView().findViewById(R.id.emptyStateView);
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
            myProgramsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void navigateToProgramDetails(Program program) {
        Intent intent = new Intent(getContext(), ProgramDetailsActivity.class);
        intent.putExtra("program_id", program.getId());
        startActivity(intent);
    }

    private void confirmAndDeleteProgram(Program program) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Program")
                .setMessage("Are you sure you want to delete this program?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProgram(program))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProgram(Program program) {
        db.collection("programs")
                .document(program.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    programAdapter.removeProgram(program);
                    showSuccess("Program deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Programs", "Error deleting program: ", e);
                    showError("Failed to delete program");
                });
    }

    private void navigateToCreateProgram() {
        startActivity(new Intent(getContext(), CreateProgramActivity.class));
    }

    private void navigateToExplorePrograms() {
        startActivity(new Intent(getActivity(), ExploreProgramsActivity.class));
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}