package com.app.happytails.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OAuthCallbackActivity extends Activity {

    private static final String CLIENT_ID = "b52EdnyzuTSeiwP8LfcSh3TEjnkshMfQpzeV8QewHNyzBOM6OObEn34E_Dfr2nCa";
    private static final String CLIENT_SECRET = "pBFENv4fyWsQunOn2ErkFgsxLCpCrJYPyvcOhrgUEmXuffHN0gkWl46yAJAcNrDX";
    private static final String REDIRECT_URI = "http://localhost:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();

        if (uri != null && uri.getScheme().equals("happytails") && uri.getHost().equals("patreon")) {
            String code = uri.getQueryParameter("code");

            if (code != null) {
                getAccessToken(code);
            }
        }
    }

    private void getAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("redirect_uri", REDIRECT_URI)
                .build();

        Request request = new Request.Builder()
                .url("https://www.patreon.com/api/oauth2/token")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse the access token from the response JSON
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String accessToken = jsonResponse.getString("access_token");

                        redirectToDonationPage(accessToken);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the error
                    Log.e("PatreonAuth", "Error in response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void redirectToDonationPage(String accessToken) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patreon.com/dog_page"));
        startActivity(intent);
    }
}
