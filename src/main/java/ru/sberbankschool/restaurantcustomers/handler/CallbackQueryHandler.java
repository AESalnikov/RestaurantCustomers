package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Rating;
import ru.sberbankschool.restaurantcustomers.entity.Tips;
import ru.sberbankschool.restaurantcustomers.utilites.KeyboardUtils;
import ru.sberbankschool.restaurantcustomers.service.DatabaseServiceImpl;
import ru.sberbankschool.restaurantcustomers.constants.Step;

import static ru.sberbankschool.restaurantcustomers.telegram.TelegramFacade.step;

@Component
public class CallbackQueryHandler {

    private final DatabaseServiceImpl dbService;

    public CallbackQueryHandler(DatabaseServiceImpl dbService) {
        this.dbService = dbService;
    }

    public SendMessage handler(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        switch (data) {
            case "Оценить": {
                if (step == Step.START) {
                    step = Step.RATING_STEP;
                    return mainEvent(callbackQuery);
                }
                break;
            }
            case "1":
            case "2":
            case "3":
            case "4":
            case "5": {
                if (step == Step.RATING_STEP) {
                    step = Step.TIPS_STEP;
                    return ratingEvent(callbackQuery, data);
                }
                break;
            }
            case "True":
            case "False": {
                if (step == Step.TIPS_STEP) {
                    step = Step.START;
                    return tipsEvent(callbackQuery, data);
                }
            }
            break;
        }
        return null;
    }

    private SendMessage tipsMessage(String chatId, Customer customer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(customer + "\n\nКлиент оставил чаевые?");
        sendMessage.setReplyMarkup(KeyboardUtils.tipsKeyBoard());
        return sendMessage;
    }

    private long getPhone(String customerInfo) {
        return Long.parseLong(customerInfo
                .split("Телефонный номер")[1]
                .split(" ")[1]
                .split("\n")[0]);
    }

    private SendMessage mainEvent(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String customerInfo = callbackQuery.getMessage().getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(customerInfo);
        sendMessage.setReplyMarkup(KeyboardUtils.ratingKeyBoard());
        return sendMessage;
    }

    private SendMessage ratingEvent(CallbackQuery callbackQuery, String mark) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String customerInfo = callbackQuery.getMessage().getText();
        Customer customer = dbService.getCustomerByPhoneNumber(getPhone(customerInfo));
        Rating rating = new Rating();
        rating.setCustomer(customer);
        rating.setMark(Integer.parseInt(mark));
        dbService.saveMark(rating);
        return tipsMessage(chatId, customer);
    }

    private SendMessage tipsEvent(CallbackQuery callbackQuery, String tipsData) {
        Tips tips = new Tips();
        String customerInfo = callbackQuery.getMessage().getText();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        Customer customer = dbService.getCustomerByPhoneNumber(getPhone(customerInfo));
        tips.setTips(tipsData);
        tips.setCustomer(customer);
        dbService.saveTips(tips);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Информация сохранена.");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
