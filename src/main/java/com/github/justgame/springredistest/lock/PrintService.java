package com.github.justgame.springredistest.lock;

import org.springframework.stereotype.Service;

/**
 * @author xcl
 * @date 2019/12/2
 */
@Service
public class PrintService {
    private int i = 0;

    @DistributeAopLock
    public void print(String string) {
        i++;
        System.out.println(string);
    }

    public void printI() {
        System.out.println(i);
    }
}
