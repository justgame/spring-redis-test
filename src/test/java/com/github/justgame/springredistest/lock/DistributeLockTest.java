package com.github.justgame.springredistest.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xcl
 * @date 2019/11/29
 */
@SpringBootTest
class DistributeLockTest {
    @Autowired
    private DistributeLock lock;

    @Test
    void testLock() throws InterruptedException {
        final int[] c = {0};
        String key = "key";
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 10000; i++) {
            executor.execute(() -> {
//                c[0]++;
                lock.lock(key);
                try {
                    c[0]++;
                } finally {
                    lock.releaseLock(key);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(c[0]);
    }
}
