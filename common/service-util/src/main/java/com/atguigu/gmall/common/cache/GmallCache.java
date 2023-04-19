package com.atguigu.gmall.common.cache;

import java.lang.annotation.*;

/**
 * 元注解：简单理解就是修饰注解的注解
 * @Target:用于描述注解的使用范围，简单理解就是当前注解可以用在什么地方
 * @Retention:表示注解的生命周期
 *      SOURCE:只存在类文件中，在class字节码不存在
 *      CLASS：存在到字节码文件中
 *      RUNTIME：运行时
 * @Inherited：表示被GmallCache修饰的类的子类会不会继承GmallCache
 * @Documented：表明这个注解应该被javadoc工具记录，因此可悲javadoc类的工具文档化
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GmallCache {

    //缓存的前缀
    String prefix() default "cache:";

    //缓存的后缀
    String suffix() default ":info";

}
