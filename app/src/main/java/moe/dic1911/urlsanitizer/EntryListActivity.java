package moe.dic1911.urlsanitizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EntryListActivity extends AppCompatActivity {
    TextView textView;
    RecyclerView recyclerView;
    EntryListAdapter entryList;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        recyclerView = findViewById(R.id.entry_list);
        entryList = new EntryListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(entryList);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mContext = this;

        textView = findViewById(R.id.txt_entry_list);
        textView.setOnClickListener(new View.OnClickListener() {
            Boolean confirmed = false;

            @Override
            public void onClick(View v) {
                if (!confirmed) {
                    textView.setText(R.string.txt_entry_list_pre_reset);
                    confirmed = true;
                } else {
                    BlacklistHandler.getInstance().resetAll();
                    textView.setText(R.string.txt_entry_list_reset_done);
                }
            }
        });
        findViewById(R.id.btn_eximport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openImExportActivity = new Intent(EntryListActivity.this, ExImportActivity.class);
                startActivity(openImExportActivity);
                finishAffinity();
            }
        });

        findViewById(R.id.btn_priv_redir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryListActivity.this, PrivacyRedirectActivity.class));
                finishAffinity();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        final LinearLayout body = findViewById(R.id.body);
        body.post(new Runnable() {
            @Override
            public void run() {
                if(body.getWidth() < 800)
                    ((LinearLayout)findViewById(R.id.btn_container)).setOrientation(LinearLayout.VERTICAL);
            }
        });
    }
}