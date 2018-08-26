package co.alizay.calendar.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import co.alizay.calendar.models.Patient;

@Entity(tableName = "patient")
public class PatientEntity implements Patient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    long id;
    String patientName;
    int roomNumber;

    @Ignore
    public PatientEntity() {
    }

    public PatientEntity(Patient patient) {
        this.id = patient.getId();
        this.patientName = patient.getPatientName();
        this.roomNumber = patient.getRoomNumber();
    }

    public PatientEntity(long id, String patientName, int roomNumber) {
        this.id = id;
        this.patientName = patientName;
        this.roomNumber = roomNumber;
    }

    @Override
    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
