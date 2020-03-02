package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {
        
        @Autowired
        private GmallPmsClient pmsClient;
        
        @Autowired
        private StringRedisTemplate redisTemplate;
        
        @Autowired
        private RedissonClient redissonClient;
        
        private static final String KEY_PREFIX = "index:cates:";
        
        public List<CategoryEntity> queryLvl1Category() {
                
                Resp<List<CategoryEntity>> categoriesResp = this.pmsClient.queryCategoriesByLevelOrPid(1, null);
                
                return categoriesResp.getData();
        }
        
        @GmallCache(prefix = "index:cates:",timeout = 14400,random = 3600,lock = "lock")
        public List<CategoryVO> queryLvl2WithSubByPid(Long pid) {
                // 缓存中没有，查询数据库并放入缓存
                Resp<List<CategoryVO>> listResp = this.pmsClient.queryCategoryWithSubByPid(pid);
                List<CategoryVO> categoryVOS = listResp.getData();
                return categoryVOS;
        }
        
        public List<CategoryVO> queryLvl2WithSubByPid2(Long pid) {
                // 先查询缓存，缓存中有直接命中返回
                String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
                if (StringUtils.isNotBlank(json)) {
                        return JSON.parseArray(json, CategoryVO.class);
                }
                
                RLock lock = this.redissonClient.getLock("lock" + pid);
                lock.lock();
                
                //再加一次判断
                String json2 = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
                if (StringUtils.isNotBlank(json)) {
                        lock.unlock();
                        return JSON.parseArray(json, CategoryVO.class);
                }
                
                // 缓存中没有，查询数据库并放入缓存
                Resp<List<CategoryVO>> listResp = this.pmsClient.queryCategoryWithSubByPid(pid);
                List<CategoryVO> categoryVOS = listResp.getData();
                
                // 判断返回数据是否为空
                if (!CollectionUtils.isEmpty(categoryVOS)) {
                        //给缓存时间添加随机值，以免缓存雪崩
                        this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVOS), 10 + (int) Math.random() * 10, TimeUnit.DAYS);
                } else {
                        this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVOS), 5 + (int) Math.random() * 10, TimeUnit.DAYS);
                }
                
                return categoryVOS;
        }

//    public void testLock() {
//            String num = this.redisTemplate.opsForValue().get("num");
//            if(StringUtils.isEmpty(num)){
//                num = "0";
//                this.redisTemplate.opsForValue().set("num",num);
//            }
//                int n = Integer.parseInt(num);
//                this.redisTemplate.opsForValue().set("num",String.valueOf(++n));
//    }


//        public void testLock(){
//                //为每个请求生成uuid
//                String uuid = UUID.randomUUID().toString();
//                //获取锁
//                Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
//                //获取到锁执行业务
//                if(lock){
//                        String num = this.redisTemplate.opsForValue().get("num");
//                        if(StringUtils.isEmpty(num)){
//                                num = "0";
//                                this.redisTemplate.opsForValue().set("num",num);
//                        }
//                        int n = Integer.parseInt(num);
//                        this.redisTemplate.opsForValue().set("num",String.valueOf(++n));
//                        //执行完成后释放锁
////                        if(StringUtils.equals(uuid,this.redisTemplate.opsForValue().get("lock"))){
////                                this.redisTemplate.delete("lock");
////                        }
//                        //使用lua脚本保证判断和删除的原子性，防止误删
//                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
//                                "then return redis.call('del', KEYS[1]) " +
//                                "else return 0 end";
//                        this.redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"),uuid);
//                }else{
//                        //获取不到则重试
//                        try {
//                                Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                                e.printStackTrace();
//                        }
//                }
//        }
        
        public void testLock() {
                RLock lock = this.redissonClient.getLock("lock");
                //加锁
                lock.lock();
                
                String num = this.redisTemplate.opsForValue().get("num");
                if (StringUtils.isEmpty(num)) {
                        num = "0";
                        this.redisTemplate.opsForValue().set("num", num);
                }
                int n = Integer.parseInt(num);
                this.redisTemplate.opsForValue().set("num", String.valueOf(++n));
                
                //解锁
                lock.unlock();
        }
        
        public void testRead() {
                RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("RwLock");
                rwLock.readLock().lock();
                
                try {
                        Thread.sleep(300);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
                
                rwLock.readLock().unlock();
        }
        
        public void testWrite() {
                RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("RwLock");
                rwLock.writeLock().lock(10, TimeUnit.SECONDS);
                
                try {
                        Thread.sleep(500);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
                
                rwLock.writeLock().unlock();
        }
        
        public void testLatch() throws InterruptedException {
                RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("cdLatch");
                countDownLatch.trySetCount(6l);
                
                countDownLatch.await();
        }
        
        public void testCountDown() {
                RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("cdLatch");
                countDownLatch.countDown();
                
                
        }
}
