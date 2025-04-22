package com.opthema.hcm.java_microservice.config;

import com.opthema.hcm.grpc.user.UserServiceGrpc;
import com.opthema.hcm.java_microservice.application.service.JwtService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.opthema.hcm.java_microservice.grpc.security.JwtGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.interceptors.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
public class GrpcSecurityConfig {

    @Value("${grpc.client.user-service.address}")
    private String grpcUserServiceAddress;
    @Autowired
    private JwtService jwtService;
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BearerAuthenticationReader(token -> {
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                return new UsernamePasswordAuthenticationToken(username, null, null);
            }
            return null;
        });
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcUserServiceAddress)
                .usePlaintext()
                .build();
        return UserServiceGrpc.newBlockingStub(channel);
    }
}


