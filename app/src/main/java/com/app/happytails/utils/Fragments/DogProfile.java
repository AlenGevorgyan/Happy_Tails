package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.happytails.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DogProfile extends Fragment {

    private TextView dogNameTv;
    private TextView dogDescriptionTv;
    private ProgressBar fundingProgress;
    private Button followBtn;
    private ImageButton settingsBtn, backBtn;
    private CircleImageView dogImage;
    private BottomNavigationView navigationView;
    private FrameLayout frameLayout;

    private String userId;
    private String dogId, clinicName, doctorName, lastVisitDate, diagnosis, gender;
    private long age;
    private ListenerRegistration dogListener;
    private ArrayList<String> galleryImageUrls, supporters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_profile, container, false);

        // Initialize views
        dogNameTv = view.findViewById(R.id.dogNameTV);
        dogDescriptionTv = view.findViewById(R.id.descriptionTV);
        fundingProgress = view.findViewById(R.id.funding_bar_profile);
        followBtn = view.findViewById(R.id.followBtn);
        settingsBtn = view.findViewById(R.id.settingsBtn);
        backBtn = view.findViewById(R.id.dogBackBtn);
        navigationView = view.findViewById(R.id.dogBottomNavigation);
        dogImage = view.findViewById(R.id.dogProfileImage);
        frameLayout = view.findViewById(R.id.dog_fragment_container);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (getArguments() != null) {
            dogId = getArguments().getString("dogId");
        }

        if (dogId != null) {
            loadDogData();
        } else {
            Toast.makeText(getContext(), "Dog ID is missing", Toast.LENGTH_SHORT).show();
        }

        navigationView.setOnNavigationItemSelectedListener(this::handleNavigation);

        backBtn.setOnClickListener(v -> handleBackButton());

        navigationView.setSelectedItemId(R.id.galleryMenu);
        loadDefaultFragment();

        return view;
    }

    private void loadDogData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dogRef = db.collection("dogs").document(dogId);

        dogListener = dogRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Map<String, Object> data = snapshot.getData();
                if (data != null) {
                    String name = (String) data.get("dogName");
                    age = (long) data.get("dogAge");
                    gender = (String) data.get("dogGender");
                    String description = (String) data.get("description");
                    String creatorId = (String) data.get("creator");
                    String profileImageUrl = (String) data.get("mainImage");
                    long fundingPercentage = (long) data.get("fundingPercentage");
                    galleryImageUrls = (ArrayList<String>) data.get("galleryImages");
                    clinicName = (String) data.get("clinicName");
                    doctorName = (String) data.get("doctorName");
                    lastVisitDate = (String) data.get("vetLastVisitDate");
                    diagnosis = (String) data.get("diagnosis");
                    supporters = (ArrayList<String>) data.get("supporters");

                    dogNameTv.setText(name);
                    dogDescriptionTv.setText(description);
                    fundingProgress.setProgress((int) fundingPercentage);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(getContext()).load(profileImageUrl).into(dogImage);
                    }

                    if (userId.equals(creatorId)) {
                        followBtn.setVisibility(View.GONE);
                        settingsBtn.setVisibility(View.VISIBLE);
                    } else {
                        followBtn.setVisibility(View.VISIBLE);
                        settingsBtn.setVisibility(View.GONE);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Dog data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dogListener != null) {
            dogListener.remove();
        }
    }

    private boolean handleNavigation(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.galleryMenu) {
            fragment = new GalleryFragment();
            if (galleryImageUrls != null) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("galleryImageUrls", galleryImageUrls);
                fragment.setArguments(bundle);
            }
        } else if (itemId == R.id.vetMenu) {
            fragment = new VetPageFragment();
            if (gender != null && age != 0) {
                Bundle bundle = new Bundle();
                bundle.putString("vetName", clinicName);
                bundle.putString("docName", doctorName);
                bundle.putString("vetLastVisitDate", lastVisitDate);
                bundle.putString("diagnosis", diagnosis);
                bundle.putLong("dogAge", age);
                bundle.putString("dogGender", gender);
                fragment.setArguments(bundle);
            }
        } else if (itemId == R.id.supportersMenu) {
            fragment = new SupportersFragment();
            if (supporters != null && !supporters.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("dogId", dogId);
                fragment.setArguments(bundle);
            }
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void handleBackButton() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dog_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadDefaultFragment() {
        Fragment fragment = new GalleryFragment();
        if (galleryImageUrls != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("galleryImageUrls", galleryImageUrls);
            fragment.setArguments(bundle);
        }
        loadFragment(fragment);
    }
}