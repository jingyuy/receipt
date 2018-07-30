package dui.com.receipt;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dui.com.receipt.db.Block;
import dui.com.receipt.db.Photo;
import dui.com.receipt.db.Receipt;
import dui.com.receipt.db.ReceiptDatabase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ProcessReceiptsAsyncTask extends AsyncTask<Void, Receipt, Void> {
    private Application application;

    public ProcessReceiptsAsyncTask(Application application) {
        this.application = application;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<Receipt> receipts = ReceiptDatabase.getInstance(application).getReceipts().blockingGet();
        for (Receipt receipt: receipts) {
            List<Photo> photos = ReceiptDatabase.getInstance(application).getPhotosByReceipt(receipt).blockingGet();
            for (Photo photo: photos) {
                if (!photo.processed) {
                    processPhoto(photo);
                }
            }
            publishProgress(receipt);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Receipt... receipts) {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    private void processPhoto(final Photo photo) {
        // Get the dimensions of the View
        int targetW = application.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size_for_recognition);
        int targetH = application.getResources().getDimensionPixelSize(R.dimen.receipt_image_view_size_for_recognition);
        int scaleFactor = PhotoUtil.getScaleFactor(application, photo.pathName, targetW, targetH);
        Bitmap bitmap = PhotoUtil.getBitmap(photo.pathName, scaleFactor);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        Log.i("!!!!!!!!", "photo");
        Log.i("!!!!!!!!", String.format(Locale.getDefault(), "width: %d, height: %d", image.getBitmapForDebugging()
                        .getWidth(), image.getBitmapForDebugging().getHeight()));
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        List<FirebaseVisionText.Block> firebaseBlocks = firebaseVisionText.getBlocks();
                        photo.processed = true;
                        List<Block> blocks = new ArrayList<>();
                        for (FirebaseVisionText.Block currentBlock : firebaseBlocks) {
                            if (currentBlock.getBoundingBox() != null) {
                                Block block = new Block();
                                block.photoId = photo.photoId;
                                block.top = currentBlock.getBoundingBox().top;
                                block.bottom = currentBlock.getBoundingBox().bottom;
                                block.left = currentBlock.getBoundingBox().left;
                                block.right = currentBlock.getBoundingBox().right;
                                block.text = currentBlock.getText();
                                blocks.add(block);
                            }
                        }
                        ReceiptDatabase.getInstance(application).savePhotoBlocks(blocks).flatMap(
                                new Function<List<Long>, Single<Integer>>() {
                                    @Override
                                    public Single<Integer> apply(List<Long> longs) throws Exception {
                                        return ReceiptDatabase.getInstance(application).updatePhoto(photo);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer integer) throws Exception {
                                        Toast.makeText(application, R.string.finish_processing_photos,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                Toast.makeText(application, R.string.failed_to_process_photos, Toast.LENGTH_LONG).show();
                            }
                        });
    }
}
