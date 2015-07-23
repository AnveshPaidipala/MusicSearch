/*
* Class to query the lyrics from the wikia service and also show other details of the album
* */
package implementations.anvesh.musicsearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class album_lyrics extends ActionBarActivity {

    TextView txtLyrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_lyrics);

        TextView txtArtistName = (TextView)findViewById(R.id.txtArtistName);
        TextView txtAlbumName = (TextView)findViewById(R.id.txtAlbumName);
        TextView txtTrackName = (TextView) findViewById(R.id.txtTrackName);
        ImageView imgAlbum = (ImageView) findViewById(R.id.albumImage);
        txtLyrics = (TextView) findViewById(R.id.txtLyrics);

        txtArtistName.setText(getIntent().getExtras().get("artistName").toString());
        txtAlbumName.setText(getIntent().getExtras().get("albumName").toString());
        txtTrackName.setText(getIntent().getExtras().get("songName").toString());

        DownloadImageTask downloadImageTask = new DownloadImageTask(imgAlbum);
        downloadImageTask.execute(getIntent().getExtras().get("albumImg").toString());

        StringBuilder buildUrl = new StringBuilder("http://lyrics.wikia.com/api.php?artist=");
        String[] inputArtistName = getIntent().getExtras().get("artistName").toString().split(" ");
        String[] inputTrackName = getIntent().getExtras().get("songName").toString().split(" ");
        for(int i=0;i<inputArtistName.length;i++){
            if(i==0)
                buildUrl.append(inputArtistName[i]);
            else
                buildUrl.append("+"+inputArtistName[i]);
        }
        for(int i=0;i<inputTrackName.length;i++){
            if(i==0)
                buildUrl.append("&song="+inputTrackName[i]);
            else
                buildUrl.append("+"+inputTrackName[i]);
        }
        buildUrl.append("&fmt=json");
        ParseLyrics getLyrics = new ParseLyrics();
        getLyrics.execute(buildUrl.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_lyrics, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    class to download the Album Image
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /** A method to download json data from url */
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

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /** A class, to download Album lyrics */
    private class ParseLyrics extends AsyncTask<String, Integer, String> {

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
            LyricsParserTask parserTask = new LyricsParserTask();

            // Start parsing the Album lyrics in JSON format
            // Invokes the "doInBackground()" method of the class LyricsParserTask
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Album Lyrics in JSON format
     */

    private class LyricsParserTask extends AsyncTask<String, Integer,  String> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... jsonData) {

            //List<HashMap<String, String>> lyrics = null;
            String lyrics = "";
            LyricsJSONParser lyricsJsonParser = new LyricsJSONParser();

            try {
                /*As the result is not exactly in json format,
                so extracting the single record and converting to a single JSON Object
                By spliting the response*/
                String[] reqJsonArray = jsonData[0].split("=");

                String reqJsonData = reqJsonArray[1].trim();
                jObject = new JSONObject(reqJsonData);

                /** Getting the parsed data  */
                lyrics = lyricsJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return lyrics;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String lyrics) {
            txtLyrics.setText("Song Lyrics: "+lyrics);
        }
    }
}
