package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    public String getAccountNoById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.map(Account::getAccountNo).orElse(null);
    }
}
