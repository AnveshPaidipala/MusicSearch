/*
* Class that assigns the internal fields of each record or serialized names to user defined variables using Gson libraries
* */
package implementations.anvesh.musicsearch;

import com.google.gson.annotations.SerializedName;
/**
 * Created by Anvesh on 7/22/2015.
 */
public class Result {

    @SerializedName("artistName")
    public String artistName;

    @SerializedName("trackName")
    public String trackName;

    @SerializedName("collectionName")
    public String albumName;

    @SerializedName("artworkUrl100")
    public String albumImage;

    @SerializedName("collectionPrice")
    public String collectionPrice;

    @SerializedName("longDescription")
    public String longDescription;
}
