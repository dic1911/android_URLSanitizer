package moe.dic1911.urlsanitizer;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static moe.dic1911.urlsanitizer.Constants.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PrivacyRedirectActivity extends AppCompatActivity {

    private static SharedPreferences prefs;
    private SwitchMaterial master, yt, twi, rdt, ig, moptt, pixiv, twimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_redirect);
        prefs = getApplicationContext().getSharedPreferences("main", Context.MODE_PRIVATE);

        // Setup switches & text inputs
        master = findViewById(R.id.sw_priv_redir);
        yt = findViewById(R.id.sw_redir_yt);
        twi = findViewById(R.id.sw_redir_twi);
        rdt = findViewById(R.id.sw_redir_rdt);
        ig = findViewById(R.id.sw_redir_ig);
        moptt = findViewById(R.id.sw_redir_moptt);
        pixiv = findViewById(R.id.sw_redir_pixiv);
        twimg = findViewById(R.id.sw_redir_twimg);

        setupSwitch(master, PREFS_PRIVACY_REDIRECT);

        setupSwitch(yt, PREFS_REDIR_YOUTUBE);
        setupDomainInput(R.id.txt_yt_target, PREFS_REDIR_YOUTUBE_TARGET, DEFAULT_YOUTUBE_TARGET);

        setupSwitch(twi, PREFS_REDIR_TWITTER);
        setupDomainInput(R.id.txt_twi_target, PREFS_REDIR_TWITTER_TARGET, DEFAULT_TWITTER_TARGET);

        setupSwitch(rdt, PREFS_REDIR_REDDIT);
        setupDomainInput(R.id.txt_rdt_target, PREFS_REDIR_REDDIT_TARGET, DEFAULT_REDDIT_TARGET);

        setupSwitch(ig, PREFS_REDIR_INSTAGRAM);
        setupDomainInput(R.id.txt_ig_target, PREFS_REDIR_INSTAGRAM_TARGET, DEFAULT_INSTAGRAM_TARGET);

        setupSwitch(moptt, PREFS_REDIR_MOPTT);
        setupSwitch(pixiv, PREFS_REDIR_PIXIV);
        setupSwitch(twimg, PREFS_REDIR_TWIMG);

        refreshUIState();
    }

    private void setupDomainInput(int txtLayoutId, String prefsKey, String defaultVal) {
        final EditText txt = findViewById(txtLayoutId);
        if (!prefs.contains(prefsKey))
            prefs.edit().putString(prefsKey, defaultVal).apply();

        txt.setText(prefs.getString(prefsKey, defaultVal));
        txt.setOnKeyListener(new View.OnKeyListener() {
            private int c = 0;

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    Log.d("030", "ENTER");
                    // temp. workaround. this gets triggered twice in one tap somehow
                    if ((++c) % 2 == 1) {
                        String key = null;
                        String value = ((EditText)view).getText().toString();
                        if (view.getId() == R.id.txt_yt_target)
                            key = PREFS_REDIR_YOUTUBE_TARGET;
                        else if (view.getId() == R.id.txt_twi_target)
                            key = PREFS_REDIR_TWITTER_TARGET;
                        else if (view.getId() == R.id.txt_rdt_target)
                            key = PREFS_REDIR_REDDIT_TARGET;
                        else if (view.getId() == R.id.txt_ig_target)
                            key = PREFS_REDIR_INSTAGRAM_TARGET;
                        prefs.edit().putString(key, value).apply();

                        Snackbar.make(view, getString(R.string.noti_saved), Snackbar.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private boolean setupSwitch(final SwitchMaterial mSwitch, final String prefsKey) {
        boolean ret = true;
        if (!prefs.contains(prefsKey))
            prefs.edit().putBoolean(prefsKey, true).apply();
        else
            ret = prefs.getBoolean(prefsKey, true);

        mSwitch.setChecked(ret);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean(prefsKey, mSwitch.isChecked()).apply();
                Log.d("030-redirSw1", String.valueOf(mSwitch.isChecked()));
                refreshUIState();
            }
        });

        if (!master.isChecked() && mSwitch != master)
            mSwitch.setEnabled(false);

        return ret;
    }

    private void refreshUIState() {
        yt.setEnabled(master.isChecked());
        twi.setEnabled(master.isChecked());
        rdt.setEnabled(master.isChecked());
        ig.setEnabled(master.isChecked());
        moptt.setEnabled(master.isChecked());
        pixiv.setEnabled(master.isChecked());
        findViewById(R.id.txt_yt_target).setEnabled(master.isChecked() && yt.isChecked());
        findViewById(R.id.txt_twi_target).setEnabled(master.isChecked() && twi.isChecked());
        findViewById(R.id.txt_rdt_target).setEnabled(master.isChecked() && rdt.isChecked());
        findViewById(R.id.txt_ig_target).setEnabled(master.isChecked() && ig.isChecked());
    }

    @Override
    protected void onDestroy() {
        startActivity(new Intent(PrivacyRedirectActivity.this, MainActivity.class));
        super.onDestroy();
    }
}