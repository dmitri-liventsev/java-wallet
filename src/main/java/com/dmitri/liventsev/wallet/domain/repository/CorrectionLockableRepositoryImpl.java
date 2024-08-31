package com.dmitri.liventsev.wallet.domain.repository;

import com.dmitri.liventsev.wallet.domain.model.Correction;
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
public class CorrectionLockableRepositoryImpl implements CorrectionLockableRepository {
    private final int maxProcessingTime = 1;
    private final int correctionFrequencyInMinutes = 10;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void lock(UUID lockId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Correction> update = cb.createCriteriaUpdate(Correction.class);
        Root<Correction> root = update.from(Correction.class);

        update.set(root.get("status"), Correction.Status.PROCESSING);
        update.set(root.get("lockedAt"), LocalDateTime.now());
        update.set(root.get("lockId"), lockId);

        Predicate isFrozenCb = cb.and(
                cb.equal(root.get("status"), Correction.Status.PROCESSING),
                cb.lessThan(root.get("lockedAt"), LocalDateTime.now().minusMinutes(maxProcessingTime))
        );

        Predicate isReadyCb = cb.and(
                cb.equal(root.get("status"), "ready"),
                cb.or(
                        cb.isNull(root.get("doneAt")),
                        cb.lessThan(root.get("doneAt"), LocalDateTime.now().minusMinutes(correctionFrequencyInMinutes))
                )
        );

        update.where( cb.or(isFrozenCb,isReadyCb));

        entityManager.createQuery(update).executeUpdate();
    }
}
