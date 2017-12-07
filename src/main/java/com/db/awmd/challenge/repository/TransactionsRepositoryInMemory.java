package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transfer;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionsRepositoryInMemory implements TransactionsRepository {

  private final Map<String, Transfer> transactions = new ConcurrentHashMap<>();

  @Override
  public void createTransaction(Transfer transaction) {
    transactions.put(transaction.getId(), transaction);
  }


}
