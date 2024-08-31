package com.dmitri.liventsev.wallet.domain.repository;

import java.util.UUID;

public interface TransactionLockableRepository {
    void lock(UUID lockId);
}
