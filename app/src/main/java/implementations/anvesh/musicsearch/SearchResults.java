/*
* Class that assigns the external fields or serialized names to user defined variables using Gson libraries
* */
package implementations.anvesh.musicsearch;
import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Anvesh on 7/22/2015.
 */
public class SearchResults {
    public List<Result> results;

    @SerializedName("resultCount")
    public int totalRecords;
}
