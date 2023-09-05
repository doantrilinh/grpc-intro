package com.example.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.DescriptorProtos;
import com.linh.models.Balance;
import com.linh.models.BalanceCheckRequest;
import com.linh.models.BankServiceGrpc;
import com.linh.models.WithdrawRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;
    @BeforeAll
    public void setup(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",6565)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void balanceTest(){
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(2)
                .build();
        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
        System.out.println("received: " + balance.getAmount());
    }

    @Test
    public void withDrawTest(){
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(10).setAmount(40).build();
        this.blockingStub.withDraw( withdrawRequest).forEachRemaining(
                money -> System.out.println("received: " + money.getValue())
        );
    }

    @Test
    public void withDrawAsyncTest(){

        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(8).setAmount(40).build();
        this.bankServiceStub.withDraw(withdrawRequest, new MoneySreamingRespone(latch));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
