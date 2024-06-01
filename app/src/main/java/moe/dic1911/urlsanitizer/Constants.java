package moe.dic1911.urlsanitizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static final String PREFS_BLACKLIST = "blacklist";
    public static final String PREFS_PRIVACY_REDIRECT = "priv_redir";
    public static final String PREFS_REDIR_YOUTUBE = "priv_redir_yt";
    public static final String PREFS_REDIR_YOUTUBE_TARGET = "priv_redir_yt_target";
    public static final String PREFS_REDIR_TWITTER = "priv_redir_twi";
    public static final String PREFS_REDIR_TWITTER_TARGET = "priv_redir_twi_target";
    public static final String PREFS_REDIR_REDDIT = "priv_redir_rdt";
    public static final String PREFS_REDIR_REDDIT_TARGET = "priv_redir_rdt_target";
    public static final String PREFS_REDIR_INSTAGRAM = "priv_redir_ig";
    public static final String PREFS_REDIR_INSTAGRAM_TARGET = "priv_redir_ig_target";
    public static final String PREFS_REDIR_MOPTT = "priv_redir_moptt";
    public static final String PREFS_REDIR_PIXIV = "priv_redir_pixiv";
    public static final String PREFS_REDIR_TWIMG = "priv_redir_twimg";

    public static final String DEFAULT_YOUTUBE_TARGET = "incogtube.com";
    public static final String DEFAULT_TWITTER_TARGET = "nitter.net";
    public static final String DEFAULT_REDDIT_TARGET = "teddit.net";
    public static final String DEFAULT_INSTAGRAM_TARGET = "bibliogram.art";

    public static final List<String> YOUTUBE_DOMAINS = Arrays.asList("youtu.be", "youtube.com", "www.youtube.com");
    public static final List<String> TWITTER_DOMAINS = Arrays.asList("twitter.com", "mobile.twitter.com", "x.com", "mobile.x.com");
    public static final List<String> REDDIT_DOMAINS = Arrays.asList("reddit.com", "www.reddit.com", "old.reddit.com");
    public static final List<String> INSTAGRAM_DOMAINS = Arrays.asList("instagram.com", "www.instagram.com", "instagr.am", "instagr.com");

    public static final List<String> PIXIV_DOMAINS = Arrays.asList("www.pixiv.net", "i.pximg.net");
}
