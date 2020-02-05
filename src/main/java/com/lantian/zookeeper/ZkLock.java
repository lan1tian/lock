package com.lantian.zookeeper;

import com.lantian.Lock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ZkLock implements Lock {

    private InterProcessLock interProcessMutex;
    private final String sourcePath;

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();


    public ZkLock(final CuratorFramework client, final String source) {
        if (client == null || source == null || "".equals(source.trim())) {
            throw new IllegalArgumentException();
        }
        this.sourcePath = source;
        this.interProcessMutex = new InterProcessMutex(client, this.sourcePath);
    }

    ZkLock(InterProcessLock interProcessMutex, String source) {
        this.sourcePath=source;
        this.interProcessMutex = interProcessMutex;
    }


    public void lock() {
        try {
            this.interProcessMutex.acquire();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tryLock() {
        return this.tryLock(1, TimeUnit.MILLISECONDS);
    }

    public boolean tryLock(int time, TimeUnit timeUnit) {
        try {
            return this.interProcessMutex.acquire(time, timeUnit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unlock() {
        try {
            this.interProcessMutex.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

}
