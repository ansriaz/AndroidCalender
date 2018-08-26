package co.alizay.calendar.db;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import java.util.List;

import co.alizay.calendar.db.converter.NameConverter;
import co.alizay.calendar.db.converter.PictureConverter;
import co.alizay.calendar.db.dao.DAOCareGiver;
import co.alizay.calendar.db.dao.DAOName;
import co.alizay.calendar.db.dao.DAOOvertime;
import co.alizay.calendar.db.dao.DAOPatient;
import co.alizay.calendar.db.dao.DAOPicture;
import co.alizay.calendar.db.dao.DAOWork;
import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.NameEntity;
import co.alizay.calendar.db.entity.OvertimeEntity;
import co.alizay.calendar.db.entity.PatientEntity;
import co.alizay.calendar.db.entity.PictureEntity;
import co.alizay.calendar.db.entity.WorkEntity;
import co.alizay.calendar.helpers.AppExecutors;

@Database(entities = {OvertimeEntity.class, WorkEntity.class, CareGiverEntity.class,
        PatientEntity.class, NameEntity.class, PictureEntity.class},
        version = 1, exportSchema = false)
@TypeConverters({NameConverter.class, PictureConverter.class})

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "caregiver-db";

    public abstract DAOCareGiver daoCareGiver();

    public abstract DAOOvertime daoOvertime();

    public abstract DAOWork daoWork();

    public abstract DAOName daoName();

    public abstract DAOPicture daoPicture();

    public abstract DAOPatient daoPatient();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        sInstance =  Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .build();
        return sInstance;
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    private static void insertData(final AppDatabase database, final List<OvertimeEntity> overtimeEntities,
                                   final List<WorkEntity> workEntities, final List<CareGiverEntity> careGiverEntities) {
        database.runInTransaction(() -> {
            database.daoOvertime().insertAll(overtimeEntities);
            database.daoCareGiver().insertAll(careGiverEntities);
            database.daoWork().insertAll(workEntities);
        });
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }

}
