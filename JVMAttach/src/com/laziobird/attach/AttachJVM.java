package com.laziobird.attach;
import com.sun.tools.attach.*;
import java.io.IOException;
import java.util.List;
/**
 * @ClassName: AttachJVM
 * @Description: Attach 测试Demo
 * @Author: 蒋志伟
 * @date: 2023/1/26 9:00 PM
 * @Version: V1.0
 */
public class AttachJVM {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException {
        // 获取运行中的JVM列表
        List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
        // 我们编写探针的Jar包路径
        String agentJar = "/Users/jiangzhiwei/eclipse-workspace/agentdemo/target/javaagent-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
        for (VirtualMachineDescriptor vmd : vmList) {
            // 找到测试的JVM
            System.out.println("vmd name: "+vmd.displayName());

            Thread.sleep(2000);

            if (vmd.displayName().endsWith("AgentAttachTest")) {
                // attach到目标ID的JVM上
                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                // agent指定jar包到已经attach的JVM上
                virtualMachine.loadAgent(agentJar);
                virtualMachine.detach();
            }
        }
    }
}