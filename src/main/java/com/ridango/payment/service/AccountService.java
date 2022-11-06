package com.ridango.payment.service;

import com.ridango.payment.dto.AccountDto;
import com.ridango.payment.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {
    Account getAccountById( Long accountId);

    List<AccountDto> searchAllAccounts();

    void save(Account account);
}

