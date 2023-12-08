package george.com.bankingapp.repository;

import george.com.bankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String>{
}
