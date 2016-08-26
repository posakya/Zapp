package com.example.aasis.zappfood;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Roshan Posakya on 09/06/2016.
 */
public class background extends AsyncTask<String, Void, String> {
    Context context;

    background(Context ctxt) {
        this.context = ctxt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String cart_url = Constants.BASE_URL + "/zappfood/cart.php";
        if (type.equals("Pizza")) {
            try {
                String iname = params[1];
                String price = params[2];
                URL url = new URL(cart_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("iname", "UTF-8") + "=" + URLEncoder.encode(iname, "UTF-8") + "&" + URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream stream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));
                String result = "Inserted ";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                stream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(context,result, Toast.LENGTH_LONG).show();
    }
}
