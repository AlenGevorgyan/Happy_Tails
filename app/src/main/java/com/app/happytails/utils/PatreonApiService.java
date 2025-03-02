package com.app.happytails.utils;

import com.app.happytails.utils.PledgeRequest;
import com.app.happytails.utils.PledgeResponse;
import com.app.happytails.utils.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PatreonApiService {

    @POST("v2/campaigns/{campaign_id}/pledges")
    @Headers("Content-Type: application/json")
    Call<PledgeResponse> createPledge(
            @Query("access_token") String accessToken,
            @Body PledgeRequest pledgeRequest
    );

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> exchangeToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType
    );
}
