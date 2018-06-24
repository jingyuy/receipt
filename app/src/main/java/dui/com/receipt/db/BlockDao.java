package dui.com.receipt.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface BlockDao {
    @Query("SELECT * FROM block")
    List<Block> getAll();

    @Query("SELECT * FROM block WHERE blockId IN (:blockIds)")
    List<Block> loadAllByIds(int[] blockIds);

    @Query("SELECT * FROM block WHERE photo_id=:photoId")
    Single<List<Block>> findByPhoto(long photoId);

    @Insert
    List<Long> insertAll(Block... blocks);

    @Insert
    Long insert(Block block);

    @Delete
    void delete(Block block);

    @Delete
    void delete(Block... blocks);
}