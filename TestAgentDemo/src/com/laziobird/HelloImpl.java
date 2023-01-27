package com.laziobird;

/**
 * jiangzhiwei
 * 4:41 PM
 */
public class HelloImpl implements Hello{
    @Override
    public String hi(String msg) {
        return ("hello " + msg);
    }
}
