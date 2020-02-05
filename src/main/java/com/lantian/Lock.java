package com.lantian;

import java.util.concurrent.TimeUnit;


public interface Lock {

    void lock();

    boolean tryLock();

    boolean tryLock(int time, TimeUnit timeUnit);

    void unlock();

}
