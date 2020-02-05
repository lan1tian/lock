package com.lantian.zookeeper.hunger;

import com.lantian.zookeeper.ZkReadWriteLock;
import com.lantian.zookeeper.ZooKeeperUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 写饥饿测试
 */
public class HungerTest {

    private static final AtomicInteger idGenerator = new AtomicInteger();

    public static class FetchReadLockThread extends Thread {

        private ZkReadWriteLock zkReadWriteLock;
        private int id;

        public FetchReadLockThread(String source) {
            zkReadWriteLock = new ZkReadWriteLock(source, ZooKeeperUtil.client());
            id = idGenerator.getAndIncrement();
        }


        @Override
        public void run() {
            zkReadWriteLock.readLock().lock();
            System.out.println("id:" + id + ",get read lock ");
        }

    }

    public static class FetchWriteLockThread extends Thread {


        private ZkReadWriteLock zkReadWriteLock;
        private int id;

        public FetchWriteLockThread(String source) {
            id = idGenerator.incrementAndGet();
            zkReadWriteLock = new ZkReadWriteLock(source, ZooKeeperUtil.client());
        }

        @Override
        public void run() {
            zkReadWriteLock.writeLock().lock();
            System.out.println("id:" + id + ",get write lock ");
        }

    }

    public static void main(String[] args) {
        final String source = "/fetchData";
        new FetchReadLockThread(source).start();
        new FetchReadLockThread(source).start();
        new FetchReadLockThread(source).start();
        new FetchWriteLockThread(source).start();
        new FetchReadLockThread(source).start();
    }


}
