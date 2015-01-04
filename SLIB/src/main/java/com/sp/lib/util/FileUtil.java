package com.sp.lib.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by acer on 2014/9/25.
 */
public class FileUtil {

    /**
     * 读取缓存文件
     */
    public static Object readCache(Context context,String user, String name) {
        return read(getCacheFile(context,user, name));

    }

    /**
     * 将object写入缓存
     */
    public static void saveCache(Context context, String user, String name, Object o) {
        save(getCacheFile(context, user, name), o);
    }

    /**
     * 读取一个非缓存文件
     *
     * @param name
     * @return
     */
    public static Object readFile(Context context, String name) {
        return read(getFile(context, name));
    }

    /**
     * 讲一个对象保存到 一个非缓存文件中
     *
     * @param name
     * @param o
     */
    public static void saveFile(Context context, String name, Object o) {
        save(getFile(context, name), o);
    }

    /**
     * 将一个文件读出，这个文件必须为一个序列化的对象
     *
     * @param file
     * @return
     */
    public static Object read(File file) {
        Object obj = null;
        try {
            FileInputStream is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            Log.w("FileUtil", e.getMessage());
        }
        return obj;
    }

    /**
     * 讲一个对象o写入到file中
     *
     * @param file
     * @param o
     */
    public static void save(File file, Object o) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 鍒犻櫎鏂囦欢
     *
     * @param file
     */
    public static void delete(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
    }

    /**
     * 鍒犻櫎鏂囦欢
     *
     * @param context
     * @param name
     */
    public static void deleteFile(Context context, String name) {
        delete(getFile(context, name));
    }

    /**
     * 缂撳瓨鏂囦欢璺緞 cache/<company_id>/name
     *
     * @param context
     * @param name
     * @return
     */
    private static File getCacheFile(Context context, String user, String name) {

        //鍒涘缓缂撳瓨鏂囦欢
        return new File(getCacheDir(context, user), name);
    }

    /**
     * 鑾峰彇缂撳瓨鐩綍
     *
     * @param context
     * @return
     */
    private static File getCacheDir(Context context, String name) {
        File dir = new File(context.getCacheDir(), name);
        if (!dir.exists()) dir.mkdir();
        return dir;
    }

    private static File getFile(Context context, String name) {
        return new File(context.getFilesDir(), name);
    }

    public static void clearCache(Context context, String name) {
        delete(getCacheDir(context, name));
    }
}
