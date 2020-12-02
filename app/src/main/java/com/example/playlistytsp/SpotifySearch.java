package com.example.playlistytsp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class created for connecting to Spotify's servers
 * Class process data with songs titles passed from MainActivity and create playlist on a Spotify account
 */

public class SpotifySearch extends AppCompatActivity {

    TextView youtubeUserTextView;
    TextView playListNameTextView;
    TextView spotifyUserTextView;
    Button confirmButton;

    String spotifyURL = getString(R.string.spotifyURL);
    String clientID = getResources().getString(R.string.clientID);
    String clientSecret = getResources().getString(R.string.clientSecret);
    String spotifyUserId;
    String playlistName;
    String encodedClientIDAndSecret;
    String redirect_uri = getString(R.string.redirect_uri);
    String formattedRedirect_uri;
    String code;
    String state = getString(R.string.state_Spotify);
    StringBuilder urlAuth;

    SpotifyToken spotifyToken;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> songsList;

    Retrofit spotifyRetrofit;

    // Constructor with a basic Retrofit object used for connection.

    public SpotifySearch (){

        spotifyRetrofit = new Retrofit.Builder()
                .baseUrl(spotifyURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spotify_search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.logo_spotify);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SpotifySearch.this);

        youtubeUserTextView = findViewById(R.id.youTubeUserAnswer);
        playListNameTextView = findViewById(R.id.playlistNameAnswer);
        spotifyUserTextView = findViewById(R.id.spotifyUserAnswer);
        confirmButton = findViewById(R.id.buttonConfirm);

        Intent intent = getIntent();
        String youtubeUser = intent.getStringExtra("youtubeUser");
        songsList = intent.getStringArrayListExtra("songs");
        spotifyUserId = intent.getStringExtra("spotifyID");
        playlistName = intent.getStringExtra("playlistName");

