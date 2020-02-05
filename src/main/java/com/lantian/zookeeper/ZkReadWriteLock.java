package com.lantian.zookeeper;

import com.lantian.Lock;
import com.lantian.ReadWriteLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

public class ZkReadWriteLock implements ReadWriteLock {

    private final InterProcessReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;

    public ZkReadWriteLock(String source, CuratorFramework client) {
        this.readWriteLock = new InterProcessReadWriteLock(client, source);
        this.readLock = new ZkLock(readWriteLock.readLock(), source);
        this.writeLock = new ZkLock(readWriteLock.writeLock(), source);
    }

    public Lock readLock() {
        return this.readLock;
    }

    public Lock writeLock() {
        return this.writeLock;
    }


}
