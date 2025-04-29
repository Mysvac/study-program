package com.mythovac.aop;

import java.io.*;
import java.lang.reflect.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;


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
            // 先检测有没有注解
            if (method.isAnnotationPresent(RemoteCallable.class)) {
                RemoteCallable annotation = method.getAnnotation(RemoteCallable.class);
                // 如果注解标记了需要报告，输出一下报告
                if(annotation.report()) System.out.print("proxy: ");
                if(!annotation.enable()){
                    if(annotation.report()) System.out.println("local");
                    return method.invoke(target, args);
                }
                if(annotation.report()) System.out.println("remote");
                return callRemoteMethod(annotation.url(), annotation.port() , method, args);
            }
            // 没有远程调用注解，直接本地执行
            return method.invoke(target, args);
        }

        private Object callRemoteMethod(String url, int port, Method method, Object[] args) {
            String server_url;
            String jsonData = String.format(
                    "{\"name\":\"%s\",\"type\":\"request\",\"args\":[%s],\"note\":\"\",\"result\":\"\"}",
                    method.getName(),
                    args.length == 0 ? "" : Arrays.stream(args)
                            .map(arg ->{
                                if (arg instanceof Integer) {
                                    return "\"" + ((int)arg) + "\"";
                                }
                                else if (arg instanceof int[]) {
                                    return Arrays.stream((int[]) arg)
                                            .mapToObj(num -> "\"" +num + "\"")
                                            .collect(Collectors.joining(","));
                                }
                                return "\"\"";
                            })
                            .collect(Collectors.joining(","))
            );


            // 创建socket
            try (Socket socket = new Socket(url, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 发送JSON数据
                out.println(jsonData);

                // 返回服务器的响应
                server_url = in.readLine();
            } catch (IOException e) {
                return "Failed to call remote method";
            }
            String[] urls =  server_url.split(":");
            try (Socket socket = new Socket(urls[0], Integer.parseInt(urls[1]));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 发送JSON数据
                out.println(jsonData);

                // 返回服务器的响应
                return in.readLine();
            } catch (IOException e) {
                return "Failed to call remote method";
            }
        }
    }
}