package dui.com.receipt.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM photo")
    List<Photo> getAll();

    @Query("SELECT * FROM photo WHERE photoId IN (:photoIds)")
    List<Photo> loadAllByIds(int[] photoIds);

    @Query("SELECT * FROM photo WHERE receipt_id=:receiptId ORDER BY in_receipt_order")
    Single<List<Photo>> findByReceipt(long receiptId);

    @Insert
    List<Long> insertAll(Photo... photos);

    @Insert
    Long insert(Photo photo);

    @Update
    int update(Photo photo);

    @Delete
    void delete(Photo photo);

    @Delete
    void delete(Photo ... photos);
}