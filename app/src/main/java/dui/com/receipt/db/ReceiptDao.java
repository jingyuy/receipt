package dui.com.receipt.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface ReceiptDao {
    @Query("SELECT * FROM receipt")
    Single<List<Receipt>> getAll();

    @Query("SELECT * FROM receipt WHERE receiptId IN (:receiptIds)")
    List<Receipt> loadAllByIds(int[] receiptIds);

    @Query("SELECT * FROM receipt WHERE receipt_date=:receiptDate")
    List<Receipt> findByDate(Date receiptDate);

    @Insert
    Long insert(Receipt receipt);

    @Delete
    void delete(Receipt receipt);
}