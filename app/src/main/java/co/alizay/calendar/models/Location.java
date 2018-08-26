package co.alizay.calendar.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @PrimaryKey
    @Expose(deserialize = false, serialize = false)
    transient private long id;

    @SerializedName("street")
    @ColumnInfo(name = "street")
    private String street;

    @SerializedName("city")
    @ColumnInfo(name = "city")
    private String city;

    @SerializedName("state")
    @ColumnInfo(name = "state")
    private String state;

    @SerializedName("postcode")
    @ColumnInfo(name = "postcode")
    private String postcode;

    @SerializedName("coordinates")
    @ColumnInfo(name = "coordinates_id")
    private Coordinates coordinates;

    @SerializedName("timezone")
    @ColumnInfo(name = "timezone_id")
    private TimeZone timezone;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public class Coordinates {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("latitude")
        @ColumnInfo(name = "latitude")
        private String latitude;

        @SerializedName("longitude")
        @ColumnInfo(name = "longitude")
        private String longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }

    public class TimeZone {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("street")
        @ColumnInfo(name = "street")
        private String street;

        @SerializedName("description")
        @ColumnInfo(name = "description")
        private String description;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
