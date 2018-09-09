package utils;

/**
 * Created by ravi on 29-Mar-17.
 */



import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {


    final  String API_URL = "http://52e4a06a.ngrok.io/fetch.php?offset=";
    String TAG = "DROID";

    public HttpHandler() {
    }


    public Response makeServiceCall(int page) {

        boolean isNetworkError = false;
        String response = null;
        try {
            URL url = new URL(API_URL+page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
            isNetworkError = true;
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
            isNetworkError = true;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            isNetworkError = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            isNetworkError = true;
        }

        Response response1;
        if(isNetworkError == false) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
             response1 = new Response(false,jsonObject,page);

        }else
        {
            response1 = new Response(true,null,page);

        }
        return response1;
    }


    public InputStream makeImageRequest(String url)
    {
        URL imageUrl = null;
        InputStream stream = null;
        try {
            imageUrl = new URL(url);

        HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
        stream=conn.getInputStream();

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return stream;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}