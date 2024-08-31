package com.dmitri.liventsev.wallet.domain.repository;

import java.util.UUID;

public interface CorrectionLockableRepository {
    void lock(UUID lockId);
}