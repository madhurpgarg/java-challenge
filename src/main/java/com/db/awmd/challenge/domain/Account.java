package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  AtomicReference<BigDecimal> balance;
//  private BigDecimal balance;

  @NotNull
  private List<Transfer> transactionList;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = new AtomicReference<>(BigDecimal.ZERO);
    this.transactionList = new ArrayList<>();
  }

  public BigDecimal debit(BigDecimal amount) {
    if(this.isEligibleForDebitWithAmount(amount)) {
      throw new NegativeBalanceException("We don't provide overdraft");
    }
    return this.balance.accumulateAndGet(amount, BigDecimal::subtract);
  }

  public BigDecimal credit(BigDecimal amount) {
    return this.balance.accumulateAndGet(amount, BigDecimal::add);
  }

  public boolean transfer(Account toAccount, Transfer transfer) {
    this.debit(transfer.getAmount());
    toAccount.credit(transfer.getAmount());
    return addTransfer(transfer);
  }

  public Boolean addTransfer(Transfer transaction) {
    return transactionList.add(transaction);
  }

  public void setBalance(BigDecimal balance) {
    this.balance.set(balance);
  }

  private boolean isEligibleForDebitWithAmount(BigDecimal amount) {
    return this.balance.get().subtract(amount).compareTo(BigDecimal.ZERO) == -1;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = new AtomicReference<>(balance);
    this.transactionList = new ArrayList<>();
  }
}
