package com.example.client;

import com.linh.models.Money;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class MoneySreamingRespone implements StreamObserver<Money> {

    private CountDownLatch countDownLatch;

    public MoneySreamingRespone(CountDownLatch latch) {
        this.countDownLatch = latch;
    }

    @Override
    public void onNext(Money money) {
        System.out.println(
                "Received async : " + money.getValue()
        );
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is done!!!");
        countDownLatch.countDown();
    }
}
