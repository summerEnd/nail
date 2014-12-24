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
 * ���� /data/data/<package_name>/�ļ��еĹ����࣬д���ļ��Ķ��ǹ�����
 */
public class FileUtil {

    private Context mContext;

    public FileUtil(Context context) {
        this.mContext = context;
    }

    /**
     * ��ȡ�����ļ�
     */
    public Object readCache(String name) {
        return read(getCacheFile(name));
    }

    /**
     * ��objectд�����
     */
    public void saveCache(String name, Object o) {
        save(getCacheFile(name), o);
    }

    /**
     * ��ȡһ���ǻ����ļ�
     *
     * @param name
     * @return
     */
    public Object readFile(String name) {
        return read(getFile(name));
    }

    /**
     * ��һ�����󱣴浽 һ���ǻ����ļ���
     *
     * @param name
     * @param o
     */
    public void saveFile(String name, Object o) {
        save(getFile(name), o);
    }

    /**
     * ��һ���ļ�����������ļ�����Ϊһ�����л��Ķ���
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
     * ��һ������oд�뵽file��
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
     * �����ļ�·�� /cache/<directory_name>/name
     *
     * @param name
     * @return
     */
    private File getCacheFile(String name) {
        //�����û�Ŀ¼
        File dir = new File(mContext.getCacheDir(), "directory_name");
        if (!dir.exists()) dir.mkdir();
        //���������ļ�
        return new File(dir, name);
    }

    /**
     * �ļ�·����/file/name
     *
     * @param name
     * @return
     */
    private File getFile(String name) {
        return new File(mContext.getFilesDir(), name);
    }

}
