package co.alizay.calendar.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.PatientEntity;
import co.alizay.calendar.db.entity.WorkEntity;

@Dao
public interface DAOWork {

    @Query("SELECT * FROM work WHERE id IN (:works)")
    LiveData<List<WorkEntity>> loadAllByIds(int[] works);

    @Query("SELECT * FROM work, caregiver WHERE work.id = :workId AND "
            + "caregiver.id = work.careGiverId LIMIT 1")
    CareGiverEntity loadCaregiverByWork(long workId);

    @Query("SELECT work.* FROM work, caregiver WHERE work.careGiverId = :caregiverId " +
            "AND caregiver.id = work.careGiverId AND work.startTime BETWEEN :dateStart AND :dateEnd")
    List<WorkEntity> loadWorkOfCareGiver(long caregiverId, long dateStart, long dateEnd);

    @Query("SELECT work.* FROM work, caregiver WHERE work.careGiverId = :caregiverId " +
            "AND caregiver.id = work.careGiverId AND work.startTime = :dateStart LIMIT 1")
    WorkEntity CareGiverWorkAtTime(long caregiverId, long dateStart);

    @Query("SELECT COUNT(*) FROM work, overtime where (work.room = :room AND work.startTime = :startTime)" +
            " OR (overtime.room = :room AND overtime.startTime = :startTime)")
    int isRoomFree(int room, long startTime);

    @Query("SELECT work.* FROM work, caregiver where work.startTime = :time AND caregiver.email = :cgEmail AND work.careGiverId = caregiver.id")
    WorkEntity getWorkCareGiver(String cgEmail, long time);

    @Query("SELECT work.* FROM work, caregiver where work.startTime = :time AND caregiver.email = :cgEmail AND work.careGiverId = caregiver.id")
    WorkEntity CareGiverWorkAtTime(String cgEmail, long time);

    @Query("SELECT COUNT(*) FROM work")
    int count();

    @Query("SELECT * FROM work")
    List<WorkEntity> getAll();

    @Query("SELECT * FROM work")
    List<WorkEntity> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkEntity> workEntities);

    @Query("select * from work where id = :workId")
    WorkEntity loadWork(int workId);

    @Query("select * from work where id = :workId")
    WorkEntity loadWorkSync(int workId);

    @Delete
    void delete(WorkEntity workEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WorkEntity workEntity);
}
