package com.buildledger.enums;

public enum ContractStatus {

    DRAFT,
    ACTIVE,
    COMPLETED,
    TERMINATED,
    EXPIRED;

    public boolean canTransitionTo(ContractStatus next) {
        return switch (this) {
            case DRAFT       -> next == ACTIVE;
            case ACTIVE      -> next == COMPLETED || next == TERMINATED;
            case COMPLETED   -> false; // terminal
            case TERMINATED  -> false; // terminal
            case EXPIRED     -> false; // terminal
        };
    }
}