package dui.com.receipt;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import dui.com.receipt.db.Photo;
import dui.com.receipt.db.Receipt;
import dui.com.receipt.db.ReceiptDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Receipt> dataset;

    public void restore(Receipt deletedItem, int deletedIndex) {
        dataset.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View foregroundView;
        public View backgroundView;
        public RecyclerView recyclerView;
        public ReceiptPhotoAdapter receiptPhotoAdapter;
        public ViewHolder(FrameLayout frameLayout) {
            super(frameLayout);
            recyclerView = frameLayout.findViewById(R.id.photo_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(frameLayout.getContext(), LinearLayoutManager
                    .HORIZONTAL, false));
            receiptPhotoAdapter = new ReceiptPhotoAdapter(frameLayout.getContext());
            recyclerView.setAdapter(receiptPhotoAdapter);
            foregroundView = frameLayout.findViewById(R.id.foreground);
            backgroundView = frameLayout.findViewById(R.id.background);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptAdapter(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    public void updateItems(List<Receipt> receipts) {
        dataset.clear();
        dataset.addAll(receipts);
        notifyDataSetChanged();
    }

    public Receipt getItem(int position) {
        return dataset.get(position);
    }

    public void remove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public List<Receipt> getItems() {
        return dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReceiptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Receipt receipt = dataset.get(position);
        ReceiptDatabase.getInstance(context)
                .getPhotosByReceipt(receipt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Photo>>() {
                    @Override
                    public void accept(List<Photo> photos) throws Exception {
                        holder.receiptPhotoAdapter.updateItems(photos);
                    }
                });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}