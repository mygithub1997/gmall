package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {
        
        @Autowired
        private StringRedisTemplate redisTemplate;
        
        @Autowired
        private RedissonClient redissonClient;
        
        /**
         * @param joinPoint
         * @return
         * @throws Throwable joinPoint.getArgs();获取目标类
         */
        @Around("@annotation(com.atguigu.gmall.index.config.GmallCache)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
                
                //获取切点方法的签名
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                //获取方法对象
                Method method = methodSignature.getMethod();
                //获取方法上面指定注解的对象
                GmallCache annotation = method.getAnnotation(GmallCache.class);
                //获取方法中的前缀
                String prefix = annotation.prefix();
                //获取方法的参数
                Object[] args = joinPoint.getArgs();
                String param = Arrays.asList(args).toString();
                //获取方法的返回值类型
                Class<?> returnType = method.getReturnType();
                
                //拦截前代码块：判断缓存中有没有
                String json = this.redisTemplate.opsForValue().get(prefix + param);
                if (StringUtils.isNotBlank(json)) {
                        return JSON.parseObject(json, returnType);
                }
                
                //如果没有则加入分布式锁，防止缓存击穿
                String lock = annotation.lock();
                RLock rLock = this.redissonClient.getLock(lock + param);
                rLock.lock();
                
                //判断缓存中有没有，有直接返回（加锁的过程中，别的请求可能已经把数据放入缓存中）
                String json2 = this.redisTemplate.opsForValue().get(prefix + param);
                if(StringUtils.isNotBlank(json2)){
                        rLock.unlock();
                        return JSON.parseObject(json2,returnType);
                }
                
                
                //执行目标方法
                Object result = joinPoint.proceed(joinPoint.getArgs());
                
                //拦截后代码块
                int timeout = annotation.timeout();
                int random = annotation.random();
                this.redisTemplate.opsForValue().set(prefix+param,JSON.toJSONString(result),timeout+new Random().nextInt(random), TimeUnit.MINUTES);
                rLock.unlock();
        
                return result;
                
        }
}
