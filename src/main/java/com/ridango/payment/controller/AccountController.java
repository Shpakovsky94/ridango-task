package com.ridango.payment.controller;

import com.ridango.payment.dto.AccountDto;
import com.ridango.payment.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/searchAll")
    public List<AccountDto> searchAllAccounts(){
        return accountService.searchAllAccounts();
    }
}
