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

public class OAuthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "b52EdnyzuTSeiwP8LfcSh3TEjnkshMfQpzeV8QewHNyzBOM6OObEn34E_Dfr2nCa";
    private static final String CLIENT_SECRET = "pBFENv4fyWsQunOn2ErkFgsxLCpCrJYPyvcOhrgUEmXuffHN0gkWl46yAJAcNrDX";
    private static final String REDIRECT_URI = "http://localhost:3000"; // Ensure this is the correct redirect URI
    private static final String AUTHORIZATION_CODE = "your_authorization_code"; // Obtain the authorization code from the OAuth process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        // Exchange authorization code for access token
        exchangeAuthorizationCodeForAccessToken(AUTHORIZATION_CODE);
    }

    // Retrofit method to exchange the authorization code for the access token
    private void exchangeAuthorizationCodeForAccessToken(String authorizationCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.patreon.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PatreonApiService apiService = retrofit.create(PatreonApiService.class);

        Call<TokenResponse> call = apiService.exchangeToken(
                CLIENT_ID,
                CLIENT_SECRET,
                authorizationCode,
                REDIRECT_URI,
                "authorization_code" // OAuth2 grant type
        );

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        String accessToken = tokenResponse.getAccessToken();
                        String refreshToken = tokenResponse.getRefreshToken();

                        // Save the tokens in SharedPreferences or a secure storage
                        saveTokens(accessToken, refreshToken);

                        // After saving the tokens, redirect to the donation page or handle as needed
                        redirectToDonationPage(accessToken);
                    }
                } else {
                    Toast.makeText(OAuthActivity.this, "Failed to exchange token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(OAuthActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokens(String accessToken, String refreshToken) {
        // Save the tokens securely (e.g., SharedPreferences, EncryptedStorage, etc.)
    }

    private void redirectToDonationPage(String accessToken) {
        // Redirect to the donation page, or pass the accessToken to the donation activity/fragment
        Toast.makeText(this, "Redirecting to donation page", Toast.LENGTH_SHORT).show();
        // Implement your logic to show the donation page using the accessToken
    }
}
