package com.lantian.zookeeper.readwrite.updown;

import com.lantian.zookeeper.ZkReadWriteLock;
import com.lantian.zookeeper.ZooKeeperUtil;
import org.apache.curator.framework.CuratorFramework;

/**
 * 测试zookeeper读写锁
 */
public class ZkUpDownTest {

    private static ZkReadWriteLock zkReadWriteLock;

    private static CuratorFramework client(){
        return ZooKeeperUtil.client();
    }

    public static void upGrade(){
        ZkReadWriteLock zkReadWriteLock=new ZkReadWriteLock("/fetchData",ZkUpDownTest.client());
        zkReadWriteLock.readLock().lock();
        System.out.println("get read lock");
        zkReadWriteLock.writeLock().lock();
        System.out.println("get write lock");
    }

    public static void downGrade(){
        ZkReadWriteLock zkReadWriteLock=new ZkReadWriteLock("/fetchData",ZkUpDownTest.client());
        zkReadWriteLock.writeLock().lock();
        System.out.println("get write lock");
        zkReadWriteLock.readLock().lock();
        System.out.println("get read lock");
        zkReadWriteLock.writeLock().lock();
        System.out.println("get write lock");
    }

    public static void main(String[] args) {
        ZkUpDownTest.upGrade();
    }

}
