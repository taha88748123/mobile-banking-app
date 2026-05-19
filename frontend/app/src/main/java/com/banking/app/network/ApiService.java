package com.banking.app.network;

import com.banking.app.models.AccountInfo;
import com.banking.app.models.AddBeneficiaryRequest;
import com.banking.app.models.ApiMessage;
import com.banking.app.models.Beneficiary;
import com.banking.app.models.ChangePasswordRequest;
import com.banking.app.models.DepositRequest;
import com.banking.app.models.LoginRequest;
import com.banking.app.models.LoginResponse;
import com.banking.app.models.OtpRequest;
import com.banking.app.models.ProfileResponse;
import com.banking.app.models.ResendOtpRequest;
import com.banking.app.models.SignupRequest;
import com.banking.app.models.StatisticsResponse;
import com.banking.app.models.Transaction;
import com.banking.app.models.TransferRequest;
import com.banking.app.models.UpdateProfileRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Interface Retrofit decrivant tous les endpoints backend.
 */
public interface ApiService {

    // ----------- AUTHENTIFICATION -----------
    @POST("auth/signup")
    Call<ApiMessage> signup(@Body SignupRequest request);

    @POST("auth/verify-otp")
    Call<ApiMessage> verifyOtp(@Body OtpRequest request);

    @POST("auth/resend-otp")
    Call<ApiMessage> resendOtp(@Body ResendOtpRequest request);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ----------- COMPTE -----------
    @GET("account/info")
    Call<AccountInfo> getAccountInfo();

    // ----------- TRANSACTIONS -----------
    @POST("transactions/transfer")
    Call<Transaction> transfer(@Body TransferRequest request);

    @POST("transactions/deposit")
    Call<Transaction> deposit(@Body DepositRequest request);

    @GET("transactions/history")
    Call<List<Transaction>> getHistory();

    // ----------- PROFIL -----------
    @GET("profile")
    Call<ProfileResponse> getProfile();

    @PUT("profile")
    Call<ProfileResponse> updateProfile(@Body UpdateProfileRequest request);

    @POST("profile/change-password")
    Call<ApiMessage> changePassword(@Body ChangePasswordRequest request);

    // ----------- BENEFICIAIRES -----------
    @GET("beneficiaries")
    Call<List<Beneficiary>> listBeneficiaries();

    @POST("beneficiaries")
    Call<Beneficiary> addBeneficiary(@Body AddBeneficiaryRequest request);

    @DELETE("beneficiaries/{id}")
    Call<ApiMessage> deleteBeneficiary(@Path("id") Long id);

    // ----------- STATISTIQUES -----------
    @GET("statistics")
    Call<StatisticsResponse> getStatistics();
}
