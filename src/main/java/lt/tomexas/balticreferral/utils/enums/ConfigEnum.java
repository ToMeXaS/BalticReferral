package lt.tomexas.balticreferral.utils.enums;

public enum ConfigEnum {
    UPDATE_INTERVAL(""),
    PLAYTIME("");

    private String cfg;
    ConfigEnum(String cfg) {
        this.cfg = cfg;
    }

    public void setValue(String cfg) {
        this.cfg = cfg;
    }

    @Override
    public String toString() {
        return this.cfg;
    }
}
