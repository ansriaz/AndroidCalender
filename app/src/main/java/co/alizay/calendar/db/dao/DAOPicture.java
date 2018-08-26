package co.alizay.calendar.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.NameEntity;
import co.alizay.calendar.db.entity.PictureEntity;

@Dao
public interface DAOPicture {

    @Query("SELECT * FROM picture")
    List<PictureEntity> getAll();

    @Query("SELECT * FROM picture")
    List<PictureEntity> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PictureEntity> pictureEntities);

    @Query("SELECT COUNT(*) FROM picture")
    int count();

    @Query("select * from picture where id = :picId")
    PictureEntity loadPicture(int picId);

    @Delete
    void delete(PictureEntity pictureEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PictureEntity pictureEntity);
}

