package george.com.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankResponse {
    private String message;
    private String code;
    private BigDecimal amount;
    private AccountInfo accountInfo;

}
