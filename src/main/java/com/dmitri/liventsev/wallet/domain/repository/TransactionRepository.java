package com.dmitri.liventsev.wallet.domain.repository;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionLockableRepository {
    @Query("SELECT t FROM Transaction t WHERE t.source != 'CORRECTION' AND MOD(t.id, 2) <> 0 AND t.status = 'DONE'")
    List<Transaction> findOddTransactions(int limit);

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PROCESSING' ORDER BY t.createdAt ASC")
    List<Transaction> findProcessingTransactionsSortedByCreatedAt();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'DONE'")
    long calculateBalance();
}