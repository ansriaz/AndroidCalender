package co.alizay.calendar.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.OvertimeEntity;
import co.alizay.calendar.db.entity.PatientEntity;

@Dao
public interface DAOOvertime {

    @Query("SELECT * FROM overtime WHERE id IN (:overtimes)")
    List<OvertimeEntity> loadAllByIds(int[] overtimes);

    @Query("SELECT * FROM overtime, caregiver WHERE :overtimeId = overtime.id AND "
            + "caregiver.id = overtime.careGiverId LIMIT 1")
    CareGiverEntity loadCaregiverByOvertime(int overtimeId);

//    @Query("SELECT * FROM overtime, patient WHERE :overtimeId = overtime.id AND "
//            + "patient.id = overtime.patientName LIMIT 1")
//    PatientEntity loadPatientByOvertime(int overtimeId);

    @Query("select overtime.* from overtime, caregiver where caregiver.id = overtime.careGiverId " +
            "AND caregiver.id = :caregiverId AND overtime.startTime BETWEEN :dateStart AND :dateEnd")
    List<OvertimeEntity> loadOvertimesByCaregiverId(long caregiverId, long dateStart, long dateEnd);

    @Query("SELECT overtime.* FROM overtime, caregiver where overtime.startTime = :time " +
            "AND caregiver.email = :cgEmail AND overtime.careGiverId = caregiver.id")
    OvertimeEntity getOvertimeCareGiver(String cgEmail, long time);

    @Query("SELECT COUNT(*) FROM overtime")
    int count();

    @Query("SELECT * FROM overtime")
    List<OvertimeEntity> getAll();

    @Query("SELECT * FROM overtime")
    List<OvertimeEntity> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OvertimeEntity> overtimeEntities);

    @Query("select * from overtime where id = :overtimeId")
    OvertimeEntity loadOvertime(int overtimeId);

    @Query("select * from overtime where id = :overtimeId")
    OvertimeEntity loadOvertimeSync(int overtimeId);

    @Delete
    void delete(OvertimeEntity overtimeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(OvertimeEntity overtimeEntity);
}
