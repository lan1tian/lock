package com.lantian.redis.redlock;

import com.lantian.Lock;
import com.lantian.redis.RedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonLock implements Lock {

    private final RLock lock;

    public RedissonLock(String source) {
        if (source == null || "".equals(source.trim())) {
            throw new IllegalArgumentException();
        }
        Config config = new Config();
        config.useSentinelServers()
                .addSentinelAddress(RedisConfig.RED_LOCK_CLUSTER)
                .setMasterName(RedisConfig.MASTER_NAME);
        RedissonClient redissonClient = Redisson.create(config);
        this.lock = redissonClient.getLock(source);
    }

    public void lock() {
        this.tryLock(Integer.MAX_VALUE, TimeUnit.DAYS);
    }

    public boolean tryLock() {
        return this.lock.tryLock();
    }

    public boolean tryLock(int time, TimeUnit timeUnit) {
        try {
            return this.lock.tryLock(time, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public void unlock() {
        try {
            this.lock.unlock();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }


}
