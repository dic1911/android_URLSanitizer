package moe.dic1911.urlsanitizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class MainFragment extends Fragment {

    private View fragmentView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText txt_new_entry = fragmentView.findViewById(R.id.txt_new_entry);
        Button butt_add = fragmentView.findViewById(R.id.btn_add);
        butt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle add entry
                if (BlacklistHandler.getInstance().addEntry(txt_new_entry.getText().toString()))
                    Snackbar.make(view, getString(R.string.noti_added), Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(view, getString(R.string.noti_dup), Snackbar.LENGTH_LONG).show();
            }
        });
        // Inflate the layout for this fragment
        return fragmentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}