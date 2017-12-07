package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transfer;

public interface TransactionsRepository {

  void createTransaction(Transfer transaction);
}
