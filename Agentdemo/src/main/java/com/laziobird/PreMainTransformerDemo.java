package com.laziobird;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import javassist.*;
/**
 * @ClassName: AttachJVM
 * @Description: 程序启动时运行Agent
 * @Author: 蒋志伟
 * @date: 2023/1/26 9:00 PM
 * @Version: V1.0
 */
public class PreMainTransformerDemo implements ClassFileTransformer {

    final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // className 默认格式 com/laziobird 替换 com.laziobird
        className = className.replace("/", ".");
        //java自带的方法不进行处理,不是特别类的方法也不处理
        if (className.startsWith("java") || className.startsWith("sun") || !className.contains("com.laziobird")) {
            return null;
        }

        CtClass ctclass = null;
        try {
            // 使用全称,用于取得字节码类<使用javassist>
            ctclass = ClassPool.getDefault().get(className);
            for (CtMethod ctMethod : ctclass.getDeclaredMethods()) {
                String methodName = ctMethod.getName();
                // 新定义一个方法叫做比如sayHello$old
                String newMethodName = methodName + "$old";
                // 将原来的方法名字修改
                ctMethod.setName(newMethodName);
                // 创建新的方法，复制原来的方法，名字为原来的名字
                CtMethod newMethod = CtNewMethod.copy(ctMethod, methodName, ctclass, null);

                // 构建新的方法体
                StringBuilder bodyStr = new StringBuilder();
                bodyStr.append("{");
                bodyStr.append("System.out.println(\"==============Enter Method: " + className + "." + methodName + " ==============\");");
                //方法执行前，定义一个时间变量，记录方法开始前时间
                bodyStr.append(prefix);
                bodyStr.append(newMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                //定义方法完成时间变量
                bodyStr.append(postfix);
                //方法完成后，运算方法执行时间
                bodyStr.append("System.out.println(\"==============Exit Method: " + className + "." + methodName + " Cost:\" +(endTime - startTime) +\"ms " + "===\");");
                bodyStr.append("}");
                // 新方法字节码替换原来的方法字节码
                newMethod.setBody(bodyStr.toString());
                ctclass.addMethod(newMethod);// 增加新方法
            }
            //返回新的字节流
            return ctclass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
