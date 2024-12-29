package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.happytails.R;
import com.app.happytails.utils.Adapters.PostAdapter;
import com.app.happytails.utils.model.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private PostAdapter adapter;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private List<PostModel> list;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        list = new ArrayList<>();
        adapter = new PostAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadDatafromFirestore();
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadDatafromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts") // Assuming posts are stored under a collection called 'posts'
                .orderBy("timestamp") // You can order by timestamp if needed
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            list.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                PostModel post = snapshot.toObject(PostModel.class);
                                list.add(post);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
