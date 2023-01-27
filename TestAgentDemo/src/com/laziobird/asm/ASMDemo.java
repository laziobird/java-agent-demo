package com.laziobird.asm;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * jiangzhiwei
 * 4:34 PM
 */
public class ASMDemo extends ClassLoader{
    public static <T> T getProxy(Class clazz) throws Exception {


        ClassReader classReader = new ClassReader(clazz.getName());
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

        classReader.accept(new ClassVisitor(ASM5, classWriter) {

            @Override
            public MethodVisitor visitMethod(int access, final String name, String descriptor, String signature, String[] exceptions) {


                // 方法过滤
                if (!"hi".equals(name))
                    return super.visitMethod(access, name, descriptor, signature, exceptions);

                final MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);

                return new AdviceAdapter(ASM5, methodVisitor, access, name, descriptor) {
                    @Override
                    protected void onMethodEnter() {

                        // 执行指令；获取静态属性
                        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        // 加载常量 load constant
                        methodVisitor.visitLdcInsn("方法名: "+name + "  你被代理了，By ASM！");
                        // 在进入方法前，修改class，打印提示
                        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                        super.onMethodEnter();
                    }
                };
            }
        }, ClassReader.EXPAND_FRAMES);

        byte[] bytes = classWriter.toByteArray();

        return (T) new ASMDemo().defineClass(clazz.getName(), bytes, 0, bytes.length).newInstance();
    }
}
