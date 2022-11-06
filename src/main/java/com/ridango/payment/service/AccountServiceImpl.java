package com.ridango.payment.service;

import com.ridango.payment.config.util.BusinessError;
import com.ridango.payment.config.util.BusinessException;
import com.ridango.payment.dao.AccountRepository;
import com.ridango.payment.dto.AccountDto;
import com.ridango.payment.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccountById(Long accountId) {

        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isEmpty()) {
            throw new BusinessException(BusinessError.ACCOUNT_NOT_FOUND);
        }
        return account.orElse(null);
    }

    public List<AccountDto> searchAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }

    private AccountDto entityToDto(Account account) {
        return AccountDto.builder()
                .id(String.valueOf(account.getId()))
                .name(account.getName())
                .balance(String.valueOf(account.getBalance()))
                .transactionInProgress(String.valueOf(account.getTransactionInProgress())).build();
    }
}