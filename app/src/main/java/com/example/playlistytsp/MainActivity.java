package com.example.playlistytsp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;
    ConnectionInterface connectionInterface;
    ArrayList <String> songsList;

    String youtubeUrl;
    String userName;
    String playlistName;
    String spotifyID;
    YouTubeTransfer youTubeTransfer;

    MaterialButton bTransfer;

    Context context;

    // Getting data from the user about his YouTube and Spotify account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youtubeUrl = getString(R.string.youtubeUrl);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.logo_spotify);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final MyEditText userEdit = findViewById(R.id.userName);
        final MyEditText playlistEdit = findViewById(R.id.playlistName);
        final MyEditText userSpotifyID = findViewById(R.id.userSpotifyID);
        context = this.getApplicationContext();

        bTransfer = findViewById(R.id.buttonTransfer);

        songsList = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .baseUrl(youtubeUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        connectionInterface = retrofit.create(ConnectionInterface.class);

        bTransfer.setOnClickListener(view -> {

            if (!userEdit.isEmpty() && !playlistEdit.isEmpty() && !userSpotifyID.isEmpty()){

                userName = String.valueOf(userEdit.getText());
                playlistName = String.valueOf(playlistEdit.getText());
                spotifyID = String.valueOf(userSpotifyID.getText());

                String packageName = getResources().getString(R.string.packageName);
                String sha1 = getResources().getString(R.string.SHA1);
                String apiKey = getResources().getString(R.string.youtubeApiKey);

                youTubeTransfer = new YouTubeTransfer(packageName, sha1, apiKey, connectionInterface, this, userName, playlistName, songsList);
                youTubeTransfer.startYouTubeDownloadProcess();

            }else {
                Toast.makeText(MainActivity.this, getString(R.string.emptyFieldsErrorMainActivity), Toast.LENGTH_SHORT).show();
            }

        });
    }

     //Going to SpotifySearch Activity to create a playlist

    public void goToSpotify(){
        Intent goToSpotify = new Intent(this, SpotifySearch.class);
        goToSpotify.putExtra("youtubeUser", userName);
        goToSpotify.putExtra("songs", songsList);
        goToSpotify.putExtra("spotifyID", spotifyID);
        goToSpotify.putExtra("playlistName", playlistName);
        startActivity(goToSpotify);
    }
}