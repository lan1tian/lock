package com.lantian.zookeeper.readwrite;

import com.lantian.zookeeper.ZkLock;
import com.lantian.zookeeper.ZooKeeperUtil;

import java.util.concurrent.TimeUnit;

/**
 * zookeeper分布式锁测试
 */
public class ZkLockTest {

    public static class FetchLockThread extends Thread{

        public void run() {
            ZkLock lock = new ZkLock(ZooKeeperUtil.client(), "/fetch");
            lock.lock();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new FetchLockThread().start();
        }
        TimeUnit.SECONDS.sleep(1000);
    }

}
