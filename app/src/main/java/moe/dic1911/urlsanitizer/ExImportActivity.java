package moe.dic1911.urlsanitizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ExImportActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private final String TAG_SAF = "030-SAF";
    private final int EXPORT = 69;
    private final int IMPORT = 96;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TITLE, "urlsanitizer_rules.txt");
        startActivityForResult(intent, EXPORT);
    }

    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TITLE, "urlsanitizer_rules.txt");
        startActivityForResult(intent, IMPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            Log.d(TAG_SAF, path.getPath());
            switch (requestCode) {
                case EXPORT: {
                    try {
                        OutputStream os = getContentResolver().openOutputStream(path);
                        String list = new BlacklistHandler(this).buildPrefs();
                        os.write(list.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case IMPORT: {
                    try {
                        InputStream is = getContentResolver().openInputStream(path);
                        byte[] buf = new byte[1024];
                        StringBuilder sb = new StringBuilder();
                        int len;
                        while ((len = is.read(buf)) > 0) {
                            sb.append(new String(buf));
                        }
                        is.close();
                        String res = sb.toString();
                        res = res.substring(0, res.indexOf("\0"));
                        Log.d(TAG_SAF + "-R", res);
                        prefs.edit().putString("blacklist", res).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile();
            }
        });
        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        });

        prefs = getApplicationContext().getSharedPreferences("main", Context.MODE_PRIVATE);
    }

    @Override
    protected void onDestroy() {
        Intent openImExportActivity = new Intent(ExImportActivity.this, MainActivity.class);
        startActivity(openImExportActivity);
        super.onDestroy();
    }
}