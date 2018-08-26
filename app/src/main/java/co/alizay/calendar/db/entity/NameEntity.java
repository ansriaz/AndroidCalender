package co.alizay.calendar.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import co.alizay.calendar.models.Name;

@Entity(tableName = "name")
public class NameEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long id;
    private String title;
    private String first;
    private String last;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public NameEntity() {}

    public NameEntity(Name name) {
        this.id = name.getId();
        this.first = name.getFirst();
        this.last = name.getLast();
        this.title = name.getTitle();
    }

    public NameEntity(long id, String title, String first, String last) {
        this.id = id;
        this.title = title;
        this.first = first;
        this.last = last;
    }
}
