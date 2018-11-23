package com.project.PotHole.async;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class AsyncHttpPost extends AsyncTask {
    AsyncHandler handler;

    public AsyncHttpPost(AsyncHandler handler) {
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Object... params) {
        byte[] result = null;
        String str = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost((String) params[0]);
        try {
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return str;
    }

    @Override
    protected void onPostExecute(Object output) {
        handler.handleResponse((String) output);
    }

}