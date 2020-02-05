package com.lantian;

public interface ReadWriteLock {

    Lock readLock();

    Lock writeLock();

}
