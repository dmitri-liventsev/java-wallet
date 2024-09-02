package com.dmitri.liventsev.wallet.domain.repository;

import com.dmitri.liventsev.wallet.domain.model.Balance;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BalanceRepository extends JpaRepository<Balance, Integer> {
    @Query("SELECT b.amount FROM Balance b WHERE b.id = 1")
    long getCurrentBalance();

    @Modifying
    @Transactional
    @Query("UPDATE Balance b SET b.amount = :newBalance WHERE b.id = 1")
    void setBalance(long newBalance);

    boolean existsById(int id);
}