package com.banking.app.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.banking.app.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Intercepteur OkHttp qui ajoute "Authorization: Bearer <token>" automatiquement
 * a toutes les requetes sauf /auth/**.
 */
public class AuthInterceptor implements Interceptor {

    private final SessionManager session;

    public AuthInterceptor(Context context) {
        this.session = new SessionManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString();

        if (url.contains("/auth/")) {
            return chain.proceed(original);
        }
        String token = session.getToken();
        if (token == null) {
            return chain.proceed(original);
        }
        Request authenticated = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authenticated);
    }
}
