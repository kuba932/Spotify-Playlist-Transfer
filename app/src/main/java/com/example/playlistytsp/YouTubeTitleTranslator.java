package com.example.playlistytsp;

import android.util.Log;
import java.util.ArrayList;

/**
 * Class created solely for purpose of translation songs titles form Youtube's playlists
 */

public class YouTubeTitleTranslator {

    public YouTubeTitleTranslator() {
    }


    //  this method returns a String ready to pass to Spotify search engine

    public String translateYouTubeTitle (String songTitle){

        StringBuilder builder = new StringBuilder();

        builder.append(songTitle);
        ArrayList<Integer> numbers = new ArrayList();


        // looking for '%22" (etc.) parts of a String
        for (int i =0; i < builder.length(); i++){
            char a = builder.charAt(i);
            char b = 'b';
            char c = 'c';
            try {
                b = builder.charAt(i + 1);
            }catch (IndexOutOfBoundsException ex){
                Log.d("isNumber error", ex.getMessage());
            }
            try {
                c = builder.charAt(i + 2);
            }catch (IndexOutOfBoundsException ex){
                Log.d("isNumber error", ex.getMessage());
            }

        //removing unwanted parts like '%22' or '%29'
            if (a == '%' && isNumber(b) && isNumber(c)){
                numbers.add(i);
                numbers.add(i+1);
                numbers.add(i+2);
            }else if (a == '%' && isNumber(b)){
                numbers.add(i);
                numbers.add(i+1);
            }else if (a == '%' && (!isNumber(b) || !isNumber(c))){
                numbers.add(i);
            }
        }

        // removing unwanted parts from StringBuilder
        for(int i = numbers.size() - 1; i >= 0; i--){
            int j = 0;
            builder.deleteCharAt(numbers.get(i) - j);
            j++;
        }

        // cutting stringBuilder to reduce a chance of unwanted parts (ex. 'official video', 'Youtube Mix')
        if (builder.length() > 50){
            builder.setLength(50);
        }

        //removing unwanted parts like '(remix)' or '(official video)'
        for (int i =0; i<builder.length(); i++){
            if (builder.charAt(i) == '(' || builder.charAt(i) == '['){
                builder.setLength(i);
            }
        }

        return builder.toString();
    }

   // Method designed to check if specified char is a number

    private boolean isNumber (char c){
        char [] numbers = new char[] {0,1,2,3,4,5,6,7,8,9};

        for (int i = 0; i < numbers.length; i++){
            if (c == numbers[i]){
                return true;
            }
        }
        return false;
    }
}

