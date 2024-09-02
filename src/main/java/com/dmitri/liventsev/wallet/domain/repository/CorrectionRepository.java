package com.dmitri.liventsev.wallet.domain.repository;

import com.dmitri.liventsev.wallet.domain.model.Correction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CorrectionRepository extends JpaRepository<Correction, Long>, CorrectionLockableRepository {
    Optional<Correction> findByLockId(UUID lockId);

    boolean existsById(int id);
}
