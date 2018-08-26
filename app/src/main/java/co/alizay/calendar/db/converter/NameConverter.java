package co.alizay.calendar.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import co.alizay.calendar.db.entity.NameEntity;
import co.alizay.calendar.models.Name;

public class NameConverter {

    @TypeConverter
    public static NameEntity fromString(String json) {
        Type type = new TypeToken<NameEntity>() {}.getType();
        return new Gson().fromJson(json, type);
//        NameEntity nameEntity = new NameEntity();
//        nameEntity.setFirst(name.getFirst());
//        nameEntity.setLast(name.getLast());
//        nameEntity.setTitle(name.getTitle());
//        return nameEntity;
    }

    @TypeConverter
    public static String toString(NameEntity name) {
//        Name nameModel = new Name();
//        name.setFirst(name.getFirst());
//        name.setLast(name.getLast());
//        name.setTitle(name.getTitle());
//        return nameModel;
        Gson gson = new Gson();
        String json = gson.toJson(name);
        return json;
    }
}
