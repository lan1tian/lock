package com.lantian.redis.common;

import com.lantian.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RedisLock implements Lock {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(2);


    private final String source;
    private final ThreadLocal<LockData> lockDataThreadLocal;
    private final RedisLockHelper redisLockHelper;


    public RedisLock(String source) {
        if (source == null || "".equals(source.trim())) {
            throw new RuntimeException("source cannot be null ");
        }
        this.source = source;
        this.lockDataThreadLocal = new ThreadLocal<LockData>();
        this.redisLockHelper = RedisLockHelper.instance();
    }


    public void lock() {
        this.tryLock(Integer.MAX_VALUE, TimeUnit.DAYS);
    }

    private String generateLockData() {
        return UUID.randomUUID().toString();
    }

    public boolean tryLock() {
        LockData lockData = this.lockDataThreadLocal.get();
        if (lockData != null) {
            lockData.count++;
            return true;
        } else {
            lockData = new LockData(1, this.generateLockData());
        }
        boolean locked = redisLockHelper.trySetnx(source, lockData.value);
        if (locked) {
            lockDataThreadLocal.set(lockData);
            lockData.future = EXECUTOR.scheduleAtFixedRate(new RenewTask(lockData), 0, 10, TimeUnit.SECONDS);
        }
        return locked;
    }

    public boolean tryLock(int time, TimeUnit timeUnit) {
        if (time <= 0 || timeUnit == null)
            throw new RuntimeException("pramater is invalid");
        LockData lockData = this.lockDataThreadLocal.get();
        if (lockData != null) {
            lockData.count++;
            return true;
        } else {
            lockData = new LockData(1, this.generateLockData());
        }
        boolean locked = this.redisLockHelper.trySetnx(source, lockData.value, time, timeUnit);
        if (locked) {
            lockDataThreadLocal.set(lockData);
            lockData.future = EXECUTOR.scheduleAtFixedRate(new RenewTask(lockData), 10, 10, TimeUnit.SECONDS);
        }
        return locked;
    }


    public void unlock() {
        LockData lockData = this.lockDataThreadLocal.get();
        if (lockData == null)
            return;
        else if (lockData.count > 1) {
            lockData.count--;
        } else {
            this.redisLockHelper.remove(this.source, lockData.value);
            this.lockDataThreadLocal.set(null);
            lockData.future.cancel(true);
        }
    }

    private class LockData {
        int count;
        final String value;
        ScheduledFuture future;

        public LockData(int count, final String value) {
            this.count = count;
            this.value = value;
        }
    }


    private class RenewTask implements Runnable {

        private final LockData lockData;
        private final Thread thread;

        public RenewTask(LockData lockData) {
            this.lockData = lockData;
            this.thread = Thread.currentThread();
        }

        public void run() {
            RedisLock.LOGGER.info("renew lock {}", RedisLock.this.source);
            boolean renewed = RedisLock.this.redisLockHelper.renewLock(RedisLock.this.source, lockData.value);
            if (!renewed) {
                thread.interrupt();
                lockData.future.cancel(true);
            }
        }

    }


}
