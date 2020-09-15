package moe.dic1911.fuckmoptt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "你按這個幹嘛030?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Handle link
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {
            Toast.makeText(this, appLinkData.toString(), Toast.LENGTH_SHORT).show();
            TextView desc_txt = findViewById(R.id.txt_desc);
            String orig_url = appLinkData.toString();
            String tmp = orig_url.split("/")[4];
            String[] tmp2 = tmp.split("\\.");
            StringBuilder result = new StringBuilder("https://www.ptt.cc/bbs/").append(tmp2[0]).append("/");
            result.append(tmp.replace(tmp2[0] + ".", "")).append(".html");
            Log.d("030", result.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(result.toString()));
            startActivity(intent);
            finishAffinity();
            System.exit(0);
        }
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