package com.laziobird;

/**
 * jiangzhiwei
 * 7:31 PM
 */
public class AgentTest {
    public void test() {
        System.out.println("hello the method: agentTest.test ");

    }
    public void testB() {
        ClassC c = new ClassC();
        c.methodD();
        System.out.println("hello the method: agentTest.testB ");
    }
    public static void main(String[] args) {
        AgentTest agentTest = new AgentTest();
        agentTest.test();
        agentTest.testB();
    }
}




