/*
* Adapter class to bind the items to the list
* */
package implementations.anvesh.musicsearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Adapter_MusicAlbums extends BaseAdapter implements ListAdapter{

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private String[] listDetails = {};

    public Adapter_MusicAlbums(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos);
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    //getview method to create each item row of the list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_album_row, null);
        }

        listDetails = list.get(position).split("__");
        //Handle TextView and display string from your list
        final TextView txtArtistName = (TextView)view.findViewById(R.id.txtArtistName);
        if(listDetails[2].equalsIgnoreCase("null")){
            //txtArtistName.setText("");
            txtArtistName.setVisibility(View.GONE);
        }else {
            txtArtistName.setText(listDetails[2]);
        }

        TextView txtAlbumName = (TextView)view.findViewById(R.id.txtAlbumName);
        if(listDetails[3].equalsIgnoreCase("null")) {
            txtAlbumName.setVisibility(View.GONE);
        }else{
            txtAlbumName.setText(listDetails[3]);
        }

        TextView txtTrackName = (TextView) view.findViewById(R.id.txtTrackName);
        if(listDetails[1].equalsIgnoreCase("null")) {
            txtTrackName.setVisibility(View.GONE);
        }else {
            txtTrackName.setText(listDetails[1]);
        }

        ImageView imgAlbum = (ImageView) view.findViewById(R.id.albumImage);
        if(listDetails[0].equalsIgnoreCase("null")) {
            imgAlbum.setImageResource(R.drawable.img_not_available);
        }else {
            DownloadImageTask downloadImageTask = new DownloadImageTask(imgAlbum);
            downloadImageTask.execute(listDetails[0]);
        }
        return view;
    }

    /*
    * Class to download the image on separate thread using async task
    * */
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
}