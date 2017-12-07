package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Transfer {

  @NotNull
  String id;

  @NotNull
  String toAccountId;

  @NotNull
  @Min(value = 0, message = "Amount to transfer should always be a positive")
  BigDecimal amount;


  @JsonCreator
  public Transfer(
                     @JsonProperty("toAccountId") String toAccountId,
                     @JsonProperty("amount") BigDecimal amount) {
    this.id = UUID.randomUUID().toString();
    this.toAccountId = toAccountId;
    this.amount = amount;
  }

}
