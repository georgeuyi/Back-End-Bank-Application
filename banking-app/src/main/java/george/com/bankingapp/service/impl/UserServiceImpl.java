package george.com.bankingapp.service.impl;

import george.com.bankingapp.config.JwtTokenProvider;
import george.com.bankingapp.dto.*;
import george.com.bankingapp.entity.Role;
import george.com.bankingapp.entity.User;
import george.com.bankingapp.repository.UserRepository;
import george.com.bankingapp.utils.AccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_EXIST_CODE)
                    .message(AccountUtil.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = new User().builder()
                .firstName(userRequest.getFirstName())
                .surname(userRequest.getSurname())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtil.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();
        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has been Successfully Created.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getSurname() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);


        return BankResponse.builder()
                .code(AccountUtil.ACCOUNT_CREATION_SUCCESS)
                .message(AccountUtil.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getSurname() + " " + savedUser.getOtherName())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())

                        .build())
                .build();

    }
    @Override
    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        return BankResponse.builder()
                .code(AccountUtil.ACCOUNT_LOGIN_SUCCESS + " Login Successful")
                .message(jwtTokenProvider.generateToken(authentication))
                .build();

    }
    @Override
    public BankResponse updateUserInfo(String accountNumber, UserRequest userRequest) {
        boolean accountExist = userRepository.existsByAccountNumber(accountNumber);
        if (!accountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(accountNumber);
        foundUser.setFirstName(userRequest.getFirstName());
        foundUser.setSurname(userRequest.getSurname());
        foundUser.setEmail(userRequest.getEmail());
        foundUser.setOtherName(userRequest.getOtherName());
        foundUser.setGender(userRequest.getGender());
        foundUser.setAddress(userRequest.getAddress());
        foundUser.setStateOfOrigin(userRequest.getStateOfOrigin());
        foundUser.setPhoneNumber(userRequest.getPhoneNumber());
        foundUser.setAlternativePhoneNumber(userRequest.getAlternativePhoneNumber());
        userRepository.save(foundUser);
        return BankResponse.builder()
                .code(AccountUtil.ACCOUNT_UPDATE_SUCCESS)
                .message(AccountUtil.ACCOUNT_UPDATE_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getSurname() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        Boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .code(AccountUtil.ACCOUNT_EXIST_CODE)
                .message(AccountUtil.ACCOUNT_EXIST_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getSurname() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist) {
            return AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getSurname() + " " + foundUser.getOtherName();

    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        BigDecimal creditAmount = request.getAmount();

        User creditAccount = userRepository.findByAccountNumber(request.getAccountNumber());
        creditAccount.setAccountBalance(creditAccount.getAccountBalance().add(request.getAmount()));
        userRepository.save(creditAccount);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(request.getAccountNumber())
                .transactionAmount(creditAmount)
                .transactionType("CREDIT")
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .amount(creditAmount)
                .code(AccountUtil.ACCOUNT_CREDITED_CODE)
                .message(AccountUtil.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(creditAccount.getFirstName() + " " + creditAccount.getSurname() + " " + creditAccount.getOtherName())
                        .accountNumber(creditAccount.getAccountNumber())
                        .accountBalance(creditAccount.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean accountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!accountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User debitAccount = userRepository.findByAccountNumber(request.getAccountNumber());
        if (debitAccount.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .code(AccountUtil.INSUFFICIENT_BALANCE_CODE)
                    .message(AccountUtil.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        BigDecimal debitAmount = request.getAmount();

        debitAccount.setAccountBalance(debitAccount.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(debitAccount);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(debitAccount.getAccountNumber())
                .transactionAmount(debitAmount)
                .transactionType("DEBIT")
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .amount(debitAmount)
                .code(AccountUtil.ACCOUNT_DEBITED_CODE)
                .message(AccountUtil.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(debitAccount.getFirstName() + " " + debitAccount.getSurname() + " " + debitAccount.getOtherName())
                        .accountNumber(debitAccount.getAccountNumber())
                        .accountBalance(debitAccount.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse transferFund(TransferRequest request) {
        boolean sourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        if (!sourceAccountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        boolean destinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!destinationAccountExist) {
            return BankResponse.builder()
                    .code(AccountUtil.ACCOUNT_DOESNT_EXIST)
                    .message(AccountUtil.ACCOUNT_DOESNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccount = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        User destinationAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        if(sourceAccount.getAccountBalance().compareTo(request.getAmount()) < 0){
            return BankResponse.builder()
                    .code(AccountUtil.INSUFFICIENT_BALANCE_CODE)
                    .message(AccountUtil.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(request.getAmount()));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(request.getAmount()));

        userRepository.save(sourceAccount);
        userRepository.save(destinationAccount);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(sourceAccount.getAccountNumber())
                .transactionAmount(request.getAmount())
                .transactionType("TRANSFER")
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .code(AccountUtil.ACCOUNT_TRANSFERRED_CODE)
                .message(AccountUtil.ACCOUNT_TRANSFERRED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceAccount.getFirstName() + " " + sourceAccount.getSurname() + " " + sourceAccount.getOtherName())
                        .accountNumber(sourceAccount.getAccountNumber())
                        .accountBalance(sourceAccount.getAccountBalance())
                        .build())
                .amount(request.getAmount())
                .build();
    }

}
