package co.alizay.calendar.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import co.alizay.calendar.models.CareGiver;
import co.alizay.calendar.models.Picture;

@Entity(tableName = "picture")
public class PictureEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long id;
    private String large;
    private String medium;
    private String thumbnail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public PictureEntity(long id, String large, String medium, String thumbnail) {
        this.id = id;
        this.large = large;
        this.medium = medium;
        this.thumbnail = thumbnail;
    }

    public PictureEntity() {
    }

    public PictureEntity(Picture picture) {
        this.id = picture.getId();
        this.large = picture.getLarge();
        this.medium = picture.getMedium();
        this.thumbnail = picture.getThumbnail();
    }
}
