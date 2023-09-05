package com.example.server;

import com.linh.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalance(accountNumber))
                .build();
        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withDraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount(); // 10, 20, 30
        int balance = AccountDatabase.getBalance(accountNumber);

        if (balance < amount){
            Status status = Status.FAILED_PRECONDITION.withDescription("No enough money. You have only " + balance);
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        for (int i = 0 ; i < (amount/10) ; i++){
            Money money = Money.newBuilder()
                    .setValue(10)
                    .build();
            responseObserver.onNext(money);
            AccountDatabase.deductBalance(accountNumber, 10);
        }

    }
}
