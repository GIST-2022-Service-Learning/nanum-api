package com.nanum.market.utils.emailsender;

import java.util.List;

public interface EmailSender {
    void send(String to, String subject, String content);

}
