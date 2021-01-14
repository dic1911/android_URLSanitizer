package moe.dic1911.urlsanitizer;

import android.net.Uri;

public class UrlHandler {
    private Uri url;
    private BlacklistHandler blh;

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

        // fuck amazon
        if (path.split("=")[0].endsWith("ref"))
            path = path.split("ref")[0];

        builder.path(path);

        if (query != null)
            for (String q : url.getQueryParameterNames())
                if (!blh.isBlacklisted(host, q))
                    builder.appendQueryParameter(q, url.getQueryParameter(q));

        return builder.build();
    }

}
