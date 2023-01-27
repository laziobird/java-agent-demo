package com.laziobird.asm;

import com.laziobird.Hello;
import com.laziobird.HelloImpl;
import org.junit.Test;

/**
 * jiangzhiwei
 * 4:37 PM
 */
public class TestASMProxy {
    @Test
    public void TestASM() throws Exception {
        Hello hello = ASMDemo.getProxy(HelloImpl.class);
        String invoke = hello.hi("运行一个实现hello接口的hi方法的调用！");
        System.out.println(invoke);
    }
}
