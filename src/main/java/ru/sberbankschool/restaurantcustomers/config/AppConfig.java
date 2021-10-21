package ru.sberbankschool.restaurantcustomers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.sberbankschool.restaurantcustomers.model.Bot;
import ru.sberbankschool.restaurantcustomers.model.TelegramFacade;

@Configuration
public class AppConfig {
    private final BotConfig botConfig;

    public AppConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        Bot bot = new Bot(telegramFacade, setWebhook);
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotPath(botConfig.getWebHookPath());
        return bot;
    }
}

