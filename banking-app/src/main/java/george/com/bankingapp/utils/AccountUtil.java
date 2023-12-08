package george.com.bankingapp.utils;

import java.time.Year;

public class AccountUtil {

    public  static final String ACCOUNT_EXIST_CODE = "001";
    public  static final String ACCOUNT_EXIST_MESSAGE = "This User Exist";
    public  static final String ACCOUNT_CREATION_SUCCESS = "002";

    public  static final String ACCOUNT_LOGIN_SUCCESS = "009";

    public  static final String ACCOUNT_DOESNT_EXIST = "003";

    public  static final String ACCOUNT_DOESNT_EXIST_MESSAGE = "This Account Doesn't Exist";

    public  static final String ACCOUNT_Found_CODE = "004";

    public  static final String ACCOUNT_Found_MESSAGE = "Account Found";
    public  static final String ACCOUNT_CREATION_MESSAGE = "Account Successfully Created";

    public  static final String ACCOUNT_CREDITED_CODE = "005";

    public  static final String ACCOUNT_CREDITED_MESSAGE = "Account Successfully Credited";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";

    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient account balance.";

    public  static final String ACCOUNT_DEBITED_CODE = "007";

    public  static final String ACCOUNT_DEBITED_MESSAGE = "Account Successfully Debited";

    public  static final String ACCOUNT_TRANSFERRED_CODE = "008";
    public  static final String ACCOUNT_TRANSFERRED_MESSAGE = "Transfer Successful";

    public  static final String ACCOUNT_UPDATE_SUCCESS = "010";
    public  static final String ACCOUNT_UPDATE_MESSAGE = "Account Successfully Updated";




    public static String generateAccountNumber(){



        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int rand = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String yearInString = String.valueOf(currentYear);
        String randomNumber = String.valueOf(rand);

        StringBuilder accountNumber = new StringBuilder();

       return accountNumber.append(yearInString).append(randomNumber).toString();
    }
}
