package com.lantian.redis.redlock;

import org.junit.Test;

public class RedissonLockTest {

    @Test
    public void lock(){
        RedissonLock lock = new RedissonLock("fetch-data");
        lock.lock();
        lock.unlock();
    }

}