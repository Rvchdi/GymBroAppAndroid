package com.efm.gymbro;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_programs) {
                    selectedFragment = new ProgramsFragment();
                } else if (itemId == R.id.navigation_nutrients) {
                    selectedFragment = new NutrientsFragment();
                } else if (itemId == R.id.navigation_user) {
                    selectedFragment = new UserFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            });

            // Set default fragment
            if (savedInstanceState == null) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_programs);
            }
        }catch(Exception E){
            Throwable e = null;
            Log.e("HomeActivity", "Error in onCreate: ", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}