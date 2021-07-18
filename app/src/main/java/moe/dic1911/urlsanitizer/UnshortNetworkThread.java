package moe.dic1911.urlsanitizer;

import android.net.Uri;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UnshortNetworkThread extends Thread implements Runnable {
    private Uri url, result = null;

    UnshortNetworkThread(Uri source) {
        this.url = source;
    }

    @Override
    public void run() {
        String result = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url.toString()).openConnection();
            con.setInstanceFollowRedirects(false);
            while (con.getResponseCode() / 100 == 3) {
                result = con.getHeaderField("location");
                con = (HttpURLConnection) new URL(result).openConnection();
            }
        } catch (IOException e) {
            result = "http://error.030/";
        }
        this.result = Uri.parse(result);
    }

    public Uri getResult() {
        if (result == null) {
            start();
            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
