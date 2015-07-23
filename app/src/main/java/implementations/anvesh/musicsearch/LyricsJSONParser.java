/*
* Class to parse JSON Data
* */
package implementations.anvesh.musicsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Anvesh on 7/23/2015.
 */
public class LyricsJSONParser {
    public String parse(JSONObject jObject){

        String albumLyrics = "";
        try {
            /** Retrieves the lyrics element from the single JSON Object */
            albumLyrics = jObject.getString("lyrics");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return albumLyrics;
    }
}
