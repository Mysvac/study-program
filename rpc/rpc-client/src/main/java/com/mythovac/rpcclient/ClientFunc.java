package com.mythovac.rpcclient;

import com.mythovac.aop.RemoteCallable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientFunc implements IClientFunc {
    @Override
    @RemoteCallable("1")
    public String fib(int n){
        if (n < 0) {
            return "请输入非负整数";
        }
        int result = calculateFibonacci(n);
        return String.valueOf(result);
    }
    @Override
    @RemoteCallable("1")
    public String sort(int[] array){
        if (array == null || array.length == 0) {
            return "请输入数组";
        }
        Arrays.sort(array);
        return Arrays.stream(array)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", "));
    }
    // 辅助方法：计算斐波那契数列
    private int calculateFibonacci(int n) {
        return n <= 1 ? n : calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }
}
