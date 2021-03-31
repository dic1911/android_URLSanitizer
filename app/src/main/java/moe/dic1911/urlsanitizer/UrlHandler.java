package moe.dic1911.urlsanitizer;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class UrlHandler {
    private Context ctx;
    private Uri url;
    private BlacklistHandler blh;
    private static final String[] shorturl = {"bit.ly", "goo.gl", "reurl.cc", "tinyurl.com"};

    public UrlHandler(Context c, BlacklistHandler bl, String str) {
        ctx = c;
        url = Uri.parse(str);
        blh = bl;
    }

    public UrlHandler(Context c, BlacklistHandler bl, Uri uri) {
        ctx = c;
        url = uri;
        blh = bl;
    }

    public Uri sanitize() {
        String host = url.getHost();
        if (isShorturl(host)) {
            Uri newUrl = unshorten();
            url = (newUrl != null) ? newUrl : url;
        }

        String scheme = url.getScheme(), path = url.getPath(), query = url.getQuery();
        host = url.getHost();

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

    public Uri unshorten() {
        Toast.makeText(ctx, ctx.getString(R.string.unshortening), Toast.LENGTH_SHORT).show();
        Uri result = new UnshortNetworkThread(url).getResult();
        if (result.getHost().equals("error.030")) {
            Toast.makeText(ctx, ctx.getString(R.string.unshorten_err), Toast.LENGTH_SHORT).show();
            return null;
        }
        return result;
    }

    private Boolean isShorturl(String host) {
        for (String s : shorturl) {
            if (s.equals(host))
                return true;
        }
        return false;
    }

}
