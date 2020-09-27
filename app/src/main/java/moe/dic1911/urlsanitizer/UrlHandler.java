package moe.dic1911.urlsanitizer;

import android.net.Uri;
import android.util.Log;

public class UrlHandler {
    private Uri url;
    private BlacklistHandler blh;
    final private String TAG = "030-UrlHandler";

    public UrlHandler(BlacklistHandler bl, String str) {
        url = Uri.parse(str);
        blh = bl;
    }

    public UrlHandler(BlacklistHandler bl, Uri uri) {
        url = uri;
        blh = bl;
    }

    public Uri sanitize() {
        String scheme = url.getScheme(), host = url.getHost(),
                path = url.getPath(), query = url.getQuery();
        Uri.Builder builder = new Uri.Builder().scheme(scheme).authority(host);

        /*Log.d(TAG, scheme);
        Log.d(TAG, host);
        Log.d(TAG, "path "+path);*/

        // fuck amazon
        if (path.split("=")[0].endsWith("ref")) {
            path = path.split("ref")[0];
        }
        builder.path(path);

        // generic
        if (query != null) {
            Log.d(TAG, "has query");

            for (String q : url.getQueryParameterNames()) {
                Log.d(TAG, q);
                if (!blh.isBlacklisted(q)) {
                    Log.d(TAG, "append");
                    builder.appendQueryParameter(q, url.getQueryParameter(q));
                }
            }
        }
        return builder.build();
    }

}
