package com.example.aasis.zappfood;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aasis.zappfood.models.Cart;
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

public class Fourthfragment extends Fragment {
    View myView;
    private ListView lv_cart;
    private ProgressDialog dialog;
    Spinner spinner;
    String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    int subtotal = 0;
    private  List<Cart> cartList = new ArrayList<Cart>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.fourth_layout, container, false);

//        spinner=(Spinner) myView.findViewById(R.id.spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, quantity);
//        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        spinner.setAdapter(adapter);

        dialog = new ProgressDialog(this.getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading, please wait");
        new JSONTask().execute("http://192.168.7.7/zappfood/cartapi.php");
        lv_cart = (ListView) myView.findViewById(R.id.listView_cart);
//        SharedPreferences settings = this.getActivity().getSharedPreferences("PREFS", 0);
//        quantity.setText(settings.getString("value", ""));
        getActivity().setTitle("Cart");


// spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//
//            });



        return myView;

    }

    public class JSONTask extends AsyncTask<String, String, List<Cart>> {

        @Override
        protected List<Cart> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            List<Cart> cartList = new ArrayList<>();
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
                    Cart cart = new Cart();

                    cart.setIname(j.getString("Iname"));

                    cart.setPrice(j.getInt("Price"));
                    cartList.add(cart);
                }
                return cartList;
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
            return cartList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<Cart> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (getActivity() != null) {
                CartAdapter adapter = new CartAdapter(getActivity(), R.layout.cart, result);
                lv_cart.setAdapter(adapter);
            }
        }


    }

    public class CartAdapter extends ArrayAdapter {
        public List<Cart> cartList;
        private int resource;
        private LayoutInflater inflater;


        public CartAdapter(Context context, int resource, List<Cart> objects) {
            super(context, resource, objects);
            cartList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.cart, null);
                // holder.imageicon = (ImageView) convertView.findViewById(R.id.imageicon);
                holder.tvIname = (TextView) convertView.findViewById(R.id.tv_selected_item_Iname);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_selected_item_price);
                // holder.tvQuantity = (TextView) convertView.findViewById(R.id.tv_selected_item_quantity);
                holder.tv_subtotal = (TextView) convertView.findViewById(R.id.tv_subtotal);
                holder.spinner = (Spinner) convertView.findViewById(R.id.spinner);
                //holder.tvratingbar = (RatingBar) convertView.findViewById(R.id.tvratingBar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final int itemPrice = cartList.get(position).getPrice();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, quantity);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    subtotal = itemPrice * (parent.getSelectedItemPosition()+1);
                    Log.i("Spinner", "String = " + quantity  + " pos = " + parent.getSelectedItemPosition() + " size = " + quantity[parent.getSelectedItemPosition()]);
                    holder.tv_subtotal.setText("Rs " + subtotal + "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.tvIname.setText(cartList.get(position).getIname());
            holder.tvPrice.setText("Rs. " + (cartList.get(position).getPrice()));

//            Log.d("MainActivity", "Image = " + movieModelList.get(position).getImage());
//            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.imageicon);
            // holder.tvratingbar.setRating(movieModelList.get(position).getRating());

            return convertView;
        }

        class ViewHolder {
            //  private ImageView imageicon;
            private TextView tvIname;
            public TextView tvPrice;
            public TextView tvQuantity, tv_subtotal;
            public Spinner spinner;

            //public Spinner spinner;
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
        }
        return super.onOptionsItemSelected(item);

    }

}