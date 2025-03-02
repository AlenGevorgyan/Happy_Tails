package com.app.happytails.utils.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.activity.OnBackPressedCallback;

import com.app.happytails.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DogProfile extends Fragment {

    private TextView dogNameTv;
    private TextView dogDescriptionTv;
    private ProgressBar fundingProgress;
    private Button donateBtn;
    private ImageButton backBtn;
    private CircleImageView dogImage;
    private BottomNavigationView navigationView;
    private String creatorLink;
    private String dogId;
    private FirebaseFirestore db;
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productLinks = new ArrayList<>();

    private String clinicName, doctorName, lastVisitDate, diagnosis, gender;
    private long age;
    private ListenerRegistration dogListener;
    private ArrayList<String> galleryImageUrls, supporters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dog_profile, container, false);

        dogNameTv = view.findViewById(R.id.dogNameTV);
        dogDescriptionTv = view.findViewById(R.id.descriptionTV);
        fundingProgress = view.findViewById(R.id.funding_bar_profile);
        donateBtn = view.findViewById(R.id.donateBtn);
        dogImage = view.findViewById(R.id.dogProfileImage);
        navigationView = view.findViewById(R.id.dogBottomNavigation);
        backBtn = view.findViewById(R.id.dogBackBtn);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            dogId = getArguments().getString("dogId");
        }

        if (dogId != null) {
            loadDogData();
        } else {
            Toast.makeText(getContext(), "Dog ID is missing", Toast.LENGTH_SHORT).show();
        }

        donateBtn.setOnClickListener(v -> openDonationDialog());

        navigationView.setOnNavigationItemSelectedListener(this::handleNavigation);
        backBtn.setOnClickListener(v -> handleBackPress());

        return view;
    }

    private void loadDogData() {
        DocumentReference dogRef = db.collection("dogs").document(dogId);

        dogListener = dogRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String dogName = (String) snapshot.get("dogName");
                String dogDescription = (String) snapshot.get("description");
                age = (long) snapshot.get("dogAge");
                gender = (String) snapshot.get("dogGender");
                creatorLink = (String) snapshot.get("patreonUrl");
                ArrayList<String> productList = (ArrayList<String>) snapshot.get("productsList");
                String profileImageUrl = (String) snapshot.get("mainImage");
                long fundingPercentage = (long) snapshot.get("fundingPercentage");

                galleryImageUrls = (ArrayList<String>) snapshot.get("galleryImages");
                clinicName = (String) snapshot.get("clinicName");
                doctorName = (String) snapshot.get("doctorName");
                lastVisitDate = (String) snapshot.get("vetLastVisitDate");
                diagnosis = (String) snapshot.get("diagnosis");
                supporters = (ArrayList<String>) snapshot.get("supporters");

                // Set data to views
                dogNameTv.setText(dogName);
                dogDescriptionTv.setText(dogDescription);
                fundingProgress.setProgress((int) fundingPercentage);

                // Load dog profile image
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(getContext()).load(profileImageUrl).into(dogImage);
                }

                // Fetch product names and links
                if (productList != null) {
                    productNames.clear();
                    productLinks.clear();

                    for (String productUrl : productList) {
                        productNames.add(extractProductName(productUrl));
                        productLinks.add(productUrl);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Dog data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBackPress() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().onBackPressed();
        }
    }

    public static String extractProductName(String url) {
        String pattern = "/shop/([a-zA-Z\\-0-9]+)";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            String productName = matcher.group(1);
            return productName.replaceAll("[\\d\\-]", " ").replaceAll("\\b\\w{1,2}\\b", "").trim(); // Remove 1 or 2 character words too
        } else {
            return "Unknown Product";
        }
    }

    private void openDonationDialog() {
        if (productNames.isEmpty()) {
            Toast.makeText(getContext(), "No products available for donation", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Choose a Product to Donate")
                .setItems(productNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String donationUrl = productLinks.get(which);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(donationUrl));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dog_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dogListener != null) {
            dogListener.remove();
        }
    }
}
