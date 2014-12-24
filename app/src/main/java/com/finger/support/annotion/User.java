package com.finger.support.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标志这个类只为用户服务
 */
@Target(ElementType.TYPE)
public @interface User {
}
