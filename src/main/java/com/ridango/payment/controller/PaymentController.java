package com.ridango.payment.controller;

import com.ridango.payment.dto.PaymentDto;
import com.ridango.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/process")
    public PaymentDto processPayment(@RequestBody PaymentDto paymentDto) throws Exception {
        return paymentService.processPayment(paymentDto);
    }

    @GetMapping("/searchAllProcessedPayments")
    public List<PaymentDto> searchAllProcessedPayments() {
        return paymentService.searchAllProcessedPayments();
    }
}
