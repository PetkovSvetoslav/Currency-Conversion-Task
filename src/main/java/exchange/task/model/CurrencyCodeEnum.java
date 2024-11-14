package exchange.task.model;

public enum CurrencyCodeEnum {
    USD("United States Dollar"),
    EUR("Euro"),
    GBP("British Pound"),
    JPY("Japanese Yen"),
    AUD("Australian Dollar"),
    BGN("Bulgarian Lev");

    private final String description;

    CurrencyCodeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

