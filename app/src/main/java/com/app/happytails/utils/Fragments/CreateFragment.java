package com.app.happytails.utils.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.happytails.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateFragment extends Fragment {

    private static final String TAG = "CreateFragment";
    private static final int PReqCode = 1;

    private static int REQUESTCODE = 1;

    private EditText description, dogName, dogAge, dogGender;
    private ImageView imageView, userProfileImage;
    private TextView userTv;
    private Uri imageUri;
    private Button createBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri);
                }
            });

    public CreateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickListener();
        loadUserData();
    }

    private void loadUserData() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            Uri photoUrl = currentUser.getPhotoUrl();

            userTv.setText(username != null ? username : "User");
            Glide.with(this)
                    .load(photoUrl != null ? photoUrl : R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .into(userProfileImage);
        } else {
            Log.e(TAG, "User is not authenticated.");
        }
    }

    private void clickListener() {
        imageView.setOnClickListener(v -> checkAndRequestPermission());
        createBtn.setOnClickListener(v -> uploadData());
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 and above
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the READ_EXTERNAL_STORAGE permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadData() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String base64Image = encodeImageToBase64(imageUri);
            savePostToFirestore(base64Image);
        } catch (IOException e) {
            Log.e(TAG, "Error encoding image", e);
            Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Uri imageUri) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imageUri));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void savePostToFirestore(String base64Image) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User is not authenticated.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("description", description.getText().toString());
        data.put("dogName", dogName.getText().toString());
        data.put("dogAge", dogAge.getText().toString());
        data.put("dogGender", dogGender.getText().toString());
        data.put("imageBase64", base64Image);
        data.put("timestamp", System.currentTimeMillis());
        data.put("userId", currentUser.getUid());
        data.put("username", currentUser.getDisplayName());

        String documentId = UUID.randomUUID().toString();

        db.collection("users")
                .document(currentUser.getUid())
                .collection("posts")
                .document(documentId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post saved successfully");
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving post", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void init(View view) {
        description = view.findViewById(R.id.descriptionED);
        userTv = view.findViewById(R.id.createNameTv);
        dogName = view.findViewById(R.id.dogName);
        dogAge = view.findViewById(R.id.dogAge);
        dogGender = view.findViewById(R.id.dogGender);
        imageView = view.findViewById(R.id.dogPic);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        createBtn = view.findViewById(R.id.postCreateBtn);
    }

    private void clearFields() {
        description.setText("");
        dogName.setText("");
        dogAge.setText("");
        dogGender.setText("");
        imageView.setImageResource(R.drawable.forgot_pass_ic);
    }
}
