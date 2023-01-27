package com.laziobird;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
/**
 * @ClassName: AttachJVM
 * @Description: JVM 运行时启动Agent
 * @Author: 蒋志伟
 * @date: 2023/1/26 9:00 PM
 * @Version: V1.0
 */
public class AgentMainTransformerDemo implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        //这次我们用另外一种简洁API方法修改字节码
        if (className.contains("com.laziobird")) {
            try {
                // 得到类信息
                CtClass ctclass = ClassPool.getDefault().get(className);
                for (CtMethod ctMethod : ctclass.getDeclaredMethods()) {
                    // 方法内部声明局部变量
                    ctMethod.addLocalVariable("start", CtClass.longType);
                    // 方法前插入Java代码片段
                    ctMethod.insertBefore("start = System.currentTimeMillis();");
                    String methodName = ctMethod.getLongName();
                    ctMethod.insertAfter("System.out.println(\"" + methodName + " cost: \" + (System" +
                            ".currentTimeMillis() - start));");
                    // 方法结束尾部插入Java代码片段
                    return ctclass.toBytecode();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
