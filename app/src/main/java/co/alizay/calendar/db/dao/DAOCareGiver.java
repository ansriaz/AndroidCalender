package co.alizay.calendar.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.CareGiverEntity;

@Dao
public interface DAOCareGiver {

//    @Query("SELECT * FROM caregiver WHERE id IN (:caregivers)")
//    LiveData<List<CareGiverEntity>> loadAllByIds(int[] caregivers);

//    @Query("SELECT * FROM caregiver WHERE first_name LIKE :first AND "
//            + "last_name LIKE :last LIMIT 1")
//    CareGiverEnt findByName(String first, String last);

    @Delete
    void delete(CareGiverEntity careGiver);

    @Query("SELECT COUNT(*) FROM caregiver")
    int count();

    @Query("SELECT * FROM caregiver")
    List<CareGiverEntity> getAll();

    @Query("SELECT * FROM caregiver")
    List<CareGiverEntity> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CareGiverEntity> careGivers);

    @Query("select * from caregiver where id = :cgId")
    CareGiverEntity loadCareGiver(long cgId);

//    @Query("select caregiver.* from caregiver, name where id = :cgId")
//    CareGiverEntity loadCareGiverByNameSurname(String name, String surname);

    @Query("select caregiver.* from caregiver where email = :email")
    CareGiverEntity loadCareGiverByEmail(String email);

    @Query("select * from caregiver where id = :cgId")
    CareGiverEntity loadCareGiverSync(long cgId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CareGiverEntity careGiver);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CareGiverEntity... careGivers);

}
