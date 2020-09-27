package moe.dic1911.urlsanitizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryListHolder> {
    private int count = 0;

    public class EntryListHolder extends RecyclerView.ViewHolder {
        final public int id;
        final public TextView entry;
        public EntryListHolder(View v) {
            super(v);
            id = (count++);
            entry = v.findViewById(R.id.txt_entry);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BlacklistHandler.getInstance().removeEntry(entry.getText().toString()))
                        notifyDataSetChanged();
                }
            });
        }
    }

    public EntryListAdapter() {}

    @NonNull
    @Override
    public EntryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_row, parent, false);
        return new EntryListHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryListHolder holder, final int position) {
        holder.entry.setText(BlacklistHandler.getInstance().getEntry(position));
    }

    @Override
    public int getItemCount() {
        return BlacklistHandler.getInstance().getBlacklistSize();
    }
}
