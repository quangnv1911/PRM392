package com.example.pmg302_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pmg302_project.Fragments.Customer.CustomerListFragment;

public class ManageCustomerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customer); // Replace with your actual layout file

        // Check if the savedInstanceState is null to avoid replacing the fragment on configuration changes
        if (savedInstanceState == null) {
            // Load the CustomerListFragment when the activity starts
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CustomerListFragment())
                    .commit();
        }
    }
}