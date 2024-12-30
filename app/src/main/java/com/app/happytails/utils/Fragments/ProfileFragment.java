package com.app.happytails.utils.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.app.happytails.R;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.Timestamp;

import android.util.Base64;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment {

    private TextView nameTV, profile_toolbarName, statusTv, followingCountTv, postCountTv;
    private CircleImageView profileImage;
    private ImageButton followRing, settingsBtn;
    private RecyclerView recyclerView;

    private FirebaseUser user;
    private FirestoreRecyclerAdapter<PostModel, PostHolder> postAdapter;
    private String profileUid;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loadBasicData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Adjust the number of columns as needed
        loadUserPosts();
    }

    private void init(View view) {
        nameTV = view.findViewById(R.id.nameTV);
        profile_toolbarName = view.findViewById(R.id.UserName);
        statusTv = view.findViewById(R.id.statusTV);
        followingCountTv = view.findViewById(R.id.folower_countTv);
        postCountTv = view.findViewById(R.id.post_counttv);
        profileImage = view.findViewById(R.id.profileImage);
        followRing = view.findViewById(R.id.subscribeBtn);
        settingsBtn = view.findViewById(R.id.settingsIcon);
        recyclerView = view.findViewById(R.id.recyclerViewProfile);  // RecyclerView for displaying posts

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        profileUid = getArguments() != null ? getArguments().getString("profileUid") : user.getUid(); // Check if it's the current user's profile
    }

    private void loadBasicData() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(profileUid);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value != null && value.exists()) {
                    String name = value.getString("username");
                    Long followers = value.getLong("followers");
                    Long posts = value.getLong("posts");
                    String profileURL = value.getString("userImage");
                    String status = value.getString("status");

                    // Set the profile data
                    nameTV.setText(name);
                    profile_toolbarName.setText(name);
                    statusTv.setText(status != null ? status : "No status available");
                    followingCountTv.setText(String.valueOf(followers != null ? followers : 0));
                    postCountTv.setText(String.valueOf(posts != null ? posts : 0));

                    // Use Glide to load the profile image
                    Glide.with(getContext())
                            .load(profileURL)
                            .placeholder(R.drawable.user_icon)
                            .timeout(6500)
                            .into(profileImage);

                    // Hide the follow button for the current user's profile and show settings button
                    if (profileUid.equals(user.getUid())) {
                        followRing.setVisibility(View.GONE);
                        settingsBtn.setVisibility(View.VISIBLE);
                    } else {
                        followRing.setVisibility(View.VISIBLE);
                        settingsBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadUserPosts() {
        // Fetch posts from Firestore
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(profileUid);
        Query query = reference.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING); // Sort by timestamp
        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class).build();

        postAdapter = new FirestoreRecyclerAdapter<PostModel, PostHolder>(options) {
            @NonNull
            @Override
            public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
                return new PostHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostModel model) {
                // Load image using Glide (image URL from Firestore)
                Glide.with(holder.itemView.getContext())
                        .load(model.getImageUrl())
                        .placeholder(R.drawable.forgot_pass_ic)
                        .into(holder.imageView);

                // Format and display timestamp
                Timestamp timestamp = model.getTimestamp();
                if (timestamp != null) {
                    Date date = timestamp.toDate();  // Convert Timestamp to Date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = sdf.format(date);
                    holder.timestampTv.setText(formattedDate);  // Set formatted timestamp to TextView
                }
            }

        };

        recyclerView.setAdapter(postAdapter);
    }

    private static class PostHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView timestampTv;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.dogPic);
            timestampTv = itemView.findViewById(R.id.timeTv);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postAdapter != null) {
            postAdapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (postAdapter != null) {
            postAdapter.startListening();
        }
    }
}
