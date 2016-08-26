package com.example.aasis.zappfood;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.aasis.zappfood.models.MovieModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Thirdfragment extends Fragment {
    View myView;
    private ListView lvMovies;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.third_layout, container, false);
        dialog = new ProgressDialog(this.getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading, please wait");
        new JSONTask().execute("http://www.roshandhobi.com.np/_cgi-bin/api.php");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        lvMovies = (ListView)myView . findViewById(R.id.listView);

        getActivity().setTitle("Order Online");
        return myView;
    }


    public class JSONTask extends AsyncTask<String, String, List<MovieModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            List<MovieModel> movieModelList = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finaljson = buffer.toString();
                Log.i("JSON", "String = " + finaljson);
                JSONArray array = new JSONArray(finaljson);

                int size = array.length();
                for (int i = 0; i < size; i++) {
                    JSONObject j = array.getJSONObject(i);
                    MovieModel movieModel = new MovieModel();
                    movieModel.setImage("http://www.roshandhobi.com.np/_cgi-bin/image/" + j.getString("Image"));
                    movieModel.setCategorie(j.getString("Iname"));
                   // movieModel.setRating((float) j.getDouble("Rating"));
                    movieModel.setDescription(j.getString("Description"));
                    movieModelList.add(movieModel);
                }
                return movieModelList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return movieModelList;
        }
        @Override
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            MovieAdapter adapter=new MovieAdapter(getActivity(),R.layout.row,result);
            lvMovies.setAdapter(adapter);
        }


    }
    public class MovieAdapter extends ArrayAdapter {
        public List<MovieModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;


        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder=null;

            if (convertView == null) {
                holder=new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.imageicon = (ImageView) convertView.findViewById(R.id.imageicon);
                holder. tvIname = (TextView) convertView.findViewById(R.id.tvIname);
                holder. tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
               // holder. tvratingbar = (RatingBar) convertView.findViewById(R.id.tvratingBar);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar=(ProgressBar) convertView.findViewById(R.id.progressBar);

            // then later, when you want to display image

            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.imageicon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            //data insert
            holder.tvIname.setText(movieModelList.get(position).getCategorie());
            holder.tvDescription.setText(movieModelList.get(position).getDescription());
            Log.d("MainActivity", "Image = " + movieModelList.get(position).getImage());
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.imageicon);
           // holder. tvratingbar.setRating(movieModelList.get(position).getRating());

            return convertView;
        }
        class ViewHolder{
            private ImageView imageicon;
            private  TextView tvIname;
            private TextView tvDescription;
           // private RatingBar tvratingbar;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
