package com.ridango.payment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDto {

    private String senderAccountId;

    private String receiverAccountId;

    private String amount;

    private String timestamp;

}
