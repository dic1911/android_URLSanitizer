package moe.dic1911.urlsanitizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("SpellCheckingInspection")
public class BlacklistHandler {
    private static SharedPreferences prefs;
    private static ArrayList<String> blacklist;
    private static BlacklistHandler blh;

    public static BlacklistHandler getInstance() {
        return blh;
    }

    public BlacklistHandler(Context c) {
        prefs = c.getSharedPreferences("main", Context.MODE_PRIVATE);
        blacklist = new ArrayList<>();
        if (prefs.contains("blacklist")) {
            Collections.addAll(blacklist, prefs.getString("blacklist", "").split(","));
        } else {
            initialize();
        }
        if (blh == null) blh = this;
    }

    public void initialize() {
        // default blacklisted shit here
        // generic share/clipboard id (ex. fbclid, igshid...)
        blacklist.add("*shid");
        blacklist.add("*clid");

        // twitter
        blacklist.add("s@twitter.com");

        // bilibili
        blacklist.add("spm_id_from");

        // Google analytics
        blacklist.add("utm*");

        // Some more from Neat URL
        // Action Map
        blacklist.add("action_object_map");
        blacklist.add("action_type_map");
        blacklist.add("action_ref_map");
        // AliExpress
        blacklist.add("spm@aliexpress.com");
        blacklist.add("scm@aliexpress.com");
        blacklist.add("aff_platform");
        blacklist.add("aff_trace_key");
        // Amazon
        blacklist.add("pd_rd_*@amazon.*");
        blacklist.add("_encoding@amazon.*");
        blacklist.add("psc@amazon.*");
        blacklist.add("tag@amazon.*");
        blacklist.add("ref_@amazon.*");
        blacklist.add("pf_rd_*@amazon.*");
        // Bilibili
        blacklist.add("callback@bilibili.com");
        // Bing
        blacklist.add("cvid@bing.com");
        blacklist.add("form@bing.com");
        blacklist.add("sk@bing.com");
        blacklist.add("sp@bing.com");
        blacklist.add("sc@bing.com");
        blacklist.add("qs@bing.com");
        blacklist.add("pq@bing.com");
        // Adobe
        blacklist.add("sc_cid");
        blacklist.add("mkt_tok");
        // Amazon Campaign
        blacklist.add("trk");
        blacklist.add("trkCampaign");
        blacklist.add("ga_*");
        // Humble Bundle
        blacklist.add("hmb_campaign");
        blacklist.add("hmb_medium");
        blacklist.add("hmb_source");

        blacklist.add("itm_*"); //itm
        blacklist.add("pk_*"); // pk

        // sc campaign
        blacklist.add("sc_campaign");
        blacklist.add("sc_channel");
        blacklist.add("sc_content");
        blacklist.add("sc_medium");
        blacklist.add("sc_outcome");
        blacklist.add("sc_geo");
        blacklist.add("sc_country");

        // Yandex
        blacklist.add("_openstat");

        blacklist.add("mbid");
        blacklist.add("cmpid");
        blacklist.add("cid");
        blacklist.add("c_id");
        blacklist.add("campaign_id");
        blacklist.add("Campaign");

        // Facebook
        blacklist.add("fb_action_ids");
        blacklist.add("fb_action_types");
        blacklist.add("fb_ref");
        blacklist.add("fb_source");
        blacklist.add("gs_l");
        // Google
        blacklist.add("ved@google.*");
        blacklist.add("ei@google.*");
        blacklist.add("sei@google.*");
        blacklist.add("gws_rd@google.*");
        // Hubspot
        blacklist.add("_hsenc");
        blacklist.add("_hsmi");
        blacklist.add("__hssc");
        blacklist.add("__hstc");
        blacklist.add("hsCtaTracking");
        // IBM
        blacklist.add("spReportId");
        blacklist.add("spJobID");
        blacklist.add("spUserID");
        blacklist.add("spMailingID");
        // Oracle Eloqua
        blacklist.add("elqTrackId");
        blacklist.add("elqTrack");
        blacklist.add("assetType");
        blacklist.add("assetId");
        blacklist.add("recipientId");
        blacklist.add("campaignId");
        blacklist.add("siteId");
        // Sourceforge
        blacklist.add("source@sourceforge.net");
        blacklist.add("position@sourceforge.net");
        // Youtube
        blacklist.add("feature@youtube.com");
        blacklist.add("kw@youtube.com");
        // Zeit.de
        blacklist.add("wt_zmc");

        String result = buildPrefs();
        prefs.edit().putString("blacklist", result).apply();
    }

    public Boolean isBlacklisted(String host, String query) {
        boolean qMatch = false, hMatch = false;
        String h = null, e;
        for (String entry : blacklist) {
            // handle domain limiter
            if (entry.contains("@")) {
                e = entry.split("@")[0];
                h = entry.split("@")[1];
            } else {
                e = entry;
                hMatch = true;
            }

            // handle wildcard
            if (e.endsWith("*"))
                qMatch = query.startsWith(e.substring(0, e.length()-1));
            else if (!qMatch && e.startsWith("*"))
                qMatch = query.endsWith(e.substring(1));
            else
                qMatch = query.equals(e);

            if (!hMatch) {
                if (h.endsWith("*"))
                    hMatch = host.startsWith(h.substring(0, h.length()-1));
                else if (e.startsWith("*"))
                    hMatch = host.endsWith(h.substring(1));
                else
                    hMatch = host.equals(h);
            }
            if (qMatch && hMatch) return true;
        }
        return false;
    }

    public Boolean addEntry(String query) {
        if (blacklist.contains(query))
            return false;
        blacklist.add(query);
        prefs.edit()
            .putString("blacklist", prefs.getString("blacklist", "") + "," + query)
            .apply();
        return true;
    }

    public Boolean removeEntry(int index) {
        try {
            blacklist.remove(index);
            prefs.edit().putString("blacklist", buildPrefs()).apply();
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public Boolean removeEntry(String value) {
        return removeEntry(blacklist.indexOf(value));
    }

    public String getEntry(int index) {
        return blacklist.get(index);
    }

    public int getBlacklistSize() {
        return blacklist.size();
    }

    private String buildPrefs() {
        StringBuilder sb = new StringBuilder();
        for (String entry : blacklist)
            sb.append(entry).append(",");

        return sb.subSequence(0, sb.length()-1).toString();
    }
}
