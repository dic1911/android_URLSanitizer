package moe.dic1911.urlsanitizer;

import static moe.dic1911.urlsanitizer.Constants.PREFS_BLACKLIST;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public class BlacklistHandler {
    private static SharedPreferences prefs;
    private static HashSet<String> blacklist; // union of defaults + custom (runtime)
    private static HashSet<String> customBlacklist; // only user-defined (persisted)
    private static HashSet<Pattern> blacklistRegex;
    private static BlacklistHandler blh;
    private static final HashSet<String> defaultEntries = new HashSet<>();

    private void addDefaultEntry(String entry) {
        // add to in-memory only; do not persist
        addEntry(entry, false, false);
        defaultEntries.add(entry);
    }

    private static Pattern compileWildcardEntryToPattern(String entry) {
        // Support optional host limiter after '@'. If no host specified, allow any host.
        String e = entry;
        String h = null;
        int at = entry.indexOf('@');
        if (at >= 0) {
            e = entry.substring(0, at);
            h = entry.substring(at + 1);
        }

        // If the user explicitly provides a raw regex with prefix "regex:" or wrapped with slashes /.../,
        // compile it as-is (case-insensitive), matching against query@host.
        if (e.startsWith("regex:")) {
            String raw = e.substring("regex:".length());
            String pattern = raw.contains("@") ? raw : ((h != null && !h.isEmpty()) ? raw + "@" + h : raw + "@.*");
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
        if (e.length() >= 2 && e.startsWith("/") && e.endsWith("/")) {
            String raw = e.substring(1, e.length() - 1);
            String pattern = raw.contains("@") ? raw : ((h != null && !h.isEmpty()) ? raw + "@" + h : raw + "@.*");
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }

        // Convert wildcard syntax in both parts to regex.
        String eRegex = wildcardToRegex(e);
        String hRegex = (h == null || h.isEmpty()) ? ".*" : wildcardToRegex(h);
        String finalRegex = "^" + eRegex + "@" + hRegex + "$";
        return Pattern.compile(finalRegex, Pattern.CASE_INSENSITIVE);
    }

    private static String wildcardToRegex(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '*':
                    out.append(".*");
                    break;
                case '.': case '\\': case '+': case '?': case '^': case '$':
                case '(': case ')': case '[': case ']': case '{': case '}': case '|':
                    out.append('\\').append(c);
                    break;
                default:
                    out.append(c);
            }
        }
        return out.toString();
    }

    public static BlacklistHandler getInstance() {
        return blh;
    }

    private static void rebuildRegexCache() {
        if (blacklist == null) {
            blacklistRegex = new HashSet<>();
            return;
        }
        HashSet<Pattern> newSet = new HashSet<>(blacklist.size());
        for (String q : blacklist) {
            if (q == null) continue;
            String trimmed = q.trim();
            if (trimmed.isEmpty()) continue;
            try {
                newSet.add(compileWildcardEntryToPattern(trimmed));
            } catch (Exception e) {
                Log.e("030-regex", "failed to compile regex for str " + trimmed, e);
            }
        }
        blacklistRegex = newSet;
    }

    public BlacklistHandler(Context c) {
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
        blacklist = new HashSet<>();
        customBlacklist = new HashSet<>();

        // Load persisted entries as custom rules (if any)
        String persisted = prefs.getString(PREFS_BLACKLIST, "");
        if (persisted != null && !persisted.isEmpty()) {
            Collections.addAll(customBlacklist, persisted.split(","));
        }

        // Build default + custom in-memory
        initializeOrUpdate();

        if (blh == null) blh = this;
    }

    public void initializeOrUpdate() {
        // reset the in-memory union set and load defaults first
        blacklist.clear();
        // default blacklisted shit here
        // generic share/clipboard id (ex. fbclid, igshid...)
        addDefaultEntry("*shid");
        addDefaultEntry("*clid");
        addDefaultEntry("*fb_action*");
        addDefaultEntry("*fb_*");
        addDefaultEntry("*gs_l");
        addDefaultEntry("*mkt_tok");
        addDefaultEntry("*otm_*");
        addDefaultEntry("*cmpid");
        addDefaultEntry("*_ga");
        addDefaultEntry("*_gl");
        addDefaultEntry("*__twitter_impression");
        addDefaultEntry("*wt_*");
        addDefaultEntry("*wtrid");
        addDefaultEntry("Echobox");
        addDefaultEntry("*spm");
        addDefaultEntry("*vn*");
        addDefaultEntry("*tracking_source");
        addDefaultEntry("*ceneo_spo");
        addDefaultEntry("*itm*");
        addDefaultEntry("*__s");
        addDefaultEntry("*__hsfp");
        addDefaultEntry("*__hssc");
        addDefaultEntry("*__hstc");
        addDefaultEntry("*hsCtaTracking");
        addDefaultEntry("*mc_*");
        addDefaultEntry("*ml_subscriber*");
        addDefaultEntry("*msclkid");
        addDefaultEntry("*oly_anon_id");
        addDefaultEntry("*oly_enc_id");
        addDefaultEntry("*rb_clickid");
        addDefaultEntry("*s_cid");
        addDefaultEntry("*vero_*");
        addDefaultEntry("*wickedid");

        // Google analytics
        addDefaultEntry("utm*");

        // Action Map
        addDefaultEntry("action_object_map");
        addDefaultEntry("action_type_map");
        addDefaultEntry("action_ref_map");

        // AliExpress
        addDefaultEntry("spm@aliexpress.com");
        addDefaultEntry("scm@aliexpress.com");
        addDefaultEntry("aff_platform");
        addDefaultEntry("aff_trace_key");

        // Amazon
        addDefaultEntry("pd_rd_*@amazon.*");
        addDefaultEntry("_encoding@amazon.*");
        addDefaultEntry("psc@amazon.*");
        addDefaultEntry("tag@amazon.*");
        addDefaultEntry("ref*@amazon.*");
        addDefaultEntry("pf_rd_*@amazon.*");
        addDefaultEntry("qid@amazon.*");
        addDefaultEntry("srs@amazon.*");
        addDefaultEntry("spIA@amazon.*");
        addDefaultEntry("ms3_c@amazon.*");
        addDefaultEntry("qualifier@amazon.*");
        addDefaultEntry("smid@amazon.*");
        addDefaultEntry("field_lbr_brands_browse-bin@amazon.*");
        addDefaultEntry("th@amazon.*");
        addDefaultEntry("sprefix@amazon.*");
        addDefaultEntry("crid@amazon.*");
        addDefaultEntry("keywords@amazon.*");
        addDefaultEntry("cv_ct_*@amazon.*");
        addDefaultEntry("linkCode@amazon.*");
        addDefaultEntry("ascsubtag@amazon.*");
        addDefaultEntry("aaxitk@amazon.*");
        addDefaultEntry("hsa_cr_id@amazon.*");
        addDefaultEntry("sb-ci-*@amazon.*");
        addDefaultEntry("rnid@amazon.*");
        addDefaultEntry("dchild@amazon.*");
        addDefaultEntry("camp@amazon.*");
        addDefaultEntry("creative*@amazon.*");
        addDefaultEntry("s@amazon.*");
        addDefaultEntry("content-id@amazon.*");
        addDefaultEntry("dib@amazon.*");
        addDefaultEntry("dib_tag@amazon.*");

        // Amazon Campaign
        addDefaultEntry("trk");
        addDefaultEntry("trkCampaign");
        addDefaultEntry("ga_*");

        // Bilibili
        addDefaultEntry("callback@bilibili.com");
        addDefaultEntry("spm_id_from");

        // Bing
        addDefaultEntry("cvid@bing.com");
        addDefaultEntry("form@bing.com");
        addDefaultEntry("sk@bing.com");
        addDefaultEntry("sp@bing.com");
        addDefaultEntry("sc@bing.com");
        addDefaultEntry("qs@bing.com");
        addDefaultEntry("pq@bing.com");

        // Adobe
        addDefaultEntry("sc_cid");
        addDefaultEntry("mkt_tok");

        // Humble Bundle
        addDefaultEntry("hmb_campaign");
        addDefaultEntry("hmb_medium");
        addDefaultEntry("hmb_source");

        addDefaultEntry("itm_*"); //itm
        addDefaultEntry("pk_*"); // pk

        // MSN
        addDefaultEntry("cvid@msn.com");
        addDefaultEntry("ocid@msn.com");

        // sc campaign
        addDefaultEntry("sc_campaign");
        addDefaultEntry("sc_channel");
        addDefaultEntry("sc_content");
        addDefaultEntry("sc_medium");
        addDefaultEntry("sc_outcome");
        addDefaultEntry("sc_geo");
        addDefaultEntry("sc_country");

        // Facebook
        addDefaultEntry("fb_action_ids");
        addDefaultEntry("fb_action_types");
        addDefaultEntry("fb_ref");
        addDefaultEntry("fb_source");
        addDefaultEntry("gs_l");

        // Google
        addDefaultEntry("ved@google.*");
        addDefaultEntry("bi*@google.*");
        addDefaultEntry("gfe*@google.*");
        addDefaultEntry("ei@google.*");
        addDefaultEntry("sei@google.*");
        addDefaultEntry("source@google.*");
        addDefaultEntry("gs_*@google.*");
        addDefaultEntry("gws_*@google.*");
        addDefaultEntry("site@google.*");
        addDefaultEntry("oq@google.*");
        addDefaultEntry("esrc@google.*");
        addDefaultEntry("uact@google.*");
        addDefaultEntry("cd@google.*");
        addDefaultEntry("cad@google.*");
        addDefaultEntry("atyp@google.*");
        addDefaultEntry("vet@google.*");
        addDefaultEntry("_u@google.*");
        addDefaultEntry("je@google.*");
        addDefaultEntry("dcr@google.*");
        addDefaultEntry("btn*@google.*");
        addDefaultEntry("usg@google.*");
        addDefaultEntry("cd@google.*");
        addDefaultEntry("cad@google.*");
        addDefaultEntry("aqs@google.*");
        addDefaultEntry("sxsrf@google.*");
        addDefaultEntry("rlz@google.*");
        addDefaultEntry("i-would-rather-use-firefox@google.*");
        addDefaultEntry("pcampaignid@google.*");
        addDefaultEntry("sca_esv@google.*");
        addDefaultEntry("client@google.*");
        addDefaultEntry("sclient@google.*");

        // Hubspot
        addDefaultEntry("_hsenc");
        addDefaultEntry("_hsmi");
        addDefaultEntry("__hssc");
        addDefaultEntry("__hstc");
        addDefaultEntry("hsCtaTracking");

        // IBM
        addDefaultEntry("spReportId");
        addDefaultEntry("spJobID");
        addDefaultEntry("spUserID");
        addDefaultEntry("spMailingID");

        // Oracle Eloqua
        addDefaultEntry("elqTrackId");
        addDefaultEntry("elqTrack");
        addDefaultEntry("assetType");
        addDefaultEntry("assetId");
        addDefaultEntry("recipientId");
        addDefaultEntry("campaignId");
        addDefaultEntry("siteId");

        // Sourceforge
        addDefaultEntry("source@sourceforge.net");
        addDefaultEntry("position@sourceforge.net");

        // twitter
        addDefaultEntry("s@twitter.com");
        addDefaultEntry("t@twitter.com");
        addDefaultEntry("t@x.com");
        addDefaultEntry("s@x.com");
        addDefaultEntry("si@x.com");

        // Yandex
        addDefaultEntry("_openstat");

        addDefaultEntry("mbid");
        addDefaultEntry("cmpid");
        addDefaultEntry("cid");
        addDefaultEntry("c_id");
        addDefaultEntry("campaign_id");
        addDefaultEntry("Campaign");

        // Youtube
        addDefaultEntry("feature@youtube.com");
        addDefaultEntry("kw@youtube.com");
        addDefaultEntry("si@youtu.be");
        addDefaultEntry("si@youtube.com");

        // Zeit.de
        addDefaultEntry("wt_zmc");

        // Spotify
        addDefaultEntry("si@*.spotify.com");

        // Merge previously saved custom rules into the runtime blacklist
        if (customBlacklist != null && !customBlacklist.isEmpty()) {
            blacklist.addAll(customBlacklist);
        }

        // One-time cleanup: remove default entries that were persisted by older versions
        if (customBlacklist != null && !defaultEntries.isEmpty()) {
            boolean changed = customBlacklist.removeAll(defaultEntries);
            if (changed) {
                prefs.edit().putString(PREFS_BLACKLIST, buildPrefs()).apply();
            }
        }

        // Now compile the regex cache for all active rules (defaults + custom)
        rebuildRegexCache();
    }

    public Boolean isBlacklisted(String host, String query) {
        String h = (host == null) ? "" : host;
        String combined = query + "@" + h;
        boolean log = h.contains("threads");
        if (log) Log.d("030-?", String.format("%s %s", host, query));
        if (blacklistRegex != null) {
            for (Pattern p : blacklistRegex) {
                try {
                    if (p.matcher(combined).matches()) {
                        if (log) Log.d("030-??", p.pattern() + " matched");
                        return true;
                    }
                    if (log) Log.d("030-??", p.pattern() + " mismatch");
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    public Boolean addEntry(String query) {
        return addEntry(query, true, true);
    }

    public Boolean addEntry(String query, boolean save, boolean rebuildCache) {
        if (blacklist.contains(query))
            return false;
        blacklist.add(query);

        if (blacklistCache != null)
            blacklistCache.add(query);

        if (save) {
            // persist to custom rules only
            customBlacklist.add(query);
            prefs.edit()
                    .putString(PREFS_BLACKLIST, buildPrefs())
                    .apply();
        }
        // keep regex cache in sync (defaults + custom)
        if (rebuildCache) rebuildRegexCache();
        return true;
    }

    public Boolean removeEntry(String value) {
        boolean removed = false;
        if (blacklist.remove(value)) removed = true;
        if (customBlacklist != null && customBlacklist.remove(value)) removed = true;

        if (removed) {
            if (blacklistCache != null)
                blacklistCache.remove(value);
            // persist only custom rules
            prefs.edit().putString(PREFS_BLACKLIST, buildPrefs()).apply();
            rebuildRegexCache();
            return true;
        }
        return false;
    }

    public void resetAll() {
        blacklist = new HashSet<>();
        customBlacklist = new HashSet<>();
        blacklistCache = null;
        // clear persisted custom rules
        prefs.edit().remove(PREFS_BLACKLIST).apply();
        initializeOrUpdate();
        rebuildRegexCache();
    }

    private List<String> blacklistCache;
    public String getEntry(int index) {
        if (blacklistCache == null) {
            blacklistCache = List.copyOf(blacklist);
        }
        return blacklistCache.get(index);
    }

    public int getBlacklistSize() {
        return blacklist.size();
    }

    public String buildPrefs() {
        // Persist only custom rules; defaults are always reloaded at runtime
        StringBuilder sb = new StringBuilder();
        if (customBlacklist != null) {
            for (String entry : customBlacklist)
                sb.append(entry).append(",");
        }
        return sb.length() > 0 ? sb.subSequence(0, sb.length() - 1).toString() : "";
    }
}
