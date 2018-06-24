package dui.com.receipt.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Photo.class, Receipt.class, Block.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BlockDao blockDao();
    public abstract PhotoDao photoDao();
    public abstract ReceiptDao receiptDao();
}