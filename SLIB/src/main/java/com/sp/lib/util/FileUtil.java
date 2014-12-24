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
 * 操作 /data/data/<package_name>/文件夹的工具类，写入文件的都是工具类
 */
public class FileUtil {

    private Context mContext;

    public FileUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 读取缓存文件
     */
    public Object readCache(String name) {
        return read(getCacheFile(name));
    }

    /**
     * 将object写入对象
     */
    public void saveCache(String name, Object o) {
        save(getCacheFile(name), o);
    }

    /**
     * 读取一个非缓存文件
     *
     * @param name
     * @return
     */
    public Object readFile(String name) {
        return read(getFile(name));
    }

    /**
     * 讲一个对象保存到 一个非缓存文件中
     *
     * @param name
     * @param o
     */
    public void saveFile(String name, Object o) {
        save(getFile(name), o);
    }

    /**
     * 将一个文件读出，这个文件必须为一个序列化的对象
     *
     * @param file
     * @return
     */
    public <T> T read(File file) {
        Object obj = null;
        try {
            FileInputStream is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("--->", "read:" + file + "===" + obj);
        return (T) obj;
    }

    /**
     * 讲一个对象o写入到file中
     *
     * @param file
     * @param o
     */
    public void save(File file, Object o) {
        Log.i("--->", "save:" + file + "--->" + o);
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

    public void delete(File file) {
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (File f : files) {
                delete(f);
            }
        } else {
            file.delete();
        }

    }


    /**
     * 缓存文件路径 /cache/<directory_name>/name
     *
     * @param name
     * @return
     */
    private File getCacheFile(String name) {
        //创建用户目录
        File dir = new File(mContext.getCacheDir(), "directory_name");
        if (!dir.exists()) dir.mkdir();
        //创建缓存文件
        return new File(dir, name);
    }

    /**
     * 文件路径：/file/name
     *
     * @param name
     * @return
     */
    private File getFile(String name) {
        return new File(mContext.getFilesDir(), name);
    }

}
