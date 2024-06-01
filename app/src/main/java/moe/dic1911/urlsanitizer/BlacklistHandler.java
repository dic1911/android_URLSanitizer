package moe.dic1911.urlsanitizer;

import static moe.dic1911.urlsanitizer.Constants.PREFS_BLACKLIST;

import android.content.Context;
import android.content.SharedPreferences;

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
        Collections.addAll(blacklist, prefs.getString(PREFS_BLACKLIST, "").split(","));
        initializeOrUpdate();

        if (blh == null) blh = this;
    }

    public void initializeOrUpdate() {
        // default blacklisted shit here
        // generic share/clipboard id (ex. fbclid, igshid...)
        addEntry("*shid", false);
        addEntry("*clid", false);
        addEntry("*fb_action*", false);
        addEntry("*fb_*", false);
        addEntry("*gs_l", false);
        addEntry("*mkt_tok", false);
        addEntry("*otm_*", false);
        addEntry("*cmpid", false);
        addEntry("*_ga", false);
        addEntry("*_gl", false);
        addEntry("*__twitter_impression", false);
        addEntry("*wt_*", false);
        addEntry("*wtrid", false);
        addEntry("Echobox", false);
        addEntry("*spm", false);
        addEntry("*vn*", false);
        addEntry("*tracking_source", false);
        addEntry("*ceneo_spo", false);
        addEntry("*itm*", false);
        addEntry("*__s", false);
        addEntry("*__hsfp", false);
        addEntry("*__hssc", false);
        addEntry("*__hstc", false);
        addEntry("*hsCtaTracking", false);
        addEntry("*mc_*", false);
        addEntry("*ml_subscriber*", false);
        addEntry("*msclkid", false);
        addEntry("*oly_anon_id", false);
        addEntry("*oly_enc_id", false);
        addEntry("*rb_clickid", false);
        addEntry("*s_cid", false);
        addEntry("*vero_*", false);
        addEntry("*wickedid", false);

        // Google analytics
        addEntry("utm*", false);

        // Action Map
        addEntry("action_object_map", false);
        addEntry("action_type_map", false);
        addEntry("action_ref_map", false);

        // AliExpress
        addEntry("spm@aliexpress.com", false);
        addEntry("scm@aliexpress.com", false);
        addEntry("aff_platform", false);
        addEntry("aff_trace_key", false);

        // Amazon
        addEntry("pd_rd_*@amazon.*", false);
        addEntry("_encoding@amazon.*", false);
        addEntry("psc@amazon.*", false);
        addEntry("tag@amazon.*", false);
        addEntry("ref*@amazon.*", false);
        addEntry("pf_rd_*@amazon.*", false);
        addEntry("qid@amazon.*", false);
        addEntry("srs@amazon.*", false);
        addEntry("spIA@amazon.*", false);
        addEntry("ms3_c@amazon.*", false);
        addEntry("qualifier@amazon.*", false);
        addEntry("smid@amazon.*", false);
        addEntry("field_lbr_brands_browse-bin@amazon.*", false);
        addEntry("th@amazon.*", false);
        addEntry("sprefix@amazon.*", false);
        addEntry("crid@amazon.*", false);
        addEntry("keywords@amazon.*", false);
        addEntry("cv_ct_*@amazon.*", false);
        addEntry("linkCode@amazon.*", false);
        addEntry("ascsubtag@amazon.*", false);
        addEntry("aaxitk@amazon.*", false);
        addEntry("hsa_cr_id@amazon.*", false);
        addEntry("sb-ci-*@amazon.*", false);
        addEntry("rnid@amazon.*", false);
        addEntry("dchild@amazon.*", false);
        addEntry("camp@amazon.*", false);
        addEntry("creative*@amazon.*", false);
        addEntry("s@amazon.*", false);
        addEntry("content-id@amazon.*", false);
        addEntry("dib@amazon.*", false);
        addEntry("dib_tag@amazon.*", false);

        // Amazon Campaign
        addEntry("trk", false);
        addEntry("trkCampaign", false);
        addEntry("ga_*", false);

        // Bilibili
        addEntry("callback@bilibili.com", false);
        addEntry("spm_id_from", false);

        // Bing
        addEntry("cvid@bing.com", false);
        addEntry("form@bing.com", false);
        addEntry("sk@bing.com", false);
        addEntry("sp@bing.com", false);
        addEntry("sc@bing.com", false);
        addEntry("qs@bing.com", false);
        addEntry("pq@bing.com", false);

        // Adobe
        addEntry("sc_cid", false);
        addEntry("mkt_tok", false);

        // Humble Bundle
        addEntry("hmb_campaign", false);
        addEntry("hmb_medium", false);
        addEntry("hmb_source", false);

        addEntry("itm_*", false); //itm
        addEntry("pk_*", false); // pk

        // MSN
        addEntry("cvid@msn.com", false);
        addEntry("ocid@msn.com", false);

        // sc campaign
        addEntry("sc_campaign", false);
        addEntry("sc_channel", false);
        addEntry("sc_content", false);
        addEntry("sc_medium", false);
        addEntry("sc_outcome", false);
        addEntry("sc_geo", false);
        addEntry("sc_country", false);

        // Facebook
        addEntry("fb_action_ids", false);
        addEntry("fb_action_types", false);
        addEntry("fb_ref", false);
        addEntry("fb_source", false);
        addEntry("gs_l", false);

        // Google
        addEntry("ved@google.*", false);
        addEntry("bi*@google.*", false);
        addEntry("gfe*@google.*", false);
        addEntry("ei@google.*", false);
        addEntry("sei@google.*", false);
        addEntry("source@google.*", false);
        addEntry("gs_*@google.*", false);
        addEntry("gws_*@google.*", false);
        addEntry("site@google.*", false);
        addEntry("oq@google.*", false);
        addEntry("esrc@google.*", false);
        addEntry("uact@google.*", false);
        addEntry("cd@google.*", false);
        addEntry("cad@google.*", false);
        addEntry("atyp@google.*", false);
        addEntry("vet@google.*", false);
        addEntry("_u@google.*", false);
        addEntry("je@google.*", false);
        addEntry("dcr@google.*", false);
        addEntry("btn*@google.*", false);
        addEntry("usg@google.*", false);
        addEntry("cd@google.*", false);
        addEntry("cad@google.*", false);
        addEntry("aqs@google.*", false);
        addEntry("sxsrf@google.*", false);
        addEntry("rlz@google.*", false);
        addEntry("i-would-rather-use-firefox@google.*", false);
        addEntry("pcampaignid@google.*", false);
        addEntry("sca_esv@google.*", false);
        addEntry("client@google.*", false);
        addEntry("sclient@google.*", false);

        // Hubspot
        addEntry("_hsenc", false);
        addEntry("_hsmi", false);
        addEntry("__hssc", false);
        addEntry("__hstc", false);
        addEntry("hsCtaTracking", false);

        // IBM
        addEntry("spReportId", false);
        addEntry("spJobID", false);
        addEntry("spUserID", false);
        addEntry("spMailingID", false);

        // Oracle Eloqua
        addEntry("elqTrackId", false);
        addEntry("elqTrack", false);
        addEntry("assetType", false);
        addEntry("assetId", false);
        addEntry("recipientId", false);
        addEntry("campaignId", false);
        addEntry("siteId", false);

        // Sourceforge
        addEntry("source@sourceforge.net", false);
        addEntry("position@sourceforge.net", false);

        // twitter
        addEntry("s@twitter.com", false);
        addEntry("t@twitter.com", false);
        addEntry("t@x.com", false);
        addEntry("s@x.com", false);
        addEntry("si@x.com", false);

        // Yandex
        addEntry("_openstat", false);

        addEntry("mbid", false);
        addEntry("cmpid", false);
        addEntry("cid", false);
        addEntry("c_id", false);
        addEntry("campaign_id", false);
        addEntry("Campaign", false);

        // Youtube
        addEntry("feature@youtube.com", false);
        addEntry("kw@youtube.com", false);
        addEntry("si@youtu.be", false);
        addEntry("si@youtube.com", false);

        // Zeit.de
        addEntry("wt_zmc", false);

        // Spotify
        addEntry("si@*.spotify.com", false);

        String result = buildPrefs();
        prefs.edit().putString(PREFS_BLACKLIST, result).apply();
    }

    public Boolean isBlacklisted(String host, String query) {
        boolean qMatch = false, hMatch = false;
        String h = null, e;
        for (String entry : blacklist) {
            // handle domain limiter
            if (entry.contains("@")) {
                e = entry.split("@")[0];
                h = entry.split("@")[1];
                hMatch = false;
            } else {
                e = entry;
                hMatch = true;
            }

            // handle wildcard
            if (e.endsWith("*"))
                qMatch = query.startsWith(e.substring(0, e.length() - 1));
            else if (!qMatch && e.startsWith("*"))
                qMatch = query.endsWith(e.substring(1));
            else
                qMatch = query.equals(e);

            if (!hMatch) {
                if (h.endsWith("*"))
                    hMatch = host.startsWith(h.substring(0, h.length() - 1));
                else if (h.startsWith("*"))
                    hMatch = host.endsWith(h.substring(1));
                else
                    hMatch = host.equals(h);
            }
            if (qMatch && hMatch) return true;
        }
        return false;
    }

    public Boolean addEntry(String query) {
        return addEntry(query, true);
    }

    public Boolean addEntry(String query, boolean save) {
        if (blacklist.contains(query))
            return false;
        blacklist.add(query);
        if (save) {
            prefs.edit()
                    .putString(PREFS_BLACKLIST, prefs.getString(PREFS_BLACKLIST, "") + "," + query)
                    .apply();
        }
        return true;
    }

    public Boolean removeEntry(int index) {
        try {
            blacklist.remove(index);
            prefs.edit().putString(PREFS_BLACKLIST, buildPrefs()).apply();
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public Boolean removeEntry(String value) {
        return removeEntry(blacklist.indexOf(value));
    }

    public void resetAll() {
        blacklist = new ArrayList<>();
        initializeOrUpdate();
    }

    public String getEntry(int index) {
        return blacklist.get(index);
    }

    public int getBlacklistSize() {
        return blacklist.size();
    }

    public String buildPrefs() {
        StringBuilder sb = new StringBuilder();
        for (String entry : blacklist)
            sb.append(entry).append(",");

        return sb.subSequence(0, sb.length() - 1).toString();
    }
}
