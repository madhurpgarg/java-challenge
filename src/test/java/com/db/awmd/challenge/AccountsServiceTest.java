package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void transferMoney() {
    this.accountsService.createAccount(new Account("ID-12345", BigDecimal.valueOf(1000)));
    this.accountsService.createAccount(new Account("ID-98765", BigDecimal.valueOf(1000)));
    this.accountsService.transfer("ID-12345", new Transfer("ID-98765", BigDecimal.valueOf(100)));
    this.accountsService.transfer("ID-12345", new Transfer("ID-98765", BigDecimal.valueOf(100)));

    assertThat(this.accountsService.getAccount("ID-12345").getBalance().get()).isEqualTo(BigDecimal.valueOf(800));
  }

  @Test(expected = NegativeBalanceException.class)
  public void transferMoney_FailsOnInsufficientBalance() {
    this.accountsService.createAccount(new Account("ID-12345", BigDecimal.valueOf(1000)));
    this.accountsService.createAccount(new Account("ID-98765", BigDecimal.valueOf(1000)));
    this.accountsService.transfer("ID-12345", new Transfer("ID-98765", BigDecimal.valueOf(1200)));

  }


  @Test
  public void transferMoneyMultiThreaded() throws InterruptedException {
    this.accountsService.createAccount(new Account("ID-123456", BigDecimal.valueOf(1000)));
    this.accountsService.createAccount(new Account("ID-987656", BigDecimal.valueOf(1000)));

    Callable<Boolean> transferFromID123456To987656 = () -> {
      Thread.sleep(5000);
      return this.accountsService.transfer("ID-123456", new Transfer("ID-987656", BigDecimal.valueOf(100)));
    };

    Callable<Boolean> transferFrom987656To123456 = () -> {
      return this.accountsService.transfer("ID-987656", new Transfer("ID-123456", BigDecimal.valueOf(200)));
    };

    Collection<Callable<Boolean>> callableList = Arrays.asList(
        transferFromID123456To987656,
        transferFromID123456To987656,
        transferFromID123456To987656,
        transferFromID123456To987656,
        transferFrom987656To123456,
        transferFrom987656To123456,
        transferFrom987656To123456
    );

    ExecutorService executor = Executors.newFixedThreadPool(8);

    executor
        .invokeAll(callableList)
        .stream()
        .map(future -> {
          try {
            return future.get();
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        })
        .forEach(System.out::println);

    assertThat(this.accountsService.getAccount("ID-123456").getBalance().get()).isEqualTo(BigDecimal.valueOf(1200));
    assertThat(this.accountsService.getAccount("ID-987656").getBalance().get()).isEqualTo(BigDecimal.valueOf(800));


  }


}
