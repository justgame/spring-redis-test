package com.github.justgame.springredistest.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author xcl
 * @date 2019/11/29
 */
@Component
@Slf4j
public class DistributeLock {

    private static final int WAIT_MILLIS = 100;

    private static final int RETRY_TIMES = 5;

    private static final int EXPIRE_MILLIS = 30000;

    private final StringRedisTemplate redisTemplate;

    private static final String UNLOCK_LUA;

    private final ThreadLocal<String> lockFlag = new ThreadLocal<>();

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
            "then " +
            "    return redis.call(\"del\",KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end ";
    }

    public DistributeLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void lock(String key) {
        lock(key, WAIT_MILLIS, Integer.MAX_VALUE, EXPIRE_MILLIS);
    }

    @SuppressWarnings("ConstantConditions")
    public void lock(String key, int waitMillis, int retryTimes, int expireMillis) {
        String uniqueStr = UUID.randomUUID().toString();
        boolean result = redisTemplate.opsForValue().setIfAbsent(key, uniqueStr, expireMillis, TimeUnit.MILLISECONDS);
        while (!result && retryTimes-- > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(waitMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result = redisTemplate.opsForValue().setIfAbsent(key, uniqueStr, expireMillis, TimeUnit.MILLISECONDS);
        }
        if (result) {
            lockFlag.set(uniqueStr);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean tryLock(String key) {
        String uniqueStr = UUID.randomUUID().toString();
        if (redisTemplate.opsForValue().setIfAbsent(key, uniqueStr, EXPIRE_MILLIS, TimeUnit.MILLISECONDS)) {
            lockFlag.set(uniqueStr);
            return true;
        }
        return false;
    }

    public void releaseLock(String key) {
        redisTemplate.execute((RedisConnection redisConnection) ->
            redisConnection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(), lockFlag.get().getBytes()));
        lockFlag.remove();
    }
}
