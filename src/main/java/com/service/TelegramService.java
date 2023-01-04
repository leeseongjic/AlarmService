package com.service;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final TelegramInterface telegramInterface;

    @Value("${test.chatId}")
    private long testChatId;

    @Value("${test.telegram.url}")
    private String testTelegramUrl;

    public void sendHealthCheck(String[] row) {
        String sendUrl = "";
        long chatId = 0L;
        StringBuffer completeMessage = new StringBuffer();

        if (row[2].equals("test")) {
            sendUrl = testTelegramUrl;
            chatId = testChatId;
        }
//        else if (row[2].equals("crmError")) {
//            sendUrl = crmError;
//            chatId = crmErrorChatId;
//        }
//        completeMessage.append("[헬쓰체크] ::: ");
//        completeMessage.append(row[1]);
//        completeMessage.append("\n30초 연속 미응답");

        try {
            URI uri = new URI(sendUrl);
            telegramInterface.sendPostMessage(uri, chatId, completeMessage.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendTestAlarm(String message) {
        try {
            URI uri = new URI(testTelegramUrl);
            telegramInterface.sendPostMessage(uri
                    ,testChatId
                    ,message);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}