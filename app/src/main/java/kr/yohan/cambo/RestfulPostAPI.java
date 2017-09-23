package kr.yohan.cambo;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 3330MT on 2017-01-16.
 */

public class RestfulPostAPI extends AsyncTask<String, Void, String> {

    OnCompletionListener listener = null;

    public RestfulPostAPI(OnCompletionListener listener) {
        this.listener = listener;
    }


    protected String doInBackground(String... params) {
        String urlStr = params[0];
        URL url = null;
        String xml = "";
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStream os = connection.getOutputStream();
            os.write(params[1].getBytes("euc-kr"));
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();

            // convert inputstream to string
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                xml += line;
            }
        } catch (Exception e) {
            xml = "0";
            e.printStackTrace();
        }
        return xml;
    }

    protected void onPostExecute(String result) {
        if (listener != null)
            listener.onComplete(result);
    }
}
