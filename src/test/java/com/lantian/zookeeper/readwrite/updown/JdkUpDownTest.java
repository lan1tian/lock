package com.lantian.zookeeper.readwrite.updown;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JdkUpDownTest {

    /**
     * 测试jdk自带的ReentrantReadWriteLock锁升级的问题
     */
    private static void testReentrantReadWriteLockUpgrade() {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        readLock.lock();
        System.out.println("get read lock");
        //不支持锁升级，陷入死锁
        writeLock.lock();
        System.out.println("get write lock");
    }

    /**
     * 测试jdk自带的ReentrantReadWriteLock锁降级的问题
     */
    private static void testReentrantReadWriteLockDown() {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        System.out.println("get write lock");
        readLock.lock();
        System.out.println("get read lock");
        writeLock.lock();
        System.out.println("get write lock");
    }

    public static void main(String[] args) {
//        UpgradeTest.testReentrantReadWriteLockUpgrade();
        JdkUpDownTest.testReentrantReadWriteLockDown();
    }

}
