package moe.dic1911.fuckurlquery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "030";
    final private ArrayList<String> blacklist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add blacklisted shit here #temp
        // facebook
        blacklist.add("igshid");
        blacklist.add("fbclid");

        // twitter
        blacklist.add("s");

        // bilibili
        blacklist.add("spm_id_from");

        // Google analytics?
        blacklist.add("utm_source");
        blacklist.add("utm_medium");
        blacklist.add("utm_campaign");
        blacklist.add("utm_term");
        blacklist.add("utm_content");
        blacklist.add("utm_medium");


        // Handle link
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Log.d(TAG, appLinkAction);
        Uri appLinkData = appLinkIntent.getData();
        Uri.Builder builder = null;
        Uri result = null;
        if (appLinkAction.equals(Intent.ACTION_VIEW) && appLinkData != null) {
            Boolean cleaned = false;
            String scheme = appLinkData.getScheme(), host = appLinkData.getHost(),
                    path = appLinkData.getPath(), query = appLinkData.getQuery();
            Log.d(TAG, scheme);
            Log.d(TAG, host);
            Log.d(TAG, "path "+path);

            // fuck amazon
            if (path.split("=")[0].endsWith("ref")) {
                cleaned = true;
                path = path.split("ref")[0];
            }

            // generic
            if (cleaned || appLinkData.getQuery() != null) {
                cleaned = true;
                Log.d(TAG, "has query");
                builder = new Uri.Builder().scheme(scheme).authority(host).path(path);

                for (String q : appLinkData.getQueryParameterNames()) {
                    Log.d(TAG, q);
                    if (!blacklist.contains(q)) {
                        Log.d(TAG, "append");
                        builder.appendQueryParameter(q, appLinkData.getQueryParameter(q));
                    }
                }

                result = builder.build();
                Log.d(TAG, result.toString());
            }

            // open link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri target = cleaned ? result : appLinkData;
            intent.setData(target);
            startActivity(Intent.createChooser(intent, target.toString()));
            finishAffinity();
            System.exit(0);
        } else if (appLinkAction.equals(Intent.ACTION_SEND)) {
            String txt = appLinkIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (txt != null) {
                Uri temp = Uri.parse(txt);
                String scheme = temp.getScheme(), host = temp.getHost(),
                        path = temp.getPath(), query = temp.getQuery();
                builder = new Uri.Builder().scheme(scheme).authority(host).path(path);

                for (String q : temp.getQueryParameterNames()) {
                    if (!blacklist.contains(q)) {
                        builder.appendQueryParameter(q, temp.getQueryParameter(q));
                    }
                }
                // share again
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, builder.build().toString());
                startActivity(Intent.createChooser(intent, "Share link via..."));
            }
            finishAffinity();
            System.exit(0);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Why?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}