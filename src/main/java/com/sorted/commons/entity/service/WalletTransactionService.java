package com.sorted.commons.entity.service;

import com.sorted.commons.entity.mongo.WalletTransactionEntity;
import com.sorted.commons.enums.TxnType;
import com.sorted.commons.enums.WalletTxnSource;
import com.sorted.commons.repository.mongo.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository repository;

    public WalletTransactionEntity createTransaction(String walletId, long amount, long newBalance, TxnType type, WalletTxnSource source) {
        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .walletId(walletId)
                .amount(amount)
                .balance(newBalance)
                .type(type)
                .source(source)
                .build();
        return repository.save(transaction);
    }
}
