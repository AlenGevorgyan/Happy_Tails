package com.app.happytails.utils;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.happytails.R;
import com.app.happytails.utils.Adapters.SearchUserAdapter;
import com.app.happytails.utils.Fragments.ChatFragment;
import com.app.happytails.utils.Fragments.HomeFragment;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private Toolbar searchToolbar;

    private ImageButton back_button;
    private SearchUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_user_EditText);
        back_button = findViewById(R.id.BacktoMain);
        recyclerView = findViewById(R.id.searchResult);
        searchToolbar = findViewById(R.id.search_toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = s.toString().trim();
                if (searchTerm.length() >= 3) {
                    setupSearchRecyclerView(searchTerm.toLowerCase());
                } else {
                    if (adapter != null) {
                        adapter.stopListening();
                        recyclerView.setAdapter(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .orderBy("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new SearchUserAdapter(options, this, fragmentManager);

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onProfileFragmentClosed() {
        toggleSearchToolbar(true);
    }

    public void toggleSearchToolbar(boolean isVisible) {
        if (searchToolbar != null) {
            searchToolbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
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
}