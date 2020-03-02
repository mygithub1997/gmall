package com.atguigu.gmall.index.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
        
        /**
         * 缓存的前缀
         *
         * @return
         */
        String prefix() default "";
        
        /**
         * 设置缓存的有效时间
         */
        int timeout() default 5;
        
        /**
         * 防止雪崩设置的随机值范围
         */
        int random() default 5;
        
        /**
         * 防止击穿，分布式锁的key
         */
        String lock() default "lock";
}

