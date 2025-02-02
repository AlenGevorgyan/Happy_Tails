package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.SupportersAdapter;
import com.app.happytails.utils.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SupportersFragment extends Fragment {

    private RecyclerView supportersRecyclerView;
    private SupportersAdapter supportersAdapter;
    private List<UserModel> supportersList;

    public SupportersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supporters, container, false);

        supportersRecyclerView = view.findViewById(R.id.supporters_recycler_view);
        supportersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        supportersList = new ArrayList<>();
        supportersAdapter = new SupportersAdapter(getContext(), supportersList, getParentFragmentManager());
        supportersRecyclerView.setAdapter(supportersAdapter);

        loadSupporters();

        return view;
    }

    private void loadSupporters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String dogId = getArguments() != null ? getArguments().getString("dogId") : null;

        if (dogId != null) {
            db.collection("dogs").document(dogId).collection("supporters")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            supportersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel supporter = document.toObject(UserModel.class);
                                supportersList.add(supporter);
                            }
                            supportersAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error getting supporters", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Dog ID is missing", Toast.LENGTH_SHORT).show();
        }
    }
}