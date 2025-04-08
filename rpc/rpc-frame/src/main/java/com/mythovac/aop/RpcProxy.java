package com.mythovac.aop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.Socket;

public class RpcProxy {
    public static <T> T createProxy(Class<T> targetClass, T targetInstance) {
        return targetClass.cast(
                Proxy.newProxyInstance(
                    targetClass.getClassLoader(),
                    new Class<?>[]{targetClass},
                    new RpcInvocationHandler(targetInstance)
                )
        );
    }

    private static class RpcInvocationHandler implements InvocationHandler {
        private final Object target;

        public RpcInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.print("proxy: ");
            if (method.isAnnotationPresent(RemoteCallable.class)) {
                System.out.println("remote");
                // 获取注解的value值
                RemoteCallable annotation = method.getAnnotation(RemoteCallable.class);
                String remoteName = annotation.value().isEmpty() ? method.getName() : annotation.value();
                // 如果方法被 @RemoteCallable 注解标记，执行远程调用逻辑
                return callRemoteMethod(remoteName, method, args);
            } else {
                System.out.println("local");
                // 否则直接调用目标方法
                return method.invoke(target, args);
            }
        }

        private Object callRemoteMethod(String remoteName, Method method, Object[] args) {
            // 构造JSON数据
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"methodName\":\"").append(method.getName()).append("\",");
            jsonBuilder.append("\"type\":\"").append("need").append("\",");
            jsonBuilder.append("\"args\":[");
            // 将参数转换为字符串并添加到JSON中
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\"").append(String.valueOf(args[i])).append("\"");
            }
            jsonBuilder.append("]");
            jsonBuilder.append("}");
            // 这里可以实现远程调用逻辑
            String jsonData = jsonBuilder.toString();
            // 远程服务器的IP地址和端口

            int serverPort = 12345; // 替换为实际的服务器端口

            try (Socket socket = new Socket(remoteName, serverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 发送JSON数据
                out.println(jsonData);

                // 接收返回值
                String response = in.readLine();
                System.out.println("Received response: " + response);
                // 返回服务器的响应
                return response;

            } catch (IOException e) {
                e.printStackTrace();
                return " { \"result\": \"Failed to call remote method\" } ";
            }

        }
    }
}