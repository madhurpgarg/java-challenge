package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountNotFound;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
      this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

  @PostMapping(path = "/{accountId}/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> transfer(@PathVariable String accountId, @RequestBody @Valid Transfer transfer) {
    log.info(
        "Transferring amount {} from account '{}' to account '{}'",
        transfer.getAmount(), accountId,
        transfer.getToAccountId());

    try {
      if (this.accountsService.transfer(accountId, transfer)) {
        log.info(
            "Transfer of amount {} from account '{}' to account '{}' successful",
            transfer.getAmount(),
            accountId, transfer.getToAccountId());

        return new ResponseEntity<>(transfer, HttpStatus.ACCEPTED);
      }
    } catch (NegativeBalanceException | AccountNotFound e) {
      log.error(
          "Transfer of amount {} from account '{}' to account '{}' failed",
          transfer.getAmount(), accountId,
          transfer.getToAccountId());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    log.error(
        "Transfer of amount {} from account '{}' to account '{}' failed",
        transfer.getAmount(),
        accountId, transfer.getToAccountId());
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }


}
