package dui.com.receipt.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Receipt {
    @PrimaryKey(autoGenerate = true)
    public long receiptId;

    @ColumnInfo(name = "receipt_from")
    public String receiptFrom;

    @ColumnInfo(name = "receipt_to")
    public String receiptTo;

    @ColumnInfo(name = "receipt_amount")
    public Float receiptAmount;

    @ColumnInfo(name = "receipt_date")
    public Date receiptDate;

    @ColumnInfo(name = "create_at")
    public Date createAt;
}
