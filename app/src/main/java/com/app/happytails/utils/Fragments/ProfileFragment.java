package com.app.happytails.utils.Fragments;

import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.PostAdapter;
import com.app.happytails.utils.AndroidUtil;
import com.app.happytails.utils.ChatActivity;
import com.app.happytails.utils.model.PostModel;
import com.app.happytails.utils.model.UserModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView statusTv, followingCountTv, postCountTv, username;
    private CircleImageView profileImage;
    private RecyclerView recyclerView;
    private ImageButton settingsBtn, back_profile, chatBtn;
    private Button followBtn;
    private FirebaseUser currentUser;
    private PostAdapter postAdapter;
    private String profileUid;
    private String currentUserId;

    public interface OnFragmentInteractionListener {
        void onProfileFragmentClosed();
    }

    private OnFragmentInteractionListener listener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        init(view);
        loadBasicData();
        setupRecyclerView();
        loadUserPosts();
    }

    private void init(View view) {
        statusTv = view.findViewById(R.id.statusTV);
        followingCountTv = view.findViewById(R.id.folower_countTv);
        postCountTv = view.findViewById(R.id.post_counttv);
        profileImage = view.findViewById(R.id.profileImage);
        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        username = view.findViewById(R.id.nameTV);
        followBtn = view.findViewById(R.id.subscribeBtn);
        settingsBtn = view.findViewById(R.id.settings_profile);
        back_profile = view.findViewById(R.id.back_profile);
        chatBtn = view.findViewById(R.id.chatBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;
        profileUid = getArguments() != null ? getArguments().getString("creator") : currentUserId;

        if (profileUid != null && profileUid.equals(currentUserId)) {
            followBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
            settingsBtn.setVisibility(View.VISIBLE);
        } else {
            followBtn.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.VISIBLE);
        }

        back_profile.setOnClickListener(v -> handleBackPress());

        settingsBtn.setOnClickListener(v -> openSettingsFragment());

        chatBtn.setOnClickListener(v -> navigateToTheChat());
    }

    private void navigateToTheChat(){
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(profileUid);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    UserModel model = task.getResult().toObject(UserModel.class);
                    if (model != null) {
                        AndroidUtil.passUserModelAsIntent(intent, model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "User model not found");
                    }
                } else {
                    Log.e(TAG, "Failed to load user data for chat");
                }
            });
        }
    }

    private void handleBackPress() {
        if (listener != null) {
            listener.onProfileFragmentClosed();
        }
        if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void openSettingsFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserSettingsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadBasicData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(profileUid);

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

                username.setText(name != null ? name : "Unknown");
                statusTv.setText(status != null ? status : "No status");
                followingCountTv.setText(String.valueOf(followers != null ? followers : 0));
                postCountTv.setText(String.valueOf(posts != null ? posts : 0));

                if (profileURL != null && !profileURL.isEmpty()) {
                    Glide.with(requireContext())
                            .load(profileURL)
                            .placeholder(R.drawable.user_icon)
                            .error(R.drawable.user_icon)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.user_icon);
                }
            } else {
                Log.d(TAG, "No profile data found for UID: " + profileUid);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadUserPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("dogs")
                .whereEqualTo("creator", profileUid);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PostModel> postList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        PostModel post = document.toObject(PostModel.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                }
                postAdapter = new PostAdapter(requireContext(), postList);
                recyclerView.setAdapter(postAdapter);
            } else {
                Toast.makeText(getContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading posts", task.getException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (postAdapter != null) {
            postAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
