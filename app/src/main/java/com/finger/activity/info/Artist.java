package com.finger.activity.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标志这个类只为美甲师服务
 */
@Target(ElementType.TYPE)
public @interface Artist {
}
