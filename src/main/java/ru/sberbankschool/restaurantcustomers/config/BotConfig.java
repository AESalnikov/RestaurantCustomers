package ru.sberbankschool.restaurantcustomers.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Component
@Getter
public class BotConfig {
    @Value("${bot.url}")
    private String webHookPath;
    @Value("${bot.name}")
    private String userName;
    @Value("${bot.token}")
    private String botToken;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(getWebHookPath()).build();
    }
}
