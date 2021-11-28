package moe.dic1911.urlsanitizer;

import static moe.dic1911.urlsanitizer.Constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

public class UrlHandler {
    private final Context ctx;
    private Uri url;
    private final BlacklistHandler blh;
    private final SharedPreferences prefs;
    private static final String[] shorturl = {"bit.ly", "goo.gl", "reurl.cc", "tinyurl.com"};

    public UrlHandler(Context c, BlacklistHandler bl, String str) {
        ctx = c;
        url = Uri.parse(str);
        blh = bl;
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    public UrlHandler(Context c, BlacklistHandler bl, Uri uri) {
        ctx = c;
        url = uri;
        blh = bl;
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    public Uri sanitize() {
        String host = url.getHost();
        if (isShorturl(host)) {
            Uri newUrl = unshorten();
            url = (newUrl != null) ? newUrl : url;
        }

        String scheme = url.getScheme(), path = url.getPath(), query = url.getQuery();

        Uri.Builder builder = new Uri.Builder().scheme(scheme).authority(host);

        // fuck amazon
        if (path.split("=")[0].endsWith("ref"))
            path = path.split("ref")[0];

        builder.path(path);

        if (query != null)
            for (String q : url.getQueryParameterNames())
                if (!blh.isBlacklisted(host, q))
                    builder.appendQueryParameter(q, url.getQueryParameter(q));

        // Privacy Redirect :)
        host = checkHostForAlternative(url.getHost());
        if (PIXIV_DOMAINS.contains(host) && prefs.getBoolean(PREFS_REDIR_PIXIV, true)) {
            return pixivHandler(url);
        } else if (host.equals("moptt.tw") && prefs.getBoolean(PREFS_REDIR_PIXIV, true)) {
            return mopttHandler(url);
        }


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

    private String checkHostForAlternative(String host) {
        SharedPreferences prefs = ctx.getSharedPreferences("main", Context.MODE_PRIVATE);
        host = host.toLowerCase();
        if (prefs.getBoolean(PREFS_PRIVACY_REDIRECT, true)) {
            if (prefs.getBoolean(PREFS_REDIR_YOUTUBE, true) &&
                YOUTUBE_DOMAINS.contains(host)) {
                return prefs.getString(PREFS_REDIR_YOUTUBE_TARGET, DEFAULT_YOUTUBE_TARGET);
            } else if (prefs.getBoolean(PREFS_REDIR_TWITTER, true) &&
                TWITTER_DOMAINS.contains(host)) {
                return prefs.getString(PREFS_REDIR_TWITTER_TARGET, DEFAULT_TWITTER_TARGET);
            } else if (prefs.getBoolean(PREFS_REDIR_REDDIT, true) &&
                    REDDIT_DOMAINS.contains(host)) {
                return prefs.getString(PREFS_REDIR_REDDIT_TARGET, DEFAULT_REDDIT_TARGET);
            } else if (prefs.getBoolean(PREFS_REDIR_INSTAGRAM, true) &&
                    INSTAGRAM_DOMAINS.contains(host)) {
                return prefs.getString(PREFS_REDIR_INSTAGRAM_TARGET, DEFAULT_INSTAGRAM_TARGET);
            }
        }
        return host;
    }

    private Uri pixivHandler(Uri url) {
        String id;
        if (url.getAuthority().equals(PIXIV_DOMAINS.get(1))) {
            // fix pximg links
            String[] splitted = url.getPath().split("/");
            splitted = splitted[splitted.length - 1].split("_"); // ID, page index, (UNUSED)
            id = splitted[0];
            String index = splitted[1].replace("p", "");

            if (!index.equals("0"))
                id += ("-" + index);
        } else if (url.getQueryParameterNames().contains("illust_id")) {
            id = url.getQueryParameter("illust_id");
        } else {
            String[] path = url.getPath().split("/");
            id = path[path.length - 1];
        }
        return new Uri.Builder().scheme("https").authority("pixiv.cat").path(id + ".jpg").build();
    }

    private Uri mopttHandler(Uri url) {
        Uri.Builder ret = new Uri.Builder().scheme("https").authority("www.ptt.cc");
        String path = "bbs/";

        String tmp = url.getPath().split("/")[2];
        String[] splitted = tmp.split("\\.");
        path += splitted[0] + "/" + tmp.replace(splitted[0] + ".", "") + ".html";
        ret.path(path);

        return ret.build();
    }
}
