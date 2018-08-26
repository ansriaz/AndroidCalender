package co.alizay.calendar.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import co.alizay.calendar.models.Work;

@Entity(tableName = "work")
public class WorkEntity implements Work, Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long id;
    private long careGiverId;
    private long startTime;
    private long endTime;
    private String patientName;
    private int room;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getCareGiverId() {
        return careGiverId;
    }

    public void setCareGiverId(long careGiverId) {
        this.careGiverId = careGiverId;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "id: " + this.id + " CareGiverId: " + this.careGiverId + " StartTime: "
                + this.startTime + " EndTime: " + this.endTime + " Room: " + this.room;
    }

    @Ignore
    public WorkEntity() {}

    public WorkEntity (long id, long careGiverId, long startTime, long endTime, String patientName, int room) {
        this.id = id;
        this.careGiverId = careGiverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.patientName = patientName;
        this.room = room;
    }

    public WorkEntity (Work work) {
        this.id = work.getId();
        this.careGiverId = work.getCareGiverId();
        this.startTime = work.getStartTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.startTime);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        this.setEndTime(cal.getTimeInMillis());
        this.patientName = work.getPatientName();
        this.room = work.getRoom();
    }
}
