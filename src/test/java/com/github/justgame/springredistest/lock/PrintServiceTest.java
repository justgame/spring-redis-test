package com.github.justgame.springredistest.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xcl
 * @date 2019/12/2
 */
@SpringBootTest
class PrintServiceTest {
    @Autowired
    private PrintService printService;

    @Test
    void testPrint() throws InterruptedException {
        String str = "hello";
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 10000; i++) {
            executor.execute(() -> {
                printService.print(str);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        printService.printI();
    }
}