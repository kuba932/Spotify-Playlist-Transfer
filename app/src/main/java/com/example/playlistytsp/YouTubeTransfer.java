package com.example.playlistytsp;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class responsible for getting data (playlist and songs titles) from user's Youtube account
 */

public class YouTubeTransfer {

    MainActivity mainActivity;
    ConnectionInterface connectionInterface;

    private String channelID;
    private String userName;
    private String playlistID;
    private String playlistName;

    private ArrayList <String> songsList;
    final String packageName;
    final String SHA1;
    final String APIKey;

    /**
     * @param connectionInterface - passed from MainActivity (working on main thread)
     * @param userName - YouTube user name got from the user
     * @param playlistName - YouTube playlist name got from the user
     * @param songsList - empty array for song's titles fetched from the user's playlist
     * @param mainActivity - object representing MainActivity class
     */

    YouTubeTransfer (String packageName, String SHA1, String APIKey, ConnectionInterface connectionInterface, MainActivity mainActivity, String userName, String playlistName, ArrayList <String> songsList){
        this.packageName = packageName;
        this.SHA1 = SHA1;
        this.APIKey = APIKey;

        this.connectionInterface = connectionInterface;
        this.userName = userName;
        this.playlistName = playlistName;
        this.mainActivity = mainActivity;
        this.songsList = songsList;
    }

    public void startYouTubeDownloadProcess(){
        getChannelId();
    }

     //Fetching channel ID of the user

    private void getChannelId(){
        connectionInterface.getChannelId(packageName, SHA1,"snippet", userName, APIKey).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JsonObject responseObject = response.body();

                    JsonArray responseArray = responseObject.get("items").getAsJsonArray();
                    channelID = responseArray.get(0).getAsJsonObject().get("id").getAsString();
                    getConnection();
                }catch (Exception ex){

                    Toast.makeText(mainActivity.getBaseContext(), mainActivity.getString(R.string.youTubeUserNotFound, userName), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(t.getCause().toString(), t.getLocalizedMessage());
            }
        });
    }

    private void getConnection(){

        connectionInterface.getPlaylists(packageName, SHA1, "snippet", channelID, APIKey).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject responseObject = response.body();
                JsonArray playlists = responseObject.getAsJsonArray("items");

                String currentPlaylistName;

                for (int i = 0; i < playlists.size(); i++){
                    currentPlaylistName = playlists.get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString();

                    if (playlistName.equals(currentPlaylistName)){
                        playlistID = playlists.get(i).getAsJsonObject().get("id").getAsString();
                        getItemsFromPlaylist();
                        break;
                    }else{
                        Toast.makeText(mainActivity.getBaseContext(), mainActivity.getString(R.string.youTubePlaylistNotFound, playlistName, userName), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(t.getCause().toString(), t.getLocalizedMessage());
            }
        });
    }


    //Fetching songs from the user's playlist

    private void getItemsFromPlaylist (){

        connectionInterface.getItemsFromPlaylist(packageName, SHA1,"snippet", 50, playlistID, 50, APIKey).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject resultObject = response.body();

                    JsonArray songs = resultObject.getAsJsonArray("items");


                    for (int i = 0; i < songs.size(); i++) {
                        try {
                            JsonObject song = songs.get(i).getAsJsonObject();
                            songsList.add(String.valueOf(song.get("snippet").getAsJsonObject().get("title")));
                        } catch (Exception ex) {
                            Log.d(ex.getCause().toString(), ex.getMessage());
                        }
                        mainActivity.goToSpotify();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(t.getCause().toString(), t.getLocalizedMessage());
            }
        });
    }

}

