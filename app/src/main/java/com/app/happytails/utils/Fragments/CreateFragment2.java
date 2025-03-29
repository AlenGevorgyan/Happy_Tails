package com.app.happytails.utils.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.GalleryAdapter;
import com.app.happytails.utils.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreateFragment2 extends Fragment {

    private EditText dogName, descriptionED;
    private ImageView dogGalleryPic, dogPic;
    private Button nextButton;
    private RecyclerView recyclerView;
    private Uri mainImageUri;
    private final ArrayList<Uri> galleryUris = new ArrayList<>();
    private GalleryAdapter galleryAdapter;
    private SeekBar urgencySeekBar;
    private TextView urgencyLevelValue;
    private int urgencyLevel = 0;

    public CreateFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dogName = view.findViewById(R.id.dog_name);
        descriptionED = view.findViewById(R.id.dog_description);
        dogPic = view.findViewById(R.id.mainProfileImage);
        dogGalleryPic = view.findViewById(R.id.dogPic);
        recyclerView = view.findViewById(R.id.dogGallery);
        nextButton = view.findViewById(R.id.postNextBtn);
        urgencySeekBar = view.findViewById(R.id.urgencySeekBar);
        urgencyLevelValue = view.findViewById(R.id.urgencyLevelValue);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter = new GalleryAdapter(getContext(), galleryUris);
        recyclerView.setAdapter(galleryAdapter);

        // Handle button click to navigate
        nextButton.setOnClickListener(v -> handleNextButtonClick());

        // Handle image selection
        dogPic.setOnClickListener(v -> openMainImageGallery());
        dogGalleryPic.setOnClickListener(v -> openGalleryForDogImages());

        // Handle urgency level changes
        urgencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                urgencyLevel = progress;
                urgencyLevelValue.setText("Urgency Level: " + urgencyLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void handleNextButtonClick() {
        String name = dogName.getText().toString();
        String description = descriptionED.getText().toString();
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new dog document in Firestore
        createDogInFirestore(creatorId, name, description, urgencyLevel);
    }

    private void createDogInFirestore(String creatorId, String name, String description, int urgencyLevel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String dogId = db.collection("dogs").document().getId();
        ArrayList<String> galleryImages = new ArrayList<>();
        for (Uri uri : galleryUris) {
            galleryImages.add(uri.toString());
        }

        HomeModel dog = new HomeModel(creatorId, dogId, name, 0, galleryImages, mainImageUri != null ? mainImageUri.toString() : "", new ArrayList<>(), 0.0, new ArrayList<>(), urgencyLevel);

        db.collection("dogs").document(dogId).set(dog)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Dog account created successfully!", Toast.LENGTH_SHORT).show();
                    navigateToHomeFragment();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create dog account", Toast.LENGTH_SHORT).show());
    }

    private void navigateToHomeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openMainImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void openGalleryForDogImages() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            if (requestCode == 1) {
                mainImageUri = data.getData();
                dogPic.setImageURI(mainImageUri);

            } else if (requestCode == 2) {
                galleryUris.clear();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        galleryUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    galleryUris.add(data.getData());
                }

                galleryAdapter.notifyDataSetChanged();
            }
        }
    }
}