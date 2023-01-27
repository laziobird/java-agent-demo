# java-agent-demo
Tell you how to write a common java agent  and learn some core  implements.

### 一个完整的Java Agent探针实现过程

#### 目标

>实现一个简单性能工具，通过探针统计Java程序所有方法的执行时间
1、构建 Maven 项目工程，添加 MANIFEST.MF , 目录大致

图-11

在 `MANIFEST.MF`文件中定义`Premain-Class`属性，指定一个实现类。类中实现了Premain方法，这就是Java Agent 在类加载启动入口

```xml
Manifest-Version: 1.0
Premain-Class: com.laziobird.MyAgentDemo
Agent-Class: com.laziobird.MyAgentDemo
Can-Redefine-Classes: true
Can-Retransform-Classes: true
```
* `Premain-Class`包含Premain方法的类
* `Can-Redefine-Classes`为true时表示能够重新定义Class
* `Can-Retransform-Classes`为true时表示能够重新转换Class，实现字节码替换
2、构建Premain方法

```xml
public class MyAgentDemo {
    // JVM 启动时，Agent修改字节码
    public static void premain(String args, Instrumentation inst) {
        System.out.println(" premain agent loaded !");
        inst.addTransformer(new PreMainTransformerDemo());
        System.out.println(" agent addTransformer start !");
    }
 ....   
}
```
我们实现Premain方法类叫 `MyAgentDemo`，里面添加一个类转化器  `PreMainTransformerDemo`，这个转化器具体来实现统计方法调用时间
3、编写类转换器

在编写类转化器时，我们通过Javassist 来具体操作字节码，首先`pom.xml` 里面添加依赖

```xml
<dependency>
   <groupId>org.javassist</groupId>
   <artifactId>javassist</artifactId>
   <version>3.25.0-GA</version>
</dependency>
```
接下来具体实现
```java
public class PreMainTransformerDemo implements ClassFileTransformer{
   final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
   final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";
   @Override
   public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                           ProtectionDomain protectionDomain, byte[] classfileBuffer){
       // className 默认格式 com/laziobird 替换 com.laziobird
       className = className.replace("/", ".");
       //java自带的方法不进行处理,不是特别类的方法也不处理
       if(className.startsWith("java") || className.startsWith("sun")|| !className.contains("com.laziobird")){
           return null;
       }
       CtClass ctclass = null;
       try {
           // 使用全称,用于取得字节码类<使用javassist>
           ctclass = ClassPool.getDefault().get(className);
           for(CtMethod ctMethod : ctclass.getDeclaredMethods()){
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
```
这段程序等价于：把指定Java类下所有方法进行了如下转换，重新生成字节码加载执行
图-12

4、打包生成Java Agent的Jar 包

在`pom.xml`配置好`maven assembly`，进行编译打包

图-13

5、写一个Java测试程序，验证探针是否生效

类`AgentTest` 有两个简单方法`test`、`testB`

为了演示，其中testB 调用了另外一个类`ClassC` 的 `methodD`方法。

可以看到，类包名是 `com.laziobird`，刚才的Agent 只会对`com.laziobird` 的类起作用

```java
package com.laziobird;
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
package com.laziobird;
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
```
我们给测试程序打成可执行的Jar包，Jar 指定默认运行的类是 `AgentTest`  
图-14

运行测试程序，通过`-javaagent`启动我们写的 Java Agent 探针

```xml
java -javaagent:/path/agentdemo/target/javaagent-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar  
-jar  /path/gitproject/TestAgentDemo/out/artifacts/TestAgentDemo_jar/TestAgentDemo.jar
```
#### 运行效果

图-15

