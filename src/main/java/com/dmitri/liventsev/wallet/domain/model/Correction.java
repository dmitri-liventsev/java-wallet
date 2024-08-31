package com.dmitri.liventsev.wallet.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "corrections")
public class Correction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "lock_id")
    private UUID lockId;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "done_at")
    private LocalDateTime doneAt;

    public Correction() {}

    public Correction(UUID lockId, LocalDateTime lockedAt, Status status, LocalDateTime doneAt) {
        this.lockId = lockId;
        this.lockedAt = lockedAt;
        this.status = status;
        this.doneAt = doneAt;
    }

    public int getId() {
        return id;
    }

    public UUID getLockId() {
        return lockId;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLockId(UUID lockId) {
        this.lockId = lockId;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    public void markAsDone() {
        setDoneAt(LocalDateTime.now());
        setStatus(Status.READY);
    }

    public enum Status {
        PROCESSING,
        READY
    }
}
