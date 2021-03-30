package moe.dic1911.urlsanitizer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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

    }
}