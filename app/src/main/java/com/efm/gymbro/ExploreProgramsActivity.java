package com.efm.gymbro;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

public class ExploreProgramsActivity extends AppCompatActivity {
    private RecyclerView programsRecyclerView;
    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private MaterialToolbar toolbar;

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
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        programsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // Set up your adapter here
        // programsRecyclerView.setAdapter(new ProgramsAdapter(programs));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submit
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search text change
                return true;
            }
        });
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Handle filter selection
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            // Show filter dialog
            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            // Show sort dialog
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}