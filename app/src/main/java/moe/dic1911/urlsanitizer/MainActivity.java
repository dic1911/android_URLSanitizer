package moe.dic1911.urlsanitizer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle link
        final BlacklistHandler blh = new BlacklistHandler(getApplicationContext());
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        Uri result;
        if (appLinkAction != null) {
            if (appLinkAction.equals(Intent.ACTION_VIEW) && appLinkData != null) {
                // handle query and stuff then rebuild the uri
                result = new UrlHandler(this, blh, appLinkData).sanitize();
                if (result == null) quit();

                // open link
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri target = result;
                intent.setData(target);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    Toast.makeText(getApplicationContext(), target.toString(), Toast.LENGTH_LONG).show();
                }

                startChooserActivity(Intent.ACTION_VIEW, result, result.toString());
                quit();
            } else if (appLinkAction.equals(Intent.ACTION_SEND)) {
                String txt = appLinkIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (txt != null) {
                    result = new UrlHandler(this, blh, txt).sanitize();
                    if (result == null) quit();

                    // share again
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, result.toString());
                    startChooserActivity(Intent.ACTION_SEND, result, "Share link via...");
                }
                quit();
            }
        }

        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        Log.d("fab?", "isNull? " + (fab == null));
        if (fab != null)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Jump to listing activity
                Intent openListActivity = new Intent(MainActivity.this, EntryListActivity.class);
                startActivity(openListActivity);
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

    private void quit() {
        finishAffinity();
        System.exit(0);
    }

    protected void startChooserActivity(String action, Uri result, String chooserTitle) {
        ArrayList<Intent> targetedShareIntents = buildTargetedShareIntents(action, result);

        Intent targetIntent = targetedShareIntents.remove(0);
        Intent chooserIntent = Intent.createChooser(targetIntent, chooserTitle);

        Parcelable[] targetedShareParceledIntents = new Parcelable[targetedShareIntents.size()];
        for (int i = 0; i < targetedShareIntents.size(); ++i)
            targetedShareParceledIntents[i] = targetedShareIntents.get(i);

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareParceledIntents);
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, new ComponentName[]{new ComponentName(this, MainActivity.class)});

        startActivity(chooserIntent);

    }

    protected ArrayList<Intent> buildTargetedShareIntents(String action, Uri result) {
        ArrayList<Intent> targetedShareIntents = new ArrayList<>();
        Intent rawIntent = createShareIntent(action, result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            targetedShareIntents.add(rawIntent);
            return targetedShareIntents;
        }

        List<ResolveInfo> resolvedActivities = getPackageManager().queryIntentActivities(rawIntent, 0);

        for (ResolveInfo info : resolvedActivities) {
            if (info.activityInfo.packageName.toLowerCase(Locale.ROOT).equals(getPackageName().toLowerCase(Locale.ROOT)))
                continue;

            Intent targetedShareIntent = createShareIntent(action, result);
            targetedShareIntent.setPackage(info.activityInfo.packageName);
            targetedShareIntents.add(targetedShareIntent);
        }

        if (targetedShareIntents.isEmpty()) {
            targetedShareIntents = new ArrayList<>();
            targetedShareIntents.add(rawIntent);
        }

        return targetedShareIntents;
    }

    protected Intent createShareIntent(String action, Uri result) {
        Intent finalIntent = new Intent();
        finalIntent.setAction(action);

        switch (action) {
            case Intent.ACTION_VIEW:
                finalIntent.setData(result);
                break;

            case Intent.ACTION_SEND:
                finalIntent.setType("text/plain");
                finalIntent.putExtra(Intent.EXTRA_TEXT, result.toString());
                break;

            default:
                break;
        }

        return finalIntent;
    }

}