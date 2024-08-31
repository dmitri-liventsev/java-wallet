package com.dmitri.liventsev.wallet.application.service;

import com.dmitri.liventsev.wallet.domain.model.Correction;
import com.dmitri.liventsev.wallet.domain.model.Balance;
import com.dmitri.liventsev.wallet.domain.repository.CorrectionRepository;
import com.dmitri.liventsev.wallet.domain.repository.BalanceRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class DataInitializationService {

    private final CorrectionRepository correctionRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    Environment env;

    @Autowired
    public DataInitializationService(CorrectionRepository correctionRepository, BalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.correctionRepository = correctionRepository;
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        lockTables();

        if (!correctionRepository.existsById(1)) {
            Correction correction = new Correction();
            correction.setId(1);
            correction.setLockId(UUID.randomUUID());
            correction.setLockedAt(LocalDateTime.now());
            correction.setStatus(Correction.Status.READY);
            correction.setDoneAt(null);
            correctionRepository.save(correction);
        }

        if (!balanceRepository.existsById(1)) {
            Balance balance = new Balance();
            balance.setId(1);
            balance.setAmount(transactionRepository.calculateBalance());
            balanceRepository.save(balance);
        }
    }

    private void lockTables() {
        String profile = env.getActiveProfiles()[0];

        if (Objects.equals(profile, "test")) {
            return;
        }

        correctionRepository.lockTable();
        balanceRepository.lockTable();
    }
}
