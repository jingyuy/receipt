package dui.com.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotoUtil {

    private PhotoUtil() { }

    public static int getScaleFactor(Context context, String pathName) {
        // Get the dimensions of the View
        int targetW = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size_for_recognition);
        int targetH = context.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size_for_recognition);

        // Get the dimensions of the thumbnail
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        return Math.min(photoW / targetW, photoH / targetH);
    }

    public static Bitmap getBitmap(String pathName, int scaleFactor) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(pathName, bmOptions);
    }
}
