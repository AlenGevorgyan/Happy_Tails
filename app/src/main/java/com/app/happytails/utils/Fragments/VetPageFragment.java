package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.happytails.R;

public class VetPageFragment extends Fragment {

    private TextView vetName, vetDoctorName, vetLastVisitDate, vetDiagnosis;

    public static VetPageFragment newInstance(String name, String doctorName, String lastVisitDate, String diagnosis) {
        VetPageFragment fragment = new VetPageFragment();
        Bundle args = new Bundle();
        args.putString("vetClinicName", name);
        args.putString("vetDoctorName", doctorName);
        args.putString("vetLastVisitDate", lastVisitDate);
        args.putString("diagnosis", diagnosis);
        fragment.setArguments(args);
        return fragment;
    }

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

        loadVetInformation();
    }

    private void loadVetInformation() {
        if (getArguments() != null) {
            String name = getArguments().getString("vetClinicName");
            String doctorName = getArguments().getString("vetDoctorName");
            String lastVisitDate = getArguments().getString("vetLastVisitDate");
            String diagnosis = getArguments().getString("diagnosis");

            vetName.setText("Clinic Name: " + name);
            vetDoctorName.setText("Doctor: " + doctorName);
            vetLastVisitDate.setText("Last Visit Date: " + lastVisitDate);
            vetDiagnosis.setText("Diagnosis: " + diagnosis);
        }
    }
}