package co.alizay.calendar.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.PatientEntity;

@Dao
public interface DAOPatient {

    @Query("SELECT * FROM patient WHERE id IN (:patients)")
    LiveData<List<PatientEntity>> loadAllByIds(int[] patients);

    @Query("SELECT * FROM patient")
    LiveData<List<PatientEntity>> getAll();

    @Query("SELECT * FROM patient")
    LiveData<List<PatientEntity>> loadAll();

    @Query("SELECT COUNT(*) FROM patient")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PatientEntity> patientEntities);

    @Query("select * from patient where id = :patientId")
    LiveData<PatientEntity> loadPatient(int patientId);

    @Query("select * from patient where id = :patientId")
    PatientEntity loadPatientSync(int patientId);

    @Delete
    void delete(PatientEntity patientEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PatientEntity patientEntity);

}
