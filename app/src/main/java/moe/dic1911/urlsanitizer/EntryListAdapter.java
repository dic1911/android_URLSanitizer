package moe.dic1911.urlsanitizer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryListHolder> {
    private Context con;
    private int count = 0;

    public class EntryListHolder extends RecyclerView.ViewHolder {
        public int id;
        public TextView entry;
        public EntryListHolder(View v) {
            super(v);
            id = (count++);
            entry = v.findViewById(R.id.txt_entry);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BlacklistHandler.getInstance().removeEntry(entry.getText().toString());
                    notifyDataSetChanged();
                    Log.d("030", entry.getText().toString());
                }
            });
        }
    }

    public EntryListAdapter(Context context) {
        con = context;
    }

    @NonNull
    @Override
    public EntryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_row, parent, false);
        return new EntryListHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryListHolder holder, final int position) {
        holder.entry.setText(BlacklistHandler.getInstance().getBlacklist().get(position));
    }

    @Override
    public int getItemCount() {
        return BlacklistHandler.getInstance().getBlacklist().size();
    }
}
