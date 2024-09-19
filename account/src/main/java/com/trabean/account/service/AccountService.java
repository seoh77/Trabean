package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final UserAccountRelationRepository userAccountRelationRepository;

    public String getAccountNoById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);

        return account.map(Account::getAccountNo).orElse(null);
    }

    public String getUserRoleByUserIdAndAccountId(Long userId, Long accountId) {
        Optional<UserAccountRelation> userAccountRelation = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId);

        return userAccountRelation
                .map(relation -> relation.getUserRole().name())
                .orElse(null);
    }
}
