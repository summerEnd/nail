package com.sp.lib.util;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by acer on 2014/12/31.
 */
public class SXml {
    public static <T> List<T> parse(Context context, int xmlId, Class<T> cls) throws IllegalAccessException, InstantiationException {
        XmlResourceParser parser = context.getResources().getXml(xmlId);
        LinkedList<T> products = new LinkedList<T>();


        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    T product = cls.newInstance();

                    Field[] fields = cls.getDeclaredFields();
                    for (int j = 0; j < fields.length; j++) {
//                        for (int i = 0; i < parser.getAttributeCount(); i++) {
//                            if (parser.getAttributeName(i).equals(fields[j].getName())){
//                                fields[j].set(product,parser.geta);
//                            }
//                        }
//                        product.value = parser.getAttributeIntValue(0, 0);
//                        product.from = parser.getAttributeIntValue(1, 0);
//                        product.to = parser.getAttributeIntValue(2, 0);
//                        products.add(product);
                    }
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
