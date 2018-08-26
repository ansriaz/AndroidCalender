package co.alizay.calendar.models;

public interface Overtime {

    long getId();
    long getCareGiverId();
    long getStartTime();
    long getEndTime();
    String getPatientName();
    int getRoom();
}
