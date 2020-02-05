package com.lantian.redis.common;


import com.lantian.Lock;

import java.util.concurrent.TimeUnit;

public class RedisLockTest {

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new RedisLock("fetch-data");
        lock.lock();
        TimeUnit.SECONDS.sleep(30);
        lock.unlock();
        TimeUnit.SECONDS.sleep(30);
        lock.tryLock();
        lock.unlock();
    }

}