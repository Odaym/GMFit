package com.mcsaatchi.gmfit.rest;

import com.mcsaatchi.gmfit.activities.SignIn_Activity;
import com.mcsaatchi.gmfit.activities.SignUp_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_3_Fragment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GMFit_Service {

    @POST("login")
    Call<AuthenticationResponse> signInUser(@Body SignIn_Activity.SignInRequest userCredentails);

    @POST("register")
    Call<AuthenticationResponse> registerUser(@Body SignUp_Activity.RegisterRequest userCredentials);

    @GET("logout")
    Call<DefaultGetResponse> signOutUser(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken);

    @POST("user/update-profile")
    Call<AuthenticationResponse> updateUserProfile(@Header(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER) String userAccessToken, @Body Setup_Profile_3_Fragment
            .UpdateProfileRequest updateProfileRequest);
}
