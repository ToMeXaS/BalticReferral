package lt.tomexas.balticreferral.utils.enums;

public enum MessagesEnum {
    USAGE(""),
    PLAYER_POINTS(""),
    PLAYER_OFFLINE(""),
    USED(""),
    DISCORD_UNLINKED(""),
    REFERRAL_DISCORD_UNLINKED(""),
    PLAYTIME(""),
    REFERRAL_PLAYTIME(""),
    SELF_USE(""),
    YOU_USED(""),
    PLAYER_USED(""),
    REFERRAL_TOP_LIST(""),
    LIST_EMPTY(""),
    LIST_PLAYER(""),
    UPDATE_INTERVAL(""),
    PLAYER_ONLY(""),
    DISCORD_LINK_MSG(""),
    DISCORD_ACCOUNT_LINKED_TO(""),
    DISCORD_ACCOUNT_ALREADY_LINKED(""),
    DISCORD_CODE_EXPIRED(""),
    DISCORD_CODE_EXISTS("");

    private String message;
    MessagesEnum(String msg) {
        this.message = msg;
    }

    public void setValue(String msg) {
        this.message = msg;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
