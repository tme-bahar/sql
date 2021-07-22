package ir.fanniherfei.sql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> ID;
    private List<String> TITLE;
    private List<String> SUBTITLE;
    private List<String> CHECKED;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> ID, List<String> TITLE, List<String> SUBTITLE, List<String> CHECKED) {
        this.mInflater = LayoutInflater.from(context);
        this.ID = ID;
        this.TITLE = TITLE;
        this.SUBTITLE = SUBTITLE;
        this.CHECKED = CHECKED;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ID = this.ID.get(position);
        String TITLE = this.TITLE.get(position);
        String SUBTITLE = this.SUBTITLE.get(position);
        String CHECKED = this.CHECKED.get(position);
        holder.myTextView.setText(ID);
        holder.myTextView2.setText(TITLE);
        holder.myTextView3.setText(SUBTITLE);
        holder.myTextView4.setText(CHECKED);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return ID.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;
        TextView myTextView3;
        TextView myTextView4;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textView);
            myTextView2 = itemView.findViewById(R.id.textView2);
            myTextView3 = itemView.findViewById(R.id.textView3);
            myTextView4 = itemView.findViewById(R.id.textView6);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return ID.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}