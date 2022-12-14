package com.ridango.payment.config.util;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BusinessError {

    ACCOUNT_NOT_FOUND("AC-001", "account does not exist"),
    SENDER_EMPTY_OR_INVALID_FORMAT("SE-001", "sender account is not found in request message or has invalid format"),
    RECEIVER_EMPTY_OR_INVALID_FORMAT("RE-001", "receiver account is not found in request message or has invalid format"),
    AMOUNT_EMPTY_OR_INVALID_FORMAT("AM-001", "amount for transaction is not found in request message or has invalid format"),
    AMOUNT_ZERO("AM-002", "amount for transaction equals to zero"),
    AMOUNT_INSUFFICIENT("AM-003", "amount for transaction exceeds current sender's balance"),
    COLLISION_OF_SENDER_AND_RECEIVER("CO-001", "sender and receiver are the same"),
    COLLISION_OF_TRANSACTIONS("CO-002", "another transaction is in progress");

    private String errorCode;
    private String errorMessage;
}
