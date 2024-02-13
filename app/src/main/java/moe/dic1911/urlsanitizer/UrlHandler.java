package moe.dic1911.urlsanitizer;

import static moe.dic1911.urlsanitizer.Constants.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlHandler {
    private final Context ctx;
    private String data;
    private Uri url;
    private ArrayList<Uri> urls;
    private final BlacklistHandler blh;
    private final SharedPreferences prefs;
    private static final String[] shorturl = {"bit.ly", "goo.gl", "reurl.cc", "t.co", "tinyurl.com"};

    // Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.\\!\\/\\\\w]*))?)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public UrlHandler(Context c, BlacklistHandler bl, String str) {
        ctx = c;
        Matcher urlMatcher = urlPattern.matcher(str);
        data = str;
        urls = new ArrayList<>();
        while (urlMatcher.find()) {
            int matchStart = urlMatcher.start(1);
            int matchEnd = urlMatcher.end();
            String match = str.substring(matchStart, matchEnd);
            Log.d("urlsan-match", match);
            try {
                urls.add(Uri.parse(match));
            } catch (Exception ignored) {}
        }
        blh = bl;
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    public UrlHandler(Context c, BlacklistHandler bl, Uri uri) {
        ctx = c;
        url = uri;
        blh = bl;
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    private Uri doSanitize(Uri source) {
        String host = source.getHost(), oHost = host;
        if (host == null) return source;
        if (isShorturl(host)) {
            Uri newUrl = unshorten(source);
            source = (newUrl != null) ? newUrl : source;

            // update host after handling short url
            host = source.getHost();
            oHost = host;
        }

        String scheme = source.getScheme(), path = source.getPath(), query = source.getQuery();

        // Privacy Redirect :)
        host = checkHostForAlternative(host);
        if (PIXIV_DOMAINS.contains(host) && prefs.getBoolean(PREFS_REDIR_PIXIV, true)) {
            return pixivHandler(source);
        } else if (host.equals("moptt.tw") && prefs.getBoolean(PREFS_REDIR_MOPTT, true)) {
            return mopttHandler(source);
        } else if (host.equals("pbs.twimg.com") && prefs.getBoolean(PREFS_REDIR_TWIMG, true)) {
            return twimgHandler(source);
        }

        Uri.Builder builder = new Uri.Builder().scheme(scheme).authority(host);

        // fuck amazon
        if (path.split("=")[0].endsWith("ref"))
            path = path.split("ref")[0];

        builder.path(path);

        if (query != null)
            for (String q : source.getQueryParameterNames())
                if (!blh.isBlacklisted(oHost, q))
                    builder.appendQueryParameter(q, source.getQueryParameter(q));


        return builder.build();
    }

    public String sanitize() {
        if (url != null) {
            return doSanitize(url).toString();
        }
        for (int i = 0; i < urls.size(); i++) {
            Uri source = urls.get(i);
            Uri sanitizedUri = doSanitize(source);
            String sanitized = sanitizedUri.toString();
            data = data.replace(source.toString(), sanitized);
            urls.set(i, sanitizedUri);
        }
        return data;
    }

    public Uri getFirstUri() {
        return urls.size() > 0 ? urls.get(0) : null;
    }

    public Uri unshorten(Uri source) {
        Toast.makeText(ctx, ctx.getString(R.string.unshortening), Toast.LENGTH_SHORT).show();
        Uri result = new UnshortNetworkThread(source).getResult();
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

    private Uri twimgHandler(Uri url) {
        String path = url.getPath().split("\\.")[0];
        Uri.Builder ret = new Uri.Builder().scheme(url.getScheme()).authority(url.getHost()).path(path);
        ret.appendQueryParameter("format", "png").appendQueryParameter("name", "large");
        return ret.build();
    }
}
