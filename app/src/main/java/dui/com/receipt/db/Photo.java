package dui.com.receipt.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

@Entity(indices = {@Index(value = {"receipt_id"})})
public class Photo {
    public Photo(String pathName, Date createAt) {
        this.pathName = pathName;
        this.createAt = createAt;
    }
    @PrimaryKey(autoGenerate = true)
    public long photoId;

    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    @ColumnInfo(name = "in_receipt_order")
    public int inReceiptOrder;

    @ColumnInfo(name = "path_name")
    public String pathName;

    @ColumnInfo(name = "create_at")
    public Date createAt;

    @ColumnInfo(name = "processed")
    public boolean processed;

    @Ignore
    public List<Block> blocks;

    @Ignore
    public Bitmap thumbnail;

    @Ignore
    public Bitmap medium;
}
