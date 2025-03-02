package com.app.happytails.utils;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.happytails.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthRedirectActivity extends AppCompatActivity {

    // Replace these with your actual values
    private static final String CLIENT_ID = "YOUR_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
    private static final String REDIRECT_URI = "http://localhost:3000";  // Use the same redirect URI as in OAuth setup
    private static final String GRANT_TYPE = "authorization_code";  // This will always be "authorization_code" when exchanging the code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_redirect);

        // Extract the authorization code from the incoming intent
        String authCode = getIntent().getStringExtra("auth_code");

        if (authCode != null) {
            exchangeAuthorizationCodeForToken(authCode);
        }
    }

    // This method handles the exchange of the authorization code for an access token
    private void exchangeAuthorizationCodeForToken(String code) {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.patreon.com/")  // Base URL of the Patreon API
                .addConverterFactory(GsonConverterFactory.create())  // Use Gson for JSON parsing
                .build();

        // Create the API service
        PatreonApiService apiService = retrofit.create(PatreonApiService.class);

        // Make the API call to exchange the code for a token
        apiService.exchangeToken(CLIENT_ID, CLIENT_SECRET, code, REDIRECT_URI, GRANT_TYPE).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        // Save the access token and refresh token in SharedPreferences
                        saveTokens(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
                        Toast.makeText(OAuthRedirectActivity.this, "Token exchanged successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error
                    Toast.makeText(OAuthRedirectActivity.this, "Failed to exchange token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // Handle failure
                Toast.makeText(OAuthRedirectActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save the tokens to SharedPreferences
    private void saveTokens(String accessToken, String refreshToken) {
        getSharedPreferences("OAuth", MODE_PRIVATE)
                .edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply();
    }
}
