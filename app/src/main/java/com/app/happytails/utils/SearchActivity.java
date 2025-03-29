package com.app.happytails.utils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Adapters.SearchUserAdapter;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.SearchModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    private EditText searchInput;
    private ImageButton searchButton;
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private Button btnSearchPerson, btnSearchDog;

    private SearchUserAdapter adapter;
    private FragmentManager fragmentManager;
    private boolean isSearchPerson = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        btnSearchPerson = findViewById(R.id.btn_search_person);
        btnSearchDog = findViewById(R.id.btn_search_dog);
        fragmentManager = getSupportFragmentManager();

        searchInput.requestFocus();

        backButton.setOnClickListener(v -> onBackPressed());

        btnSearchPerson.setOnClickListener(v -> {
            isSearchPerson = true;
            btnSearchPerson.setTextColor(getResources().getColor(R.color.primary_color));
            btnSearchDog.setTextColor(getResources().getColor(R.color.gray));
            searchInput.setHint("Username");
        });

        btnSearchDog.setOnClickListener(v -> {
            isSearchPerson = false;
            btnSearchDog.setTextColor(getResources().getColor(R.color.primary_color));
            btnSearchPerson.setTextColor(getResources().getColor(R.color.gray));
            searchInput.setHint("Dog Name");
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid search term");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        Query query;
        if (isSearchPerson) {
            query = FirebaseUtil.allUserCollectionReference()
                    .whereGreaterThanOrEqualTo("username", searchTerm)
                    .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');
        } else {
            query = FirebaseFirestore.getInstance().collection("dogs")
                    .whereGreaterThanOrEqualTo("dogName", searchTerm)
                    .whereLessThanOrEqualTo("dogName", searchTerm + '\uf8ff');
        }

        FirestoreRecyclerOptions<SearchModel> options = new FirestoreRecyclerOptions.Builder<SearchModel>()
                .setQuery(query, SearchModel.class)
                .build();

        adapter = new SearchUserAdapter(options, this, fragmentManager, isSearchPerson);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onProfileFragmentClosed() {
        // Handle interaction with the fragment if needed
    }
}