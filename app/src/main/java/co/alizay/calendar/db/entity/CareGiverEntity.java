package co.alizay.calendar.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import co.alizay.calendar.models.CareGiver;
import co.alizay.calendar.models.Location;
import co.alizay.calendar.models.Name;
import co.alizay.calendar.models.Picture;

@Entity(tableName = "caregiver")
public class CareGiverEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long id;

    private String gender;
    private NameEntity name;
//    private Location location;
    private String email;
//    private CareGiver.Login login;
//    private CareGiver.DOB dob;
//    private CareGiver.Registered registered;
    private String phone;
    private String cell;
//    private CareGiver.ID idId;
//    private PictureEntity picture;
    private PictureEntity picture;
    private String nat;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public NameEntity getName() {
//        Name nameModel = new Name();
//        name.setFirst(this.name.getFirst());
//        name.setLast(this.name.getLast());
//        name.setTitle(this.name.getTitle());
        return name;
    }

    public void setName(NameEntity name) {
        this.name = name;
    }

//    public Location getLocation() {
//        return location;
//    }
//
//    public void setLocation(Location location) {
//        this.location = location;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public CareGiver.Login getLogin() {
//        return login;
//    }
//
//    public void setLogin(CareGiver.Login login) {
//        this.login = login;
//    }
//
//    public CareGiver.DOB getDob() {
//        return dob;
//    }
//
//    public void setDob(CareGiver.DOB dob) {
//        this.dob = dob;
//    }
//
//    public CareGiver.Registered getRegistered() {
//        return registered;
//    }
//
//    public void setRegistered(CareGiver.Registered registered) {
//        this.registered = registered;
//    }

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

//    public CareGiver.ID getIdId() {
//        return idId;
//    }
//
//    public void setIdId(CareGiver.ID idId) {
//        this.idId = idId;
//    }

    public PictureEntity getPicture() {
        return picture;
    }

    public void setPicture(PictureEntity picture) {
        this.picture = picture;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    public CareGiverEntity () {}

    public CareGiverEntity (int id, String gender,
                            Name name,
//                            Location location,
                            String email,
//                            CareGiver.Login login,
//                            CareGiver.DOB dob,
//                            CareGiver.Registered registered,
                            String phone,
                            String cell,
//                            CareGiver.ID idId,
                            Picture picture,
                            String nat) {
        this.id = id;
        this.gender = gender;

        NameEntity nameModel = new NameEntity();
        nameModel.setFirst(name.getFirst());
        nameModel.setLast(name.getLast());
        nameModel.setTitle(name.getTitle());

        this.name = nameModel;
//        this.location = location;
        this.email = email;
//        this.login = login;
//        this.dob = dob;
//        this.registered = registered;
        this.phone = phone;
        this.cell = cell;
//        this.idId = idId;

        PictureEntity pic = new PictureEntity(picture.getId(), picture.getLarge(),
                picture.getMedium(), picture.getThumbnail());
        this.picture = pic;
        this.nat = nat;
    }

    public CareGiverEntity (CareGiver careGiver) {
        this.id = careGiver.getId();
        this.gender = careGiver.getGender();

        NameEntity nameModel = new NameEntity();
        nameModel.setFirst(careGiver.getName().getFirst());
        nameModel.setLast(careGiver.getName().getLast());
        nameModel.setTitle(careGiver.getName().getTitle());

        this.name = nameModel;
//        this.location = careGiver.getLocation();
        this.email = careGiver.getEmail();
//        this.login = careGiver.getLogin();
//        this.dob = careGiver.getDob();
//        this.registered = careGiver. getRegistered();
        this.phone = careGiver.getPhone();
        this.cell = careGiver.getCell();
//        this.idId = careGiver.getID();

        PictureEntity pic = new PictureEntity(careGiver.getPicture().getId(), careGiver.getPicture().getLarge(),
                careGiver.getPicture().getMedium(), careGiver.getPicture().getThumbnail());
        this.picture = pic;

        this.nat = careGiver.getNat();
    }

    public CareGiver getCareGiver() {
        CareGiver careGiver = new CareGiver();
        careGiver.setGender(this.getGender());

        Name nameModel = new Name();
        nameModel.setFirst(this.getName().getFirst());
        nameModel.setLast(this.getName().getLast());
        nameModel.setTitle(this.getName().getTitle());

        careGiver.setName(nameModel);

        careGiver.setEmail(this.getEmail());
        careGiver.setPhone(this.getPhone());
        careGiver.setCell(this.getCell());

        Picture pic = new Picture();
        pic.setLarge(this.getPicture().getLarge());
        pic.setMedium(this.getPicture().getMedium());
        pic.setThumbnail(this.getPicture().getThumbnail());

        careGiver.setPicture(pic);

        careGiver.setNat(this.getNat());

        return careGiver;
    }
}
