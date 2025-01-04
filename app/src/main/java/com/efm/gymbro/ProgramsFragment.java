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
        programAdapter = new ProgramAdapter();
        myProgramsRecyclerView.setAdapter(programAdapter);
        loadUserPrograms();
    }

    private void loadUserPrograms() {
        db.collection("programs")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Program> programs = new ArrayList<>();
                    for(DocumentSnapshot doc : snapshot.getDocuments()) {
                        Program program = doc.toObject(Program.class);
                        if(program != null) {
                            programs.add(program);
                        }
                    }
                    programAdapter.setPrograms(programs);
                })
                .addOnFailureListener(e -> Log.e("Programs", "Error: ", e));
    }

    private void navigateToCreateProgram() {
        startActivity(new Intent(getContext(), CreateProgramActivity.class));
    }

    private void navigateToExplorePrograms() {
        startActivity(new Intent(getActivity(), ExploreProgramsActivity.class));
    }
}