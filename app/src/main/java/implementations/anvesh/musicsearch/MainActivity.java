/*
* Main Class which allows the user to give a search query and get the results
* */
package implementations.anvesh.musicsearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity{

    EditText edtSearch;
    ListView albumsList;
    Adapter_MusicAlbums adapter;
    SearchResults response;
    public ArrayList<String> listContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtSearch = (EditText) findViewById(R.id.edit_query);
        albumsList = (ListView) findViewById(R.id.listResults);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           //Method to hide the Soft keyboard on clicking the search icon on keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(edtSearch.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(getApplicationContext(), "Enter your favourite album/artist before clicking on search", Toast.LENGTH_LONG).show();
                    }else {
                        performSearch();
                    }
                    return true;
                }
                return false;
            }
        });

        //Setting the onItem Click Listener for the list items
        albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String[] params = adapter.getItem(position).toString().split("__");

                Intent albumDetailsIntent = new Intent(getApplicationContext(), album_lyrics.class);
                albumDetailsIntent.putExtra("albumImg",params[0]);
                albumDetailsIntent.putExtra("songName",params[1]);
                albumDetailsIntent.putExtra("artistName",params[2]);
                albumDetailsIntent.putExtra("albumName",params[3]);

                startActivity(albumDetailsIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }/*else if(id == R.id.action_search){
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    //Method which perform search operation by invoking the Async task class MusicSearch class to fetch the results
    private void performSearch(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_LONG).show();
        //String url = "https://itunes.apple.com/search?term="+edtSearch.getText().toString();
        String[] inputSearchQuery = edtSearch.getText().toString().split(" ");
        StringBuilder buildUrl = new StringBuilder("https://itunes.apple.com/search?term=");
        for(int i=0;i<inputSearchQuery.length;i++){
            if(i==0)
                buildUrl.append(inputSearchQuery[i]);
            else
                buildUrl.append("+"+inputSearchQuery[i]);
        }
        MusicSearch getSearchResults = new MusicSearch();
        getSearchResults.execute(buildUrl.toString());

    }
    /**
     * A class, to download Albums/Artists Data
     */
    private class MusicSearch extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            if(response.totalRecords == 0){
                Toast.makeText(getApplicationContext(), "No results found, please try with different name!!", Toast.LENGTH_LONG).show();
            }else {
                adapter = new Adapter_MusicAlbums(listContents, getApplicationContext());
                albumsList.setAdapter(adapter);
            }
        }
    }

    /** A method to download json data from url
     * In this method we used GSON Library for easy parsing of the JSON data
     * */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            Gson gson = new Gson(); //Gson object

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            //Assigns all the external fields in the response to searialized names
            response = gson.fromJson(br,SearchResults.class);
            //Assigns all the internal fields of the each record in the response to searialized names
            List<Result> albumResults = response.results;
            listContents = new ArrayList<String>();

            for(Result res : albumResults){
                listContents.add(res.albumImage+"__"+res.trackName+"__"+res.artistName+"__"+res.albumName);
            }


            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

}