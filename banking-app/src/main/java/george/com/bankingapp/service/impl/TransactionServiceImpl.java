package george.com.bankingapp.service.impl;

import george.com.bankingapp.dto.TransactionDto;
import george.com.bankingapp.entity.Transaction;
import george.com.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;


    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .transactionAmount(transactionDto.getTransactionAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");
    }
}
