package com.example.playlistytsp;

public class SpotifyToken {

    String access_token;
    String token_type;
    String scope;
    int expires_in;
    String refresh_token;

    public SpotifyToken (String access_token, String token_type, String scope, int expires_in, String refresh_token){
        this.access_token = access_token;
        this.token_type = token_type;
        this.scope = scope;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
    }
}

