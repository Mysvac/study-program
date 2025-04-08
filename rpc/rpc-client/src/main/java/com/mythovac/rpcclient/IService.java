package com.mythovac.rpcclient;

import com.mythovac.aop.RemoteCallable;

public interface IService {

    @RemoteCallable(url = "10.100.164.32", port = 18083, report = true)
    String fib(int n);

    @RemoteCallable(url = "10.100.164.32", port = 18083, enable = false)
    String sort(int[] array);
}
