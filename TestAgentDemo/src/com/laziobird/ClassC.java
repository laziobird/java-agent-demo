package com.laziobird;

/**
 * jiangzhiwei
 * 9:33 PM
 */
public class ClassC {
    public void methodD(){
        try {
            System.out.println(" methodD start!");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
