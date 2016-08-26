package com.example.aasis.zappfood;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aasis.zappfood.models.ItemList;
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

public class MoMo extends AppCompatActivity {
    private ListView lvMovies;
    private ProgressDialog dialog;
    TextView Iname,Price;
    String iname,price;
    String username,password,name;
    private  List<ItemList> itemList = new ArrayList<ItemList>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mo_mo);
        new JSONTask().execute(Constants.BASE_URL +"/zappfood/momoapi.php");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        lvMovies = (ListView) findViewById(R.id.item_list);

        itemClick();
        setTitle(getResources().getText(R.string.MoMo));
    }
    private void itemClick(){
        lvMovies.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ItemList value = (ItemList) parent.getItemAtPosition(position);
                        //ItemList value= (ItemList) parent.getOnItemClickListener();
                        // updatetable in server
                        new background(getApplicationContext()).execute("Pizza", value.getIname(), value.getPrice());
                        Toast.makeText(MoMo.this, "Added to cart", Toast.LENGTH_SHORT).show();

                        /// save to database
                        // saveToDatabase(value);


                    }
                }

        );
    }
    public class JSONTask extends AsyncTask<String, String, List<ItemList>> {

        @Override
        protected List<ItemList> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            List<ItemList> itemList = new ArrayList<>();
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
                    ItemList itemList1 = new ItemList();
                    itemList1.setImage("http://192.168.7.7/zappfood/image/" + j.getString("Image"));
                    itemList1.setID(j.getString("ID"));
                    itemList1.setIname(j.getString("Iname"));
                    //movieModel.setRating((float) j.getDouble("Rating"));
                    itemList1.setPrice(j.getString("Price"));
                    itemList.add(itemList1);
                }
                return itemList;
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
            return itemList;
        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.show();
//        }

        @Override
        protected void onPostExecute(List<ItemList> result) {
            super.onPostExecute(result);
//            dialog.dismiss();
            ItemAdapter adapter = new ItemAdapter(getApplicationContext(),R.layout.item, result);
            lvMovies.setAdapter(adapter);

        }

    }

    public class ItemAdapter extends ArrayAdapter {
        public List<ItemList> itemList;
        private int resource;
        private LayoutInflater inflater;


        public ItemAdapter(Context context, int resource, List<ItemList> objects) {
            super(context, resource, objects);
            itemList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item, null);
                holder.imageicon = (ImageView) convertView.findViewById(R.id.imageicon);
                holder.tvIname = (TextView) convertView.findViewById(R.id.tvIname);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                //holder.tvratingbar = (RatingBar) convertView.findViewById(R.id.tvratingBar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            // then later, when you want to display image

            ImageLoader.getInstance().displayImage(itemList.get(position).getImage(), holder.imageicon, new ImageLoadingListener() {
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
            holder.tvIname.setText(itemList.get(position).getIname());
            holder.tvPrice.setText(itemList.get(position).getPrice());
            Log.d("MainActivity", "Image = " + itemList.get(position).getImage());
            ImageLoader.getInstance().displayImage(itemList.get(position).getImage(), holder.imageicon);
            // holder.tvratingbar.setRating(movieModelList.get(position).getRating());

            return convertView;
        }

        class ViewHolder {
            private ImageView imageicon;
            private TextView tvIname;
            private TextView tvPrice;

            // private RatingBar tvratingbar;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
