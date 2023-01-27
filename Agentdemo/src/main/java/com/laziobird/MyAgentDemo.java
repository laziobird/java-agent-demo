package com.laziobird;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
/**
 * @ClassName: AttachJVM
 * @Description: Agent 方法主入口
 * @Author: 蒋志伟
 * @date: 2023/1/26 9:00 PM
 * @Version: V1.0
 */
public class MyAgentDemo {
    // JVM 启动时，Agent修改字节码
    public static void premain(String args, Instrumentation inst) {

        System.out.println(" premain agent loaded !");
        inst.addTransformer(new PreMainTransformerDemo());
        System.out.println(" agent addTransformer start !");
    }

    // JVM运行时，Agent修改字节码
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println(" agentmain agent loaded !");
        Class[] allClass = inst.getAllLoadedClasses();
        for (Class c : allClass) {
            if (c.getName().contains("laziobird")){
                inst.addTransformer(new AgentMainTransformerDemo(), true);
                try {
                    //agentmain 是JVM运行时，需要调用 retransformClasses 重定义类 ！！
                    inst.retransformClasses(c);
                } catch (UnmodifiableClassException e) {
                    throw new RuntimeException(e);
                }
            }
		}
        System.out.println(" agentmain addTransformer start !");
    }
}
