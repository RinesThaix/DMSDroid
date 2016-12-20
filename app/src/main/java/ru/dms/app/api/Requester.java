package ru.dms.app.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by RinesThaix on 19.12.16.
 */

public class Requester {

    private final static String BASE_URL = "https://api.dms.yt/methods/";

    private static InputStream execute(String link) throws IOException {
        String[] spl = link.split("\\?");
        String urlClean = spl[0], params = spl[1];
        URL url = new URL(urlClean);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        byte[] out = params.getBytes("UTF-8");
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        OutputStream os = http.getOutputStream();
        os.write(out);
        os.close();
        return http.getInputStream();
    }

    private static String process(String url) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(execute(url), "UTF-8"));
        String line = reader.readLine();
        reader.close();
        return line;
    }

    public static JSONObject get(Params params) {
        try {
            return new JSONObject(process(BASE_URL + params.toString()));
        }catch(Exception ex) {
            return null;
        }
    }

}