        youtubeUserTextView.setText(youtubeUser);
        spotifyUserTextView.setText(spotifyUserId);
        playListNameTextView.setText(playlistName);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playlistName.isEmpty() || spotifyUserId.isEmpty()){
                    Toast.makeText(SpotifySearch.this, getString(R.string.incompleteData), Toast.LENGTH_SHORT).show();
                }else {
                    authorize();
                }
            }
        });
    }

    // This method is used for encoding client ID and client Secret to Spotify format

    private void setEncodeClientIdSecret (){
        byte[] data = new byte[0];
        String clientIDandSecret = clientID + ":" + clientSecret;
        try {
            data = clientIDandSecret.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("ClientIdSecret error",e.getMessage());
        }
        encodedClientIDAndSecret = Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private void setFormattedRedirect_uri() throws UnsupportedEncodingException {
        formattedRedirect_uri = URLEncoder.encode(redirect_uri, "UTF-8");
    }


    /*
     Method responsible for authorizing this application to use user's spotify account accordingly to 'scope'.
     It directs user to a @WebAuth activity solely for this purpose. If user already gave his consent, he is directed to 'onActivityResult' automatically.
    */

    private void authorize (){
        try {
            setFormattedRedirect_uri();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setEncodeClientIdSecret();

        urlAuth = new StringBuilder();
        urlAuth.append("https://accounts.spotify.com/authorize?");
        urlAuth.append("client_id=" + clientID);
        urlAuth.append("&response_type=code");
        urlAuth.append("&redirect_uri=" + formattedRedirect_uri);
        urlAuth.append("&scope=user-read-private%20playlist-modify-public%20playlist-read-private%20playlist-modify-private%20playlist-read-collaborative");
        urlAuth.append("&state=" + state);

        Intent webIntent = new Intent(this, WebAuth.class);
        webIntent.putExtra("URL",urlAuth.toString());
        webIntent.putExtra("redirect_uri", redirect_uri);
        startActivityForResult(webIntent, 1);
    }


     //Parsing result from WebAuth. Cutting unwanted parts of the "code" passed by WebView

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            code = data.getStringExtra("code");
            int endCode = code.length() - 18;
            code = code.substring(0, endCode);

            createToken();
        }
    }

    //Creating token with a 'code' from onActivityResult

    private void createToken (){

        String authorizationHeader = "basic " + encodedClientIDAndSecret ;

        ConnectionInterface connectionInterface = spotifyRetrofit.create(ConnectionInterface.class);

        connectionInterface.createToken(authorizationHeader, "authorization_code", code, redirect_uri).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                try {
                    JsonObject jsonToken = response.body();
                    String access_token = jsonToken.get("access_token").getAsString();
                    String token_type = jsonToken.get("token_type").getAsString();
                    String scope = jsonToken.get("scope").getAsString();
                    int expires_in = jsonToken.get("expires_in").getAsInt();
                    String refresh_token = jsonToken.get("refresh_token").getAsString();

                    spotifyToken = new SpotifyToken(access_token, token_type, scope, expires_in, refresh_token);

                    createPlaylist();
                }catch (Exception ex){
                    Log.d("Token error", ex.getMessage());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.d("Token error", t.getMessage());
            }
        });
    }

     // Creating playlist on user's Spotify account. Initiation of tracks searching and adding.

    private void createPlaylist () {
        String url = "https://api.spotify.com/v1/users/" + spotifyUserId + "/playlists";

        editor = sharedPreferences.edit();

        JsonObject sendObject = new JsonObject();
        sendObject.addProperty("name", playlistName);
        sendObject.addProperty("public", true);
        sendObject.addProperty("description", "moja testowa playlista");

        ConnectionInterface interfacePost = spotifyRetrofit.create(ConnectionInterface.class);

        interfacePost.createPlaylist(url,"Bearer " + spotifyToken.access_token, "application/json", sendObject).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                try {
                    editor.putString("playlistDescription", response.body().get("description").getAsString());
                    editor.putString("playlistName", response.body().get("name").getAsString());
                    editor.putString("playlistID",response.body().get("id").getAsString());
                    editor.putBoolean("playlistPublic",response.body().get("public").getAsBoolean());
                    editor.putString("playlistUri", response.body().get("uri").getAsString());

                    editor.apply();
                } catch (Exception ex) {
                    Log.d("Create playlist Error", ex.getMessage());
                }

                if (response.isSuccessful()){
                    for (int i = 0; i < songsList.size() ; i++){
                        searchTrack(songsList.get(i));
                    }
                    Toast.makeText(SpotifySearch.this, getString(R.string.playListCopied), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.d("Create playlist Error", t.getMessage());
            }
        });
    }


     // Searching for a track from the YouTube playlist

    private void searchTrack (String title){

        String searchUrl = "https://api.spotify.com/v1/search";

        YouTubeTitleTranslator translator = new YouTubeTitleTranslator();

        String titleEncoded = translator.translateYouTubeTitle(title);

        ConnectionInterface jsonInterfacePost = spotifyRetrofit.create(ConnectionInterface.class);

        jsonInterfacePost.searchForArtist(searchUrl, "Bearer " + spotifyToken.access_token, "application/json", "application/json", titleEncoded, "track").enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JsonObject object = response.body();
                try {
                    JsonObject items = object.getAsJsonObject("tracks");
                    JsonArray songs = items.getAsJsonArray("items");

                    if (response.isSuccessful()) {
                        String songUri = songs.get(0).getAsJsonObject().get("uri").toString();
                        addTrack(songUri);
                    }

                }catch (Exception ex){
                    Log.d("Song not found", titleEncoded);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.d("Search track error", t.getMessage());
            }
        });
    }

    // Adding track to the playlist.

    private void addTrack (String songUri){
        String url = ("https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistID","") + "/tracks");

        ConnectionInterface jsoNinterfacePost = spotifyRetrofit.create(ConnectionInterface.class);

        String cuttedSongUri = songUri.substring(1,songUri.length()-1);

        jsoNinterfacePost.addTrack(url,"Bearer " + spotifyToken.access_token, "application/json", "application/json",cuttedSongUri).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.d("track response", response.message());
            }
            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.d("Add track error", t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goHome = new Intent(SpotifySearch.this, MainActivity.class);
        startActivity(goHome);
    }
}

