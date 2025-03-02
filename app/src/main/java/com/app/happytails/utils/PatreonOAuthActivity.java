package com.app.happytails.utils;// PatreonOAuthActivity.java

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.app.happytails.R;

public class PatreonOAuthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "b52EdnyzuTSeiwP8LfcSh3TEjnkshMfQpzeV8QewHNyzBOM6OObEn34E_Dfr2nCa";
    private static final String REDIRECT_URI = "http://localhost:3000";
    private static final String AUTH_URL = "https://www.patreon.com/oauth2/authorize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        // Construct the OAuth URL with required parameters
        Uri authUri = Uri.parse(AUTH_URL)
                .buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", "identity")
                .build();

        // Launch the OAuth Authorization page in a browser
        Intent intent = new Intent(Intent.ACTION_VIEW, authUri);
        startActivity(intent);
    }
}
