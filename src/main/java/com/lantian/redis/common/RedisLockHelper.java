package com.lantian.redis.common;

import com.lantian.redis.RedisConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class RedisLockHelper {

    private static volatile RedisLockHelper redisLockHelper;

    private static final int RETRY_AWAIT_MS = 1;
    private static final int KEY_EXPIRE_MS = 30000;

    public static RedisLockHelper instance() {
        if (redisLockHelper == null) {
            synchronized (RedisLockHelper.class) {
                if (redisLockHelper == null) {
                    redisLockHelper = new RedisLockHelper(new JedisPool(RedisConfig.HOST, RedisConfig.PORT));
                }
            }
        }
        return redisLockHelper;
    }

    private JedisPool jedisPool;

    public RedisLockHelper(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean trySetnx(String source, String value) {
        final String luaScript = "" +
                "local r = tonumber(redis.call('SETNX',KEYS[1],ARGV[1]));\n" +
                "if(r == 1) then\n" +
                "\tredis.call('PEXPIRE',KEYS[1],ARGV[2]);\n" +
                "end;\n" +
                "return r;";
        Jedis conn = null;
        try {
            conn = jedisPool.getResource();
            List<String> keys = Collections.singletonList(source);
            List<String> args = Arrays.asList(value, String.valueOf(KEY_EXPIRE_MS));
            Integer result = Integer.valueOf(String.valueOf(conn.eval(luaScript, keys, args)));
            return result == 1;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public boolean trySetnx(String source, String value, int time, TimeUnit timeUnit) {
        long remain = timeUnit.toMillis(time);
        do {
            long start = System.currentTimeMillis();
            boolean result = this.trySetnx(source, value);
            if (result) {
                return true;
            }
            long end = System.currentTimeMillis();
            //在本地虚拟机条件下,网络耗时(end-start)为0，可能会陷入长时间循环
            remain = remain - (end - start);
            if (TimeUnit.MILLISECONDS.toNanos(remain) > RETRY_AWAIT_MS) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RETRY_AWAIT_MS));
            }
        } while (remain > 0);
        return false;
    }


    public void remove(String source, String value) {
        final String luaScript = "" +
                "local r = redis.call('GET',KEYS[1]);\n" +
                "if r == ARGV[1] then\n" +
                "    redis.call('DEL',KEYS[1]);\n" +
                "    return 1;\n" +
                "else\n" +
                "\treturn 0;\n" +
                "end";
        Jedis conn = null;
        try {
            conn = jedisPool.getResource();
            List<String> keys = Collections.singletonList(source);
            List<String> args = Collections.singletonList(value);
            conn.eval(luaScript, keys, args);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }


    public boolean renewLock(String source, String value) {
        final String luaScript = "" +
                "if(redis.call('GET',KEYS[1])==ARGV[1]) then\n" +
                "\tredis.call('PEXPIRE',KEYS[1],ARGV[2]);\n" +
                "\treturn 1;\n" +
                "else \n" +
                "\treturn 0;\n" +
                "end";
        Jedis conn = null;
        try {
            conn = jedisPool.getResource();
            List<String> keys = Collections.singletonList(source);
            List<String> args = Arrays.asList(value, String.valueOf(KEY_EXPIRE_MS));
            return Integer.valueOf(String.valueOf(conn.eval(luaScript, keys, args))) == 1;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
