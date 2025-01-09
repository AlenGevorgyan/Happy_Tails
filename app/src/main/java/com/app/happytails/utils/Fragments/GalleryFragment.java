package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.GalleryAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private RecyclerView galleryRecyclerView;
    private ImageButton backBtn;
    private GalleryAdapter galleryAdapter;
    private List<String> imageUrls;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        galleryRecyclerView.setHasFixedSize(true);
        galleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        backBtn = view.findViewById(R.id.backToProfileFromGallery);

        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Load images from Firestore
        loadGalleryImages();
    }

    private void loadGalleryImages() {
        String currentUserId = getArguments() != null ? getArguments().getString("userId") : null;
        if (currentUserId == null) {
            Toast.makeText(getContext(), "Error loading gallery", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users_posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageUrls = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> postImageUrls = (List<String>) document.get("galleryImageUrls");
                                if (postImageUrls != null) {
                                    imageUrls.addAll(postImageUrls);
                                }
                            }
                        }

                        if (imageUrls.isEmpty()) {
                            Toast.makeText(getContext(), "No images to display", Toast.LENGTH_SHORT).show();
                        } else {
                            // Initialize and set the adapter
                            galleryAdapter = new GalleryAdapter(getContext(), imageUrls);
                            galleryRecyclerView.setAdapter(galleryAdapter);
                        }
                    } else {
                        Log.e("GalleryFragment", "Error getting documents: ", task.getException());
                        Toast.makeText(getContext(), "Error loading images", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}