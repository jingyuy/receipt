package dui.com.receipt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.ArrayList;
import java.util.List;

import dui.com.receipt.db.Photo;
import dui.com.receipt.db.Receipt;
import dui.com.receipt.db.ReceiptDatabase;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Receipt> receiptList;
    private BehaviorRelay<Long> clicks = BehaviorRelay.create();

    public void restore(Receipt deletedItem, int deletedIndex) {
        receiptList.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View foregroundView;
        public View backgroundView;
        public RecyclerView recyclerView;
        public ReceiptPhotoAdapter receiptPhotoAdapter;
        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.photo_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager
                    .HORIZONTAL, false));
            receiptPhotoAdapter = new ReceiptPhotoAdapter(itemView.getContext());
            recyclerView.setAdapter(receiptPhotoAdapter);
            foregroundView = itemView.findViewById(R.id.foreground);
            backgroundView = itemView.findViewById(R.id.background);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicks.accept(receiptList.get(getAdapterPosition()).receiptId);
                }
            });
        }
    }

    public Observable<Long> clicks() {
        return this.clicks.hide();
    }

    // Provide a suitable constructor (depends on the kind of receiptList)
    public ReceiptAdapter(Context context) {
        this.context = context;
        this.receiptList = new ArrayList<>();
    }

    public void updateItems(List<Receipt> receipts) {
        receiptList.clear();
        receiptList.addAll(receipts);
        notifyDataSetChanged();
    }

    public Receipt getItem(int position) {
        return receiptList.get(position);
    }

    public void remove(int position) {
        receiptList.remove(position);
        notifyItemRemoved(position);
    }

    public List<Receipt> getItems() {
        return receiptList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // - get element from your receiptList at this position
        // - replace the contents of the view with that element
        Receipt receipt = receiptList.get(position);
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

    // Return the size of your receiptList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return receiptList.size();
    }
}