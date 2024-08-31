package com.dmitri.liventsev.wallet.domain.repository;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class TransactionLockableRepositoryImpl implements TransactionLockableRepository {
    private final int maxProcessingTime = 1;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void lock(UUID lockId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Transaction> update = cb.createCriteriaUpdate(Transaction.class);
        Root<Transaction> root = update.from(Transaction.class);

        update.set(root.get("status"), Transaction.Status.PROCESSING);
        update.set(root.get("lockedAt"), LocalDateTime.now());
        update.set(root.get("lockId"), lockId);

        Predicate isNew = cb.equal(root.get("status"), Transaction.Status.NEW);
        Predicate isFrozen = cb.and(
                cb.equal(root.get("status"), Transaction.Status.PROCESSING),
                cb.lessThan(root.get("lockedAt"), LocalDateTime.now().minusMinutes(maxProcessingTime))
        );
        update.where(cb.or(isNew, isFrozen));

        entityManager.createQuery(update).executeUpdate();
    }
}
