package dui.com.receipt.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"photo_id"})})
public class Block {
    public Block() { }
    @PrimaryKey(autoGenerate = true)
    public long blockId;

    @ColumnInfo(name = "photo_id")
    public long photoId;

    @ColumnInfo(name = "top")
    public int top;

    @ColumnInfo(name = "left")
    public int left;

    @ColumnInfo(name = "right")
    public int right;

    @ColumnInfo(name = "bottom")
    public int bottom;

    @ColumnInfo(name = "text")
    public String text;
}
