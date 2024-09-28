package com.trabean.util;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.Account.AccountType;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.domain.UserAccountRelation.UserRole;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.exception.InvalidAccountTypeException;
import com.trabean.exception.UnauthorizedUserRoleException;
import com.trabean.exception.UserAccountRelationNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationUtil {

    /**
     * Account 테이블에서 해당 통장이 존재하는지 체크
     *
     * <li>통장이 존재하면 Account 리턴</li>
     * <li>통장이 존재하지 않으면 AccountNotFoundException(404) 예외 발생</li>
     *
     * @param account
     * @return account
     */
    public static Account validateAccount(Optional<Account> account) {
        return account.orElseThrow(AccountNotFoundException::getInstance);
    }

    /**
     * Account 테이블에서 해당 유저와 통장의 타입이 파라미터와 일치하는지 체크
     *
     * <pre>isMatch == true</pre>
     * <li>통장이 존재하면서 파라미터와 일치하면 Account 리턴</li>
     * <li>통장이 존재하지 않으면 AccountNotFoundException(404) 예외 발생</li>
     * <li>통장이 존재하는데 파라미터와 일치하지 않으면 InvalidAccountTypeException(401) 예외 발생</li>
     *
     * <pre>isMatch == false</pre>
     * <li>통장이 존재하면서 파라미터와 일치하지 않으면 리턴</li>
     * <li>통장이 존재하지 않으면 AccountNotFoundException(404) 예외 발생</li>
     * <li>통장이 존재하는데 파라미터와 일치하면 InvalidAccountTypeException(401) 예외 발생</li>
     *
     * @param expectedAccount
     * @param expectedAccountType
     * @param isMatch
     * @return account
     */
    public static Account validateAccount(Optional<Account> expectedAccount, AccountType expectedAccountType, boolean isMatch) {
        Account account = expectedAccount.orElseThrow(AccountNotFoundException::getInstance);

        if (isMatch != (account.getAccountType() == expectedAccountType)) {
            throw InvalidAccountTypeException.getInstance();
        }

        return account;
    }

    /**
     * UserAccountRelation 테이블에서 해당 유저와 통장의 관계가 존재하는지 체크
     *
     * <li>관계가 존재하면 UserAccountRelation 리턴</li>
     * <li>관계가 존재하지 않으면 UserAccountRelationNotFoundException(404) 예외 발생</li>
     *
     * @param userAccountRelation
     */
    public static UserAccountRelation validateUserAccountRelation(Optional<UserAccountRelation> userAccountRelation) {
        return userAccountRelation.orElseThrow(UserAccountRelationNotFoundException::getInstance);
    }

    /**
     * UserAccountRelation 테이블에서 해당 통장과 관계가 있는 튜플들이 존재하는지 체크
     *
     * <li>관계가 존재하면 UserAccountRelation List 리턴</li>
     * <li>관계가 존재하지 않으면 빈 List 리턴</li>
     *
     * @param userAccountRelationList
     * @return List<UserAccountRelation>
     */
    public static List<UserAccountRelation> validateUserAccountRelationList(Optional<List<UserAccountRelation>> userAccountRelationList) {
        return userAccountRelationList.orElse(new ArrayList<>());
    }

    /**
     * UserAccountRelation 테이블에서 해당 유저와 통장의 관계가 파라미터와 일치하는지 체크
     *
     * <pre>isMatch == true</pre>
     * <li>관계가 존재하면서 파라미터와 일치하면 리턴</li>
     * <li>관계가 존재하지 않으면 UserAccountRelationNotFoundException(404) 예외 발생</li>
     * <li>관계가 존재하는데 파라미터와 일치하지 않으면 UnauthorizedUserRoleException(401) 예외 발생</li>
     *
     * <pre>isMatch == false</pre>
     * <li>관계가 존재하면서 파라미터와 일치하지 않으면 리턴</li>
     * <li>관계가 존재하지 않으면 UserAccountRelationNotFoundException(404) 예외 발생</li>
     * <li>관계가 존재하는데 파라미터와 일치하면 UnauthorizedUserRoleException(401) 예외 발생</li>
     *
     * @param userAccountRelation
     * @param expectedUserRole
     * @param isMatch
     */
    public static void validateUserRole(Optional<UserAccountRelation> userAccountRelation, UserRole expectedUserRole, boolean isMatch) {
        UserRole userRole = userAccountRelation.orElseThrow(UserAccountRelationNotFoundException::getInstance).getUserRole();

        if (isMatch != (userRole == expectedUserRole)) {
            throw UnauthorizedUserRoleException.getInstance();
        }
    }

    //////////



    //////////
    public static Account validateInput(ValidateInputDTO validateInputDTO) {
        Account account = validateInputDTO.getAccount().orElseThrow(AccountNotFoundException::getInstance);
        UserAccountRelation userAccountRelation = validateInputDTO.getUserAccountRelation().orElseThrow(UserAccountRelationNotFoundException::getInstance);

        if(validateInputDTO.getAccountType() != null){
            AccountType accountType = validateInputDTO.getAccountType();

            if(accountType != account.getAccountType()) {
                throw InvalidAccountTypeException.getInstance();
            }
        }

        if(validateInputDTO.getUserRole() != null){
            UserRole userRole = validateInputDTO.getUserRole();

            if(userRole != userAccountRelation.getUserRole()) {
                throw UnauthorizedUserRoleException.getInstance();
            }
        }

        if(validateInputDTO.getIsPayable() != null){

            if(validateInputDTO.getUserRole() == UserRole.NONE_PAYER) {
                throw UnauthorizedUserRoleException.getInstance();
            }
        }

        return account;
    }
    //////////



    //////////

}
