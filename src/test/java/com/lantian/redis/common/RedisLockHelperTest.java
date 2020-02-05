package com.lantian.redis.common;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisLockHelperTest {

    private RedisLockHelper redisLockHelper = RedisLockHelper.instance();

    @Test
    public void trySetnx() {
        boolean result = this.redisLockHelper.trySetnx("fetch-data", UUID.randomUUID().toString());
        System.out.println(result);
    }

    @Test
    public void trySetnxInTime() {
        boolean result = this.redisLockHelper.trySetnx("fetch-data", UUID.randomUUID().toString(), 10, TimeUnit.SECONDS);
        System.out.println(result);
    }

    @Test
    public void remove() {
        this.redisLockHelper.remove("fetch-data", "9cf0ca93-90ac-4d59-b344-5b2a499d2415");
    }

    @Test
    public void renewLock() {
        boolean result = this.redisLockHelper.renewLock("fetch-data", "d66e1873-233d-4a44-a0ff-d95e1be3d948");
        System.out.println(result);
    }


}