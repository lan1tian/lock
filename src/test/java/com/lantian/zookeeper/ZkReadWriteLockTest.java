package com.lantian.zookeeper;

/**
 * zk读写锁测试
 */
public class ZkReadWriteLockTest {


    public static class FetchLockThread extends Thread {

        private ZkReadWriteLock zkReadWriteLock;

        public FetchLockThread(String source) {
            zkReadWriteLock = new ZkReadWriteLock(source, ZooKeeperUtil.client());
        }


        @Override
        public void run() {
            if (Math.random() > 0.5) {
                zkReadWriteLock.readLock().lock();
            } else {
                zkReadWriteLock.writeLock().lock();
            }
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new FetchLockThread("/order").start();
        }
    }


}