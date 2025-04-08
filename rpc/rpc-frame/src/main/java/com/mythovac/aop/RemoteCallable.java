package com.mythovac.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 仅适用于方法
@Retention(RetentionPolicy.RUNTIME) // 在运行时保留注解信息
public @interface RemoteCallable {
    // 可以添加注解的属性，例如方法名等
    String value() default "";
}
