package com.sp.lib.exception;

import android.content.Context;

import com.sp.lib.util.FileUtil;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by acer on 2014/12/5.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Context context;
    private static Thread.UncaughtExceptionHandler defaultHandler;

    public static class ErrorLog implements Serializable{
        public String msg;
        public String time;
    }

    public static void init(Context context) {
        ExceptionHandler.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LinkedList<ErrorLog> logs = (LinkedList<ErrorLog>) FileUtil.readFile(context, "debugs");
        if (logs == null) logs = new LinkedList<ErrorLog>();

        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);

        ex.printStackTrace(pw);
        ErrorLog log=new ErrorLog();
        log.msg=sw.toString();
        log.time=new Date().toString();
        logs.add(log);

        FileUtil.saveFile(context, "debugs", logs);
        defaultHandler.uncaughtException(thread, ex);
    }

    public static final LinkedList<ErrorLog> getErrors(){
        return (LinkedList<ErrorLog>) FileUtil.readFile(context,"debugs");
    }

    public static final void clear(){
        FileUtil.deleteFile(context,"debugs");
    }

}
