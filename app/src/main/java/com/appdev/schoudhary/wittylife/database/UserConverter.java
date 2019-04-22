package com.appdev.schoudhary.wittylife.database;

import android.arch.persistence.room.TypeConverter;

import com.appdev.schoudhary.wittylife.model.Urls;
import com.appdev.schoudhary.wittylife.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class UserConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static User stringToUser(String data) {
        if (data == null) {
            return null;
        }

        Type listType = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String UserToString(User user) {
        return gson.toJson(user);
    }
}
