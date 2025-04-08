package com.mythovac.rpcclient;

import com.mythovac.aop.RpcProxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RpcConfig {

    @Bean
    public IClientFunc clientFunc() {
        return RpcProxy.createProxy(IClientFunc.class, new ClientFunc());
    }
}