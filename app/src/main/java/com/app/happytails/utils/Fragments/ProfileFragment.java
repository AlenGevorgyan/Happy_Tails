package com.app.happytails.utils.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.PostAdapter;
import com.app.happytails.utils.model.PostModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView profile_toolbarName, statusTv, followingCountTv, postCountTv, username;
    private CircleImageView profileImage;
    private RecyclerView recyclerView;
    private ImageButton settingsBtn;
    private Button followBtn;
    private FirebaseUser user;
    private PostAdapter postAdapter;
    private String profileUid;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loadBasicData();
        setupRecyclerView();
        loadUserPosts();
    }

    private void init(View view) {
        profile_toolbarName = view.findViewById(R.id.UserName);
        statusTv = view.findViewById(R.id.statusTV);
        followingCountTv = view.findViewById(R.id.folower_countTv);
        postCountTv = view.findViewById(R.id.post_counttv);
        profileImage = view.findViewById(R.id.profileImage);
        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        username = view.findViewById(R.id.nameTV);
        followBtn = view.findViewById(R.id.subscribeBtn);
        settingsBtn = view.findViewById(R.id.settingsIcon);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        profileUid = getArguments() != null ? getArguments().getString("profileUid") : user.getUid();
    }

    private void loadBasicData() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(profileUid);
        userRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error loading profile data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading profile data", error);
                return;
            }

            if (value != null && value.exists()) {
                String name = value.getString("username");
                Long followers = value.getLong("followers");
                Long posts = value.getLong("posts");
                String profileURL = value.getString("userImage");
                String status = value.getString("status");

                username.setText(name);
                profile_toolbarName.setText(name);
                statusTv.setText(status != null ? status : "No status");
                followingCountTv.setText(String.valueOf(followers != null ? followers : 0));
                postCountTv.setText(String.valueOf(posts != null ? posts : 0));

                if (profileURL != null && !profileURL.isEmpty()) {
                    Log.d(TAG, "Profile image URL: " + profileURL);
                    Glide.with(getContext())
                            .load(profileURL)
                            .placeholder(R.drawable.user_icon)
                            .timeout(6500)
                            .error(R.drawable.user_icon)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.user_icon);
                }

                if (profileUid.equals(user.getUid())) {
                    followBtn.setVisibility(View.GONE);
                    settingsBtn.setVisibility(View.VISIBLE);
                } else {
                    followBtn.setVisibility(View.VISIBLE);
                    settingsBtn.setVisibility(View.GONE);
                }
            } else {
                Log.d(TAG, "No profile data found");
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadUserPosts() {
        Query query = FirebaseFirestore.getInstance()
                .collection("users_posts")
                .whereEqualTo("userId", profileUid);

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        postAdapter = new PostAdapter(options, getContext());

        recyclerView.setAdapter(postAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (postAdapter != null) {
            postAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postAdapter != null) {
            postAdapter.stopListening();
        }
    }
}