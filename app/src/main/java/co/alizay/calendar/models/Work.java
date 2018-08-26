package co.alizay.calendar.models;

public interface Work {

    long getId();
    long getCareGiverId();
    long getStartTime();
    long getEndTime();
    String getPatientName();
    int getRoom();
}
