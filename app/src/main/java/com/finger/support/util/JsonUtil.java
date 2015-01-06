package com.finger.support.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2014/9/5.
 */
public class JsonUtil {
    public static <T> T get(String json, Class<T> cls) {
        return (T) (new Gson().fromJson(json, cls));
    }


    public static <T> ArrayList<T> getArray(JSONArray array, Class<T> cls) throws JSONException {
        ArrayList<T> list = new ArrayList<T>();
        Gson gson = new Gson();

        for (int i = 0; i < array.length(); i++) {
            T t = gson.fromJson(array.get(i).toString(), cls);
            list.add(t);
        }
        return list;
    }

    public static <T> void getArray(JSONArray array, Class<T> cls, List<T> list) throws JSONException {
        Gson gson = new Gson();

        for (int i = 0; i < array.length(); i++) {
            T t = gson.fromJson(array.get(i).toString(), cls);

            list.add(t);
        }
    }

}
