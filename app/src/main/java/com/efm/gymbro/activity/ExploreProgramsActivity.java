package com.efm.gymbro.activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExploreProgramsActivity extends AppCompatActivity {
    private RecyclerView programsRecyclerView;
    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private MaterialToolbar toolbar;
    private MuscleVisualizationView muscleView;
    private ChipGroup muscleChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_programs);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchView();
        setupFilterChips();
    }

    private void initializeViews() {
        programsRecyclerView = findViewById(R.id.programsRecyclerView);
        searchView = findViewById(R.id.searchView);
        filterChipGroup = findViewById(R.id.filterChipGroup);
        toolbar = findViewById(R.id.topAppBar);
        muscleView = findViewById(R.id.muscleVisualization);
        muscleChipGroup = findViewById(R.id.muscleChipGroup);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        programsRecyclerView.setLayoutManager(layoutManager);
        programsRecyclerView.setHasFixedSize(true);
        // Set up your adapter here
        // programsRecyclerView.setAdapter(new ProgramsAdapter(programs));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        // Implement search logic here
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Set<String> selectedFilters = new HashSet<>();
            for (int id : checkedIds) {
                Chip chip = group.findViewById(id);
                if (chip != null) {
                    selectedFilters.add(chip.getText().toString().toLowerCase());
                }
            }
            applyFilters(selectedFilters);
        });
    }

    private void applyFilters(Set<String> filters) {
        // Apply filters to your RecyclerView adapter
        if (!filters.isEmpty()) {
            for (String filter : filters) {
                updateMuscleVisualization(filter);
            }
        }
    }

    private void updateMuscleVisualization(String programType) {
        Set<String> activeGroups = new HashSet<>();

        switch (programType.toLowerCase()) {
            case "strength":
                activeGroups.addAll(Arrays.asList("shoulders", "chest", "biceps", "back", "quads"));
                break;
            case "cardio":
                activeGroups.addAll(Arrays.asList("quads", "calves"));
                break;
            case "upperbody":
                activeGroups.addAll(Arrays.asList("shoulders", "chest", "biceps", "back"));
                break;
            case "lowerbody":
                activeGroups.addAll(Arrays.asList("quads", "calves"));
                break;
            case "core":
                activeGroups.addAll(Arrays.asList("abs", "back"));
                break;
            case "beginner":
            case "intermediate":
            case "advanced":
                // Handle difficulty levels differently if needed
                break;
        }

        if (!activeGroups.isEmpty()) {
            muscleView.setActiveGroups(activeGroups);
            updateMuscleChips(activeGroups);
        }
    }

    private void updateMuscleChips(Set<String> activeGroups) {
        muscleChipGroup.removeAllViews();

        for (String muscle : activeGroups) {
            Chip chip = new Chip(this);
            chip.setText(capitalizeFirstLetter(muscle));
            chip.setClickable(false);
            chip.setChipBackgroundColorResource(R.color.red_100);
            chip.setTextColor(ContextCompat.getColor(this, R.color.red_700));
            chip.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Chip);
            muscleChipGroup.addView(chip);
        }
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_filter) {
            // Show filter dialog
            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            // Show sort dialog
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}