//package com.opthema.hcm.java_microservice.config;
//
//import com.opthema.hcm.java_microservice.application.service.JwtService;
//import io.grpc.Metadata;
//import io.grpc.ServerCall;
//import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//
//import java.util.Optional;
//
//public class JwtGrpcAuthenticationReader implements GrpcAuthenticationReader {
//    @Autowired
//
//    private JwtService jwtService;
//
//    @Override
//    public Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers) {
//        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
//        String authHeader = headers.get(authKey);
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if (jwtService.validateToken(token)) {
//                String username = jwtService.extractUsername(token);
//                return new UsernamePasswordAuthenticationToken(username, null, null);
//            }
//        }
//        return null;
//    }
//}
