package me.meczka.utils;


import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.io.IOException;

public class Authenticator implements okhttp3.Authenticator {
    String username, password;
    public Authenticator(String username, String password)
    {
        this.username=username;
        this.password=password;
    }
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
    }
}
