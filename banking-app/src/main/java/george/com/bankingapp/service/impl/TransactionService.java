package george.com.bankingapp.service.impl;

import george.com.bankingapp.dto.TransactionDto;
import george.com.bankingapp.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
