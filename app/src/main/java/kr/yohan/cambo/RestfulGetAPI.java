package kr.yohan.cambo;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 3330MT on 2017-01-10.
 */

interface OnCompletionListener {
    void onComplete(String result);
}

public class RestfulGetAPI extends AsyncTask<String, Void, String> {

    OnCompletionListener listener = null;

    public RestfulGetAPI(OnCompletionListener listener) {
        this.listener = listener;
    }


    protected String doInBackground(String... params) {
        String urlStr = params[0];
        URL url = null;
        String xml = "";
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].split("=");
                connection.setRequestProperty(pair[0], pair[1]);
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                xml += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    protected void onPostExecute(String result) {
        if (listener != null)
            listener.onComplete(result);
    }
}
