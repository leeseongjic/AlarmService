package com.service;

import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(
        name = "telegramFeignClient",
        url = "/",
        configuration = {FeignAutoConfiguration.class})
public interface TelegramInterface {

    @GetMapping
    void sendMessage(URI uri);

    @PostMapping
    void sendPostMessage(URI uri, @RequestParam("chat_id") long chat_id, @RequestParam("text") String text);
}
