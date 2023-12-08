package george.com.bankingapp.service.impl;

import george.com.bankingapp.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
