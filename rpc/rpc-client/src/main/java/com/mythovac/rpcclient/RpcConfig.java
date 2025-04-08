package com.mythovac.rpcclient;

import com.mythovac.aop.RpcProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RpcConfig {

    @Bean
    public IService clientFunc() {
        return RpcProxy.createProxy(IService.class, new Service());
    }
}