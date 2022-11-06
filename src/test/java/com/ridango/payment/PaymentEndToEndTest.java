package com.ridango.payment;

import com.ridango.payment.config.util.BusinessException;
import com.ridango.payment.dao.PaymentRepository;
import com.ridango.payment.dto.PaymentDto;
import com.ridango.payment.entity.Account;
import com.ridango.payment.entity.Payment;
import com.ridango.payment.service.AccountService;
import com.ridango.payment.service.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
public class PaymentEndToEndTest {

    @Spy
    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    AccountService accountService;

    @Mock
    PaymentRepository paymentRepository;

    @Test
    void testSenderAndReceiverAreSame() {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("1").amount("100.00").build();
        assertThatThrownBy(() -> paymentService.processPayment(paymentDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void testReceiverIsNotFound() {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("2").amount("100.00").build();

        when(accountService.getAccountById(1L)).thenThrow(new BusinessException("not found"));
        assertThatThrownBy(() -> paymentService.processPayment(paymentDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void testAmountInvalid() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("2").amount("100.00").build();
        Account receiver = Account.builder().id(1L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        Account sender = Account.builder().id(2L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        when(accountService.getAccountById(1L)).thenReturn(receiver);
        when(accountService.getAccountById(2L)).thenReturn(sender);
        when(paymentRepository.save(Mockito.any())).thenReturn(Payment.builder().receiver(receiver).sender(sender).amount(new BigDecimal("10.00")).build());
        assertThatThrownBy(() -> paymentService.processPayment(paymentDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void testAmountZero() {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("2").amount("00.00").build();
        Account receiver = Account.builder().id(1L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        Account sender = Account.builder().id(2L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        when(accountService.getAccountById(1L)).thenReturn(receiver);
        when(accountService.getAccountById(2L)).thenReturn(sender);
        when(paymentRepository.save(Mockito.any())).thenReturn(Payment.builder().receiver(receiver).sender(sender).amount(new BigDecimal("10.00")).build());
        assertThatThrownBy(() -> paymentService.processPayment(paymentDto))
                .isInstanceOf(BusinessException.class);
    }


    @Test()
    void testAccountIsLockedByAnotherTransaction() {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("2").amount("10.00").build();
        Account receiver = Account.builder().id(1L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(true).build();

        Account sender = Account.builder().id(2L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        when(accountService.getAccountById(1L)).thenReturn(receiver);
        when(accountService.getAccountById(2L)).thenReturn(sender);
        when(paymentRepository.save(Mockito.any())).thenReturn(Payment.builder().receiver(receiver).sender(sender).amount(new BigDecimal("10.00")).build());
        assertThatThrownBy(() -> paymentService.processPayment(paymentDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void testOK() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder().senderAccountId("1").receiverAccountId("2").amount("10.00").build();
        Account receiver = Account.builder().id(1L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        Account sender = Account.builder().id(2L)
                .balance(new BigDecimal("50.00"))
                .transactionInProgress(false).build();

        when(accountService.getAccountById(1L)).thenReturn(receiver);
        when(accountService.getAccountById(2L)).thenReturn(sender);
        when(paymentRepository.save(Mockito.any())).thenReturn(Payment.builder().receiver(receiver).sender(sender).amount(new BigDecimal("10.00")).build());
        assertNotNull(paymentService.processPayment(paymentDto));
    }
}