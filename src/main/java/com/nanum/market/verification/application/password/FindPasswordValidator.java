package com.nanum.market.verification.application.password;

public interface FindPasswordValidator {
    void checkIsVerified(String username, String verificationCode);
}
