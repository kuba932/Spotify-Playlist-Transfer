package com.example.playlistytsp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ConnectionInterface {

    // YouTube Class

    @GET("channels")
    Call<JsonObject> getChannelId (@Header("X-Android-Package") String packageMy, @Header("X-Android-Cert") String SHA1,  @Query("part") String part, @Query("forUsername") String userName, @Query("key") String APIKey);

    @GET("playlists")
    Call<JsonObject> getPlaylists (@Header("X-Android-Package") String packageMy, @Header("X-Android-Cert") String SHA1, @Query("part") String part, @Query("channelId") String channelId, @Query("key") String APIkey);

    @GET("playlistItems")
    Call <JsonObject> getItemsFromPlaylist (@Header("X-Android-Package") String packageMy, @Header("X-Android-Cert") String SHA1, @Query("part") String part, @Query("maxResults") int maxResults, @Query("playlistId") String playlistId,@Query("maxResults") int numberOfSongs, @Query("key") String APIKey);

    // Spotify Class

    @FormUrlEncoded
    @POST("api/token")
    Call <JsonObject> createToken (@Header("Authorization") String baseAuth, @Field("grant_type") String grant_type, @Field("code") String code, @Field("redirect_uri") String redirect_uri);

    @POST()
    Call<JsonObject> createPlaylist(@Url String url, @Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Body JsonObject postPlaylist);

    @GET()
    Call<JsonObject> searchForArtist (@Url String url, @Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Header("Accept") String application, @Query("q") String title, @Query("type") String type);

    @POST()
    Call<JsonObject> addTrack (@Url String url, @Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Header("Accept") String accept, @Query("uris") String uris);
}

