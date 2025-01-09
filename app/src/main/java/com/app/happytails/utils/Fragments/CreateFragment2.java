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

import java.util.ArrayList;

public class CreateFragment2 extends Fragment {

    private EditText dogName, dogAge, dogGender, descriptionED;
    private ImageView dogGalleryPic, dogPic;
    private Button nextButton;
    private RecyclerView recyclerView;
    private Uri mainImageUri;
    private final ArrayList<String> galleryUrls = new ArrayList<>();
    private GalleryAdapter galleryAdapter;

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

        dogName = view.findViewById(R.id.dogName);
        dogAge = view.findViewById(R.id.dogAge);
        dogGender = view.findViewById(R.id.dogGender);
        descriptionED = view.findViewById(R.id.descriptionED);
        dogPic = view.findViewById(R.id.mainProfileImage);
        dogGalleryPic = view.findViewById(R.id.dogPic);
        recyclerView = view.findViewById(R.id.dogGallery);
        nextButton = view.findViewById(R.id.postNextBtn);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter = new GalleryAdapter(getContext(), galleryUrls);
        recyclerView.setAdapter(galleryAdapter);

        // Handle button click to navigate
        nextButton.setOnClickListener(v -> handleNextButtonClick());

        // Handle image selection
        dogPic.setOnClickListener(v -> openMainImageGallery());
        dogGalleryPic.setOnClickListener(v -> openGalleryForDogImages());
    }

    private void handleNextButtonClick() {
        String name = dogName.getText().toString();
        String age = dogAge.getText().toString();
        String gender = dogGender.getText().toString();
        String description = descriptionED.getText().toString();

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("dogName", name);
        bundle.putString("dogAge", age);
        bundle.putString("dogGender", gender);
        bundle.putString("description", description);
        bundle.putString("mainImageUri", mainImageUri != null ? mainImageUri.toString() : null);
        bundle.putStringArrayList("galleryUrls", galleryUrls);

        CreateFragment createFragment = new CreateFragment();
        createFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, createFragment);
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
                // Handle single main image selection
                mainImageUri = data.getData();
                dogPic.setImageURI(mainImageUri);

            } else if (requestCode == 2) {
                // Handle multiple images for the gallery
                galleryUrls.clear();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        galleryUrls.add(imageUri.toString());
                    }
                } else if (data.getData() != null) {
                    galleryUrls.add(data.getData().toString());
                }

                // Notify the adapter that the data has changed
                galleryAdapter.notifyDataSetChanged();
            }
        }
    }
}