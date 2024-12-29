package com.app.happytails.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.happytails.R;
import com.app.happytails.utils.Fragments.ChatFragment;
import com.app.happytails.utils.Fragments.CreateFragment;
import com.app.happytails.utils.Fragments.HomeFragment;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageButton searchButton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    CreateFragment createFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove the default ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the default action bar
        }

        // Set Navigation Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.primary_color));
        }

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);  // Set your custom toolbar

        // Initialize Fragments
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();
        createFragment = new CreateFragment();

        // Initialize Views
        bottomNav = findViewById(R.id.bottomNavigation);
        searchButton = findViewById(R.id.searchIcon);

        // Set OnClickListener for Search Button
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        // Set Item Selection Listener for Bottom Navigation
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Replace the Fragment based on the selected menu item
                if(item.getItemId() == R.id.chatsMenu) {
                    toolbar.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                if(item.getItemId() == R.id.homeMenu) {
                    toolbar.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
                }
                if(item.getItemId() == R.id.profileMenu) {
                    toolbar.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                }
                if(item.getItemId() == R.id.createPostMenu) {
                    toolbar.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, createFragment).commit();
                }
                return true;
            }
        });

        // Set Default Selected Item
        bottomNav.setSelectedItemId(R.id.homeMenu);
    }
}
