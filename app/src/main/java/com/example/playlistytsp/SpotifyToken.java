package com.example.playlistytsp;

import com.google.gson.annotations.SerializedName;

public class SpotifyToken {

    @SerializedName("access_token")
    String access_token;

    @SerializedName("token_type")
    String token_type;

    @SerializedName("scope")
    String scope;

    @SerializedName("expires_in")
    int expires_in;

    @SerializedName("refresh_token")
    String refresh_token;

    public SpotifyToken (){

    }

    public SpotifyToken (String access_token, String token_type, String scope, int expires_in, String refresh_token){
        this.access_token = access_token;
        this.token_type = token_type;
        this.scope = scope;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getScope() {
        return scope;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}

