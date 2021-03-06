package dui.com.receipt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dui.com.receipt.db.Photo;

public class ReceiptPhotoAdapter extends RecyclerView.Adapter<ReceiptPhotoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Photo> dataset;
    private View.OnClickListener onClickListener;

    public void restore(Photo deletedItem, int deletedIndex) {
        dataset.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.receipt_image_imageview);
            itemView.setOnClickListener(onClickListener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptPhotoAdapter(Context context, View.OnClickListener onClickListener) {
        this.context = context;
        this.dataset = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    public void updateItems(List<Photo> photos) {
        dataset.clear();
        dataset.addAll(photos);
        notifyDataSetChanged();
    }

    public Photo getItem(int position) {
        return dataset.get(position);
    }

    public void remove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public List<Photo> getItems() {
        return dataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReceiptPhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_photo_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Photo photo = dataset.get(position);
        if (photo.thumbnail == null) {
            int targetW = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size);
            int targetH = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size);
            int scaleFactor = PhotoUtil.getScaleFactor(context, photo.pathName, targetW, targetH);
            photo.thumbnail = PhotoUtil.getBitmap(photo.pathName, scaleFactor);
        }
        holder.imageView.setImageBitmap(photo.thumbnail);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}