package com.banking.app.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Response;

/**
 * Extrait un message d'erreur lisible depuis une reponse Retrofit non OK.
 */
public class ApiErrorParser {

    public static String parse(Response<?> response) {
        if (response.errorBody() == null) {
            return "Erreur " + response.code();
        }
        try {
            String body = response.errorBody().string();
            JsonObject obj = new Gson().fromJson(body, JsonObject.class);
            if (obj != null && obj.has("message")) {
                return obj.get("message").getAsString();
            }
            return body;
        } catch (Exception e) {
            return "Erreur " + response.code();
        }
    }
}
