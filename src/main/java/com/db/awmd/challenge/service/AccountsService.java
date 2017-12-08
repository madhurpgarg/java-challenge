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
    Account account = this.accountsRepository.getAccount(accountId);
    if (account == null) {
      throw new AccountNotFound(accountId + " account number not available");
    }
    return account;
  }

  public Boolean transfer(String fromAccountId, Transfer transfer) {

    Account fromAccount = getAccount(fromAccountId);
    Account toAccount = getAccount(transfer.getToAccountId());

    boolean transferSuccessful =  fromAccount.transfer(toAccount, transfer);

    notificationService.notifyAboutTransfer(fromAccount, "Amount: " +transfer.getAmount() + " has been transferred to " + toAccount.getAccountId());
    notificationService.notifyAboutTransfer(toAccount, "Amount: " +transfer.getAmount() + " has been received from " + fromAccount.getAccountId());

    return transferSuccessful;
  }
}
