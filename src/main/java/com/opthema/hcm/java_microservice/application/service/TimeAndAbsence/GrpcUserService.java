package com.opthema.hcm.java_microservice.application.service.TimeAndAbsence;

import com.opthema.hcm.grpc.user.UserOuterClass.UserRequest;
import com.opthema.hcm.grpc.user.UserOuterClass.UserResponse;
import com.opthema.hcm.grpc.user.UserOuterClass.User;
import com.opthema.hcm.grpc.user.UserServiceGrpc;
import com.opthema.hcm.java_microservice.domain.model.AppUser;
import com.opthema.hcm.java_microservice.infrastructure.repository.AppUserRepository;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;


@GrpcService
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    // gRPC istemcisi için kanal
    private ManagedChannel channel;
    @Autowired
    private  AppUserRepository appUserRepository;


//    @PostConstruct
//    public void init() {
//        // gRPC kanalını başlat
//        channel = ManagedChannelBuilder.forTarget(grpcServerAddress)
//                .usePlaintext()
//                .idleTimeout(5, TimeUnit.MINUTES) // Bağlantı zaman aşımını 5 dakikaya ayarlıyoruz
//                .build();
//
//        // UserService stub'ını başlat
//        userStub = UserServiceGrpc.newBlockingStub(channel);
//    }

    @PreDestroy
    public void shutdown() {
        // Uygulama kapanırken kanalın düzgün şekilde kapanmasını sağla
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    @Override
    public void getUsers(UserRequest request, StreamObserver<UserResponse> responseObserver){
        List<Long> userIds = request.getIdsList();

        List<AppUser> users = appUserRepository.findAllById(userIds);

        List<User> grpcUsers = users.stream()
                .map(user -> User.newBuilder()
                        .setId(user.getId())
                        .setName(user.getUsername())
                        .setEmail(user.getEmail())
                        .build())
                .collect(Collectors.toList());

        UserResponse response = UserResponse.newBuilder()
                .addAllUsers(grpcUsers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
