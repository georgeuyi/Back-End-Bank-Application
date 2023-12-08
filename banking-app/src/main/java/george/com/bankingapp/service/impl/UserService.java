package george.com.bankingapp.service.impl;

import george.com.bankingapp.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transferFund(TransferRequest request);
    BankResponse login(LoginDto loginDto);

     BankResponse updateUserInfo(String accountNumber, UserRequest userRequest);
}
