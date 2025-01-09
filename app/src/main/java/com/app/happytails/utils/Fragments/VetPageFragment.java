package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.happytails.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class VetPageFragment extends Fragment {

    private TextView vetName, vetDoctorName, vetLastVisitDate, vetDiagnosis;
    private CircleImageView vetImage;
    private ImageView vetPicture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vet_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vetName = view.findViewById(R.id.vetName);
        vetDoctorName = view.findViewById(R.id.vetDoctorName);
        vetLastVisitDate = view.findViewById(R.id.vet_last_visit_date);
        vetDiagnosis = view.findViewById(R.id.vetDiagnosis);
        vetImage = view.findViewById(R.id.vetImage);
        vetPicture = view.findViewById(R.id.vet_picture);

        loadVetInformation();
    }

    private void loadVetInformation() {
        String profileUid = getArguments().getString("profileUid");
        DocumentReference vetRef = FirebaseFirestore.getInstance().collection("users_posts").document(profileUid);
        vetRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Error loading vet information", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null && value.exists()) {
                    String name = value.getString("vetClinicName");
                    String doctorName = value.getString("vetDoctorName");
                    String lastVisitDate = value.getString("vetLastVisitDate");
                    String diagnosis = value.getString("diagnosis");
                    String imageUrl = value.getString("vetImage");
                    String pictureUrl = value.getString("vetPicture");

                    vetName.setText(name);
                    vetDoctorName.setText(doctorName);
                    vetLastVisitDate.setText(lastVisitDate);
                    vetDiagnosis.setText(diagnosis);

                    Glide.with(getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.user_icon)
                            .timeout(6500)
                            .into(vetImage);

                    Glide.with(getContext())
                            .load(pictureUrl)
                            .placeholder(R.drawable.user_icon)
                            .timeout(6500)
                            .into(vetPicture);
                }
            }
        });
    }
}