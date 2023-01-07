package lt.tomexas.balticreferral.utils.enums;

public enum DiscordEnum {
    TOKEN(""),
    ACTIVITY_PLAYING(""),
    TEXT_CHANNEL_ID(""),
    LOG_CHANNEL_ID(""),
    CODE_AVAILABILITY(""),
    SUCCESS_COLOR(""),
    ERROR_COLOR(""),
    SUCCESS_TEXT(""),
    ERROR_TEXT(""),
    ACCOUNT_ALREADY_LINKED("");

    private String dc;
    DiscordEnum(String dc) {
        this.dc = dc;
    }

    public void setValue(String dc) {
        this.dc = dc;
    }

    @Override
    public String toString() {
        return this.dc;
    }
}
