package dui.com.receipt.db;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReceiptDatabase {
    private static final String DATABASE_NAME = "receipt-appDatabase";
    private static ReceiptDatabase receiptDatabase;
    private AppDatabase appDatabase;

    private ReceiptDatabase(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public static ReceiptDatabase getInstance(Context context) {
        if (receiptDatabase == null) {
            receiptDatabase = new ReceiptDatabase(Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .build());
        }
        return receiptDatabase;
    }

    public Single<List<Receipt>> getReceipts() {
        return appDatabase.receiptDao().getAll();
    }

    public Single<List<Photo>> getPhotosByReceipt(Receipt receipt) {
        return appDatabase.photoDao().findByReceipt(receipt.receiptId);
    }

    public Single<List<Photo>> getPhotosByReceiptId(long receiptId) {
        return appDatabase.photoDao().findByReceipt(receiptId);
    }

    public void deleteReceipt(final Receipt receipt) {
        Observable.just(receipt)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Receipt>() {
                    @Override
                    public void accept(final Receipt receipt) throws Exception {
                        appDatabase.photoDao().findByReceipt(receipt.receiptId)
                                .observeOn(Schedulers.io())
                                .subscribe(new Consumer<List<Photo>>() {
                                    @Override
                                    public void accept(List<Photo> photos) throws Exception {
                                        appDatabase.receiptDao().delete(receipt);
                                        Photo[] photoArray = new Photo[photos.size()];
                                        appDatabase.photoDao().delete(photos.toArray(photoArray));
                                    }
                                });
                    }
                });
    }

    public Single<Integer> updatePhoto(Photo photo) {
        return Single.just(photo)
                .subscribeOn(Schedulers.io())
                .map(new Function<Photo, Integer>() {
                    @Override
                    public Integer apply(Photo photo) throws Exception {
                        return appDatabase.photoDao().update(photo);
                    }
                });
    }

    public Single<List<Long>> savePhotoBlocks(List<Block> blocks) {
        return Single.just(blocks)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<Block>, List<Long>>() {
                    @Override
                    public List<Long> apply(List<Block> blocks) throws Exception {
                        Log.i("!!!!!!!!", "blocks");
                        for (Block block : blocks) {
                            Log.i("!!!!!!!!!", "text: " + block.text);
                            Log.i("!!!!!!!!!", "left: " + block.left);
                            Log.i("!!!!!!!!!", "right: " + block.right);
                            Log.i("!!!!!!!!!", "top: " + block.top);
                            Log.i("!!!!!!!!!", "bottom: " + block.bottom);
                            Log.i("!!!!!!!!!!!!", "---------------");
                        }
                        return appDatabase.blockDao().insertAll(blocks.toArray(new Block[blocks.size()]));
                    }
                });
    }

    public Single<List<Long>> saveReceiptPhotos(List<Photo> photos) {
        return Single.just(photos)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<Photo>, List<Long>>() {

                    @Override
                    public List<Long> apply(List<Photo> photos) throws Exception {
                        long receiptId = appDatabase.receiptDao().insert(new Receipt());
                        int order = 0;
                        for (Photo photo : photos) {
                            photo.receiptId = receiptId;
                            photo.inReceiptOrder = order;
                            order++;
                        }
                        return appDatabase.photoDao().insertAll(photos.toArray(new Photo[photos.size()]));
                    }
                });
    }
}
