package ru.sberbankschool.restaurantcustomers.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {
    @Value("${bot.url}")
    private String webHookPath;
    @Value("${bot.name}")
    private String userName;
    @Value("${bot.token}")
    private String botToken;
}
