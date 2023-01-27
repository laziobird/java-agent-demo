package com.laziobird;

import java.util.Random;

/**
 * jiangzhiwei
 * 7:31 PM
 */
public class AgentAttachTest {

    public void test(int x) {
        try {
            long sleepTime = x*1000;
            Thread.sleep(sleepTime);
            System.out.println("the method: AgentAttachTest.test | sleep time = " + sleepTime+ "ms");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        AgentAttachTest agentTest = new AgentAttachTest();
        while (1==1){
            int x = new Random().nextInt(10);
            agentTest.test(x);
        }
    }

}
