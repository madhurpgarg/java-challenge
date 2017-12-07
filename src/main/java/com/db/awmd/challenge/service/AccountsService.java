package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountNotFound;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService emailNotificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = emailNotificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public Boolean transfer(String fromAccountId, Transfer transfer) {

    Account fromAccount = getAccount(fromAccountId);
    if (fromAccount == null) {
      throw new AccountNotFound(fromAccountId + " account number not available");
    }

    Account toAccount = getAccount(transfer.getToAccountId());
    if (toAccount == null) {
      throw new AccountNotFound(transfer.getToAccountId() + " account number not available");
    }
    fromAccount.debit(transfer.getAmount());
    toAccount.credit(transfer.getAmount());
    return fromAccount.addTransfer(transfer);
  }
}