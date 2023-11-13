package moe.dic1911.urlsanitizer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import static android.view.KeyEvent.KEYCODE_ENTER;

import java.util.Arrays;

public class MainFragment extends Fragment {

    private View fragmentView;
    private EditText txt_new_entry = null;
    private ClipboardManager clipboardManager = null;

    private void addEntry(View view) {
        if (txt_new_entry == null)
            txt_new_entry = fragmentView.findViewById(R.id.txt_new_entry);

        // handle add entry
        if (BlacklistHandler.getInstance().addEntry(txt_new_entry.getText().toString()))
            Snackbar.make(view, getString(R.string.noti_added), Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(view, getString(R.string.noti_saved), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        txt_new_entry = fragmentView.findViewById(R.id.txt_new_entry);
        Button butt_add = fragmentView.findViewById(R.id.btn_add);
        if (butt_add != null) {
            butt_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEntry(view);
                }
            });
        }

        Button butt_clipboard = fragmentView.findViewById(R.id.btn_clipboard);
        if (butt_clipboard != null) {
            final MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                clipboardManager = (ClipboardManager)
                        activity.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                butt_clipboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipData clip = clipboardManager.getPrimaryClip();
                        Context context = activity.getApplicationContext();
                        if (clip.getItemCount() == 0) {
                            Toast.makeText(context, R.string.clipboard_err, Toast.LENGTH_LONG).show();
                            return;
                        }
                        CharSequence _content = clip.getItemAt(0).getText();
                        String content = _content.toString();
                        try {
                            Uri sanitized = Uri.parse(new UrlHandler(activity.getApplicationContext(),
                                    activity.blacklistHandler, content).sanitize());
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("", sanitized.toString()));
                            Toast.makeText(context, R.string.clipboard_sanitized, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("urlsan",
                                    String.format("exception occurred while handling clipboard content: %s - %s",
                                            e.getMessage(), Arrays.toString(e.getStackTrace())));
                        }
                    }
                });
            }

        }

        txt_new_entry.setOnKeyListener(new View.OnKeyListener() {
            private int c = 0;

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    Log.d("030", "ENTER");
                    if ((++c) % 2 == 1) // temp. workaround. this gets triggered twice in one tap somehow
                        addEntry(view);
                    //return true;
                }
                return false;
            }
        });
        // Inflate the layout for this fragment
        return fragmentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}