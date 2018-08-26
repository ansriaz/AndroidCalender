package co.alizay.calendar.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class CareGiver {

    @PrimaryKey
    @Expose(deserialize = false, serialize = false)
    transient private long id;

    @Expose
    @SerializedName("gender")
    @ColumnInfo(name = "gender")
    private String gender;

    @SerializedName("name")
    @ColumnInfo(name = "name_id")
    private Name name;

    @SerializedName("location")
    @ColumnInfo(name = "location_id")
    private Location location;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String email;

    @SerializedName("login")
    @ColumnInfo(name = "login_id")
    private Login login;

    @SerializedName("dob")
    @ColumnInfo(name = "dob_id")
    private DOB dob;

    @SerializedName("registered")
    @ColumnInfo(name = "registered_id")
    private Registered registered;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    private String phone;

    @SerializedName("cell")
    @ColumnInfo(name = "cell")
    private String cell;

    @SerializedName("id")
    @ColumnInfo(name = "id_id")
    private ID idId;

    @SerializedName("picture")
    @ColumnInfo(name = "picture_id")
    private Picture picture;

    @SerializedName("nat")
    @ColumnInfo(name = "nat")
    private String nat;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public DOB getDob() {
        return dob;
    }

    public void setDob(DOB dob) {
        this.dob = dob;
    }

    public Registered getRegistered() {
        return registered;
    }

    public void setRegistered(Registered registered) {
        this.registered = registered;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public ID getID() {
        return idId;
    }

    public void setID(ID id) {
        this.idId = id;
    }

    public co.alizay.calendar.models.Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    @Override
    public String toString() {
        return this.getName().toString() + " email: " + this.email;
    }

    @Entity
    public class ID {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("name")
        @ColumnInfo(name = "name")
        private String name;

        @SerializedName("value")
        @ColumnInfo(name = "value")
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Entity
    public class Registered {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("date")
        @ColumnInfo(name = "date")
        private String date;

        @SerializedName("age")
        @ColumnInfo(name = "age")
        private String age;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    @Entity
    public class DOB {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("date")
        @ColumnInfo(name = "date")
        private String date;

        @SerializedName("age")
        @ColumnInfo(name = "age")
        private String age;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    @Entity
    public class Login {

        @PrimaryKey
        @Expose(deserialize = false, serialize = false)
        transient private long id;

        @SerializedName("uuid")
        @ColumnInfo(name = "uuid")
        private String uuid;

        @SerializedName("username")
        @ColumnInfo(name = "username")
        private String username;

        @SerializedName("password")
        @ColumnInfo(name = "password")
        private String password;

        @SerializedName("salt")
        @ColumnInfo(name = "salt")
        private String salt;

        @SerializedName("md5")
        @ColumnInfo(name = "md5")
        private String md5;

        @SerializedName("sha1")
        @ColumnInfo(name = "sha1")
        private String sha1;

        @SerializedName("sha256")
        @ColumnInfo(name = "sha256")
        private String sha256;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public String getSha256() {
            return sha256;
        }

        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
    }
}
