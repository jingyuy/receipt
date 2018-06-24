package dui.com.receipt.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

import javax.annotation.Nullable;

public class Converters {
    @TypeConverter
    @Nullable
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    @Nullable
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}