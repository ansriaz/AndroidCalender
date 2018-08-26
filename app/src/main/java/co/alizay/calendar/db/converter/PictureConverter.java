package co.alizay.calendar.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import co.alizay.calendar.db.entity.PictureEntity;
import co.alizay.calendar.models.Picture;

public class PictureConverter {

    @TypeConverter
    public static PictureEntity toPicture(String json) {

//        PictureEntity pictureEntity = new PictureEntity();
//        pictureEntity.setId(picture.getId());
//        pictureEntity.setLarge(picture.getLarge());
//        pictureEntity.setMedium(picture.getMedium());
//        pictureEntity.setMedium(picture.getThumbnail());
//        return pictureEntity;

        Type type = new TypeToken<PictureEntity>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    @TypeConverter
    public static String toString(PictureEntity pictureEntity) {

//        Picture picture = new Picture();
//        pictureEntity.setId(picture.getId());
//        pictureEntity.setLarge(picture.getLarge());
//        pictureEntity.setMedium(picture.getMedium());
//        pictureEntity.setMedium(picture.getThumbnail());
//        return picture;

        Gson gson = new Gson();
        String json = gson.toJson(pictureEntity);
        return json;
    }

}
