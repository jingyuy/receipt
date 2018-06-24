package dui.com.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dui.com.receipt.db.Photo;

public class ReceiptPhotoAdapter extends RecyclerView.Adapter<ReceiptPhotoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Photo> dataset;

    public void restore(Photo deletedItem, int deletedIndex) {
        dataset.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public ViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptPhotoAdapter(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
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
    @Override
    public ReceiptPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_photo_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Photo photo = dataset.get(position);
        if (photo.thumbnail == null) {
            photo.thumbnail = getBitmap(photo.pathName);
        }
        holder.imageView.setImageBitmap(photo.thumbnail);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private Bitmap getBitmap(String pathName) {
        // Get the dimensions of the View
        int targetW = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size);
        int targetH = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size);

        // Get the dimensions of the thumbnail
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(pathName, bmOptions);
    }

}