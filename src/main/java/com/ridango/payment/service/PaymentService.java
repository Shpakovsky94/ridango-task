package com.ridango.payment.service;

import com.ridango.payment.dto.PaymentDto;

import java.util.List;

public interface PaymentService {

    List<PaymentDto> searchAllProcessedPayments();

    PaymentDto processPayment(PaymentDto paymentDto) throws Exception;
}
