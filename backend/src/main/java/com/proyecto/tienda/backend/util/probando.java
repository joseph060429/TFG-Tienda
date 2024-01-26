package com.proyecto.tienda.backend.util;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;

public class probando {
    public static void main(String[] args) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credentials = getCredentials(HTTP_TRANSPORT);

            System.out.println(credentials.getRefreshToken());
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    private static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport,
                GsonFactory.getDefaultInstance(),
                "558869305771-8q1lohufrmhnmvpb0o2eisrovkh3pfkc.apps.googleusercontent.com", // Client ID
                "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", // Client Secret
                Arrays.asList(GmailScopes.GMAIL_SEND))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
