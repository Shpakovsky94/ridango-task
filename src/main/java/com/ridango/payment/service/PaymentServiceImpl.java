package com.ridango.payment.service;

import com.ridango.payment.config.util.BusinessError;
import com.ridango.payment.config.util.BusinessException;
import com.ridango.payment.config.util.RegexPatterns;
import com.ridango.payment.dao.PaymentRepository;
import com.ridango.payment.dto.PaymentDto;
import com.ridango.payment.entity.Account;
import com.ridango.payment.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;

    AccountService accountService;


    public PaymentServiceImpl(PaymentRepository paymentRepository, AccountService accountService) {
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
    }

    public List<PaymentDto> searchAllProcessedPayments() {
        return paymentRepository.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto processPayment(PaymentDto paymentDto) throws Exception {
        try {
            validateRequest(paymentDto);
            validateBalance(paymentDto);

            Account accountSender = accountService.getAccountById(Long.valueOf(paymentDto.getSenderAccountId()));
            Account receiver = accountService.getAccountById(Long.valueOf(paymentDto.getReceiverAccountId()));

            if (accountSender.getTransactionInProgress()) {
                throw new BusinessException(BusinessError.COLLISION_OF_TRANSACTIONS);
            }

            //Acquire Sender Account Lock
            accountSender.setTransactionInProgress(true);
            accountService.save(accountSender);

            BigDecimal amount = new BigDecimal(paymentDto.getAmount());

            if (amount.compareTo(accountSender.getBalance()) > 0) {
                throw new BusinessException(BusinessError.AMOUNT_INSUFFICIENT);
            }

            return performBalanceUpdate(accountSender, receiver, amount);
        } catch (Exception e) {
            log.error("something is wrong {}", e.getCause(), e);
            throw e;
        }

    }

    private PaymentDto performBalanceUpdate(Account accountSender, Account accountReceiver, BigDecimal amount) {
        BigDecimal newSenderAmount = accountSender.getBalance().subtract(amount);
        BigDecimal newReceiverAmount = accountReceiver.getBalance().add(amount);

        accountSender.setBalance(newSenderAmount);
        accountReceiver.setBalance(newReceiverAmount);

        Payment payment = Payment.builder()
                .sender(accountSender)
                .receiver(accountReceiver)
                .amount(amount)
                .timestamp(LocalDateTime.now()).build();
        payment = paymentRepository.save(payment);

        //Release Sender Account Lock
        accountSender.setTransactionInProgress(false);
        accountService.save(accountSender);

        accountService.save(accountReceiver);

        return entityToDto(payment);
    }


    private void validateBalance(PaymentDto paymentDto) {
        if (!paymentDto.getAmount().matches(RegexPatterns.AMOUNT_PATTERN)) {
            throw new BusinessException(BusinessError.AMOUNT_EMPTY_OR_INVALID_FORMAT);
        }

        BigDecimal amount = new BigDecimal(paymentDto.getAmount());

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(BusinessError.AMOUNT_ZERO);
        }

    }

    private void validateRequest(PaymentDto paymentDto) {
        if (!paymentDto.getSenderAccountId().matches(RegexPatterns.ACCOUNT_PATTERN)) {
            throw new BusinessException(BusinessError.SENDER_EMPTY_OR_INVALID_FORMAT);
        }

        if (!paymentDto.getReceiverAccountId().matches(RegexPatterns.ACCOUNT_PATTERN)) {
            throw new BusinessException(BusinessError.RECEIVER_EMPTY_OR_INVALID_FORMAT);
        }

        if (paymentDto.getSenderAccountId().equals(paymentDto.getReceiverAccountId())) {
            throw new BusinessException(BusinessError.COLLISION_OF_SENDER_AND_RECEIVER);
        }
    }

    private PaymentDto entityToDto(Payment payment) {
        PaymentDto paymentDto = PaymentDto.builder()
                .senderAccountId(String.valueOf(payment.getSender().getId()))
                .receiverAccountId(String.valueOf(payment.getReceiver().getId()))
                .amount(String.valueOf(payment.getAmount()))
                .timestamp(String.valueOf(payment.getTimestamp()))
                .build();
        return paymentDto;
    }
}
