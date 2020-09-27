package moe.dic1911.urlsanitizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "030";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle link
        final BlacklistHandler blh = new BlacklistHandler(getApplicationContext());
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Log.d(TAG, appLinkAction);
        Uri appLinkData = appLinkIntent.getData();
        Uri result = null;
        if (appLinkAction.equals(Intent.ACTION_VIEW) && appLinkData != null) {
            // handle query and stuff then rebuild the uri
            result = new UrlHandler(blh, appLinkData).sanitize();

            // open link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri target = result;
            intent.setData(target);
            startActivity(Intent.createChooser(intent, target.toString()));
            finishAffinity();
            System.exit(0);
        } else if (appLinkAction.equals(Intent.ACTION_SEND)) {
            String txt = appLinkIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (txt != null) {
                result = new UrlHandler(blh, txt).sanitize();
                // share again
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, result.toString());
                startActivity(Intent.createChooser(intent, "Share link via..."));
            }
            finishAffinity();
            System.exit(0);
        }

        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 1000;
        params.width = 1000;
        params.y = -10;

        this.getWindow().setAttributes(params);

        FloatingActionButton fab = findViewById(R.id.fab);
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
}