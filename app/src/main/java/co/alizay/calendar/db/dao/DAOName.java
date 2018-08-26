package co.alizay.calendar.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.NameEntity;

@Dao
public interface DAOName {

    @Query("SELECT * FROM name")
    List<NameEntity> getAll();

    @Query("SELECT * FROM name")
    List<NameEntity> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NameEntity> nameEntities);

    @Query("select * from name where id = :nameId")
    NameEntity loadName(int nameId);

    @Query("SELECT COUNT(*) FROM name")
    int count();

    @Delete
    void delete(NameEntity nameEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NameEntity nameEntity);
}
