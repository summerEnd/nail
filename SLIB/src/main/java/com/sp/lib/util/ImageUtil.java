package com.sp.lib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by acer on 2014/9/9.
 */
public class ImageUtil {

    public static final String IMAGE_TEMP_PUBLISH_SUPPLY = "publish_supply.png";
    public static final String IMAGE_TEMP_COMPANY_IMAGE = "company_image.png";


    /**
     * 获取图片暂存文件
     *
     * @param name
     * @return
     */
    public static File getImageTempFile(String name) {
        return new File(getImageDir(), name);
    }

    private static String getImageDir() {
        File pic_file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!pic_file.exists())
            pic_file.mkdirs();
        return pic_file.getAbsolutePath();
    }

    /**
     * 保存方法
     */
    public static Uri saveBitmap(Bitmap bm, String picName) {
        if (TextUtils.isEmpty(picName) || bm == null) return null;
        File f = new File(getImageDir(), picName);

        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Uri.fromFile(f);
    }

    public static Bitmap readBitmap(String picName) {
        Bitmap bitmap = null;
        try {
            File f = new File(getImageDir(), picName);
            FileInputStream inputStream = new FileInputStream(f);
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String base64Encode(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bytes = bos.toByteArray();
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 以一个bitmap的中心为圆点，以radius为半径，去截取
     *
     * @param src
     * @param radius 单位px
     * @return
     */
    public static Bitmap roundBitmap(Bitmap src, int radius) {
        int output_size = radius * 2;

        if (src == null) {
            ColorDrawable drawable = new ColorDrawable(0xDDe53769);
            src = Bitmap.createBitmap(
                    output_size,
                    output_size,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(src);
            drawable.setBounds(0,0,output_size,output_size);
            drawable.draw(canvas);
        }
        int src_w = src.getWidth();
        int src_h = src.getHeight();
        float scale;
        if (src_w > src_h) {
            scale = output_size / (float) src_h;
        } else {
            scale = output_size / (float) src_w;
        }
        src_w *= scale;
        src_h *= scale;
        Bitmap resizeSrc = Bitmap.createScaledBitmap(src, src_w, src_h, false);//缩放后的Bitmap

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap result = Bitmap.createBitmap(output_size, output_size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
//        canvas.drawARGB(0,0,0,0);//背景透明效果
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float translate_x = (result.getWidth() - resizeSrc.getWidth()) / 2;
        float translate_y = (result.getHeight() - resizeSrc.getHeight()) / 2;

        canvas.save();
        canvas.translate(translate_x, translate_y);
        canvas.drawBitmap(resizeSrc, 0, 0, paint);
        canvas.restore();

        if (result != resizeSrc && !resizeSrc.isRecycled()) resizeSrc.recycle();

        return result;
    }

    /**
     * 耗时方法，不可放在UI线程上
     *
     * @param path
     * @return
     */
    public static Bitmap getImageFromWeb(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
