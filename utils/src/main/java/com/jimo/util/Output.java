package com.jimo.util;

/**
 * 输出工具类
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/16 15:49
 */
public class Output {

    /**
     * 输出： print("a={},b={}",a,b)
     */
    public static void print(String pattern, Object... args) {
        for (Object arg : args) {
            pattern = pattern.replaceFirst("\\{}", arg.toString());
        }
        System.out.println(pattern);
    }
}
