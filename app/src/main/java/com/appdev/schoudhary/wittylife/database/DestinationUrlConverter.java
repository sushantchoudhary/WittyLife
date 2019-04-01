package com.appdev.schoudhary.wittylife.database;

import android.arch.persistence.room.TypeConverter;

import com.appdev.schoudhary.wittylife.model.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DestinationUrlConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static Urls stringToURL(String data) {
        if (data == null) {
            return null;
        }

        Type listType = new TypeToken<Urls>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String URLToString(Urls url) {
        return gson.toJson(url);
    }
}
