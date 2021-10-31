package ru.sberbankschool.restaurantcustomers.status;

public enum Status {
    START("start"),
    RATING_STEP("rating_step"),
    TIPS_STEP("tips_step");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
