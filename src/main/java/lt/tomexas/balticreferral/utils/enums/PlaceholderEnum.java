package lt.tomexas.balticreferral.utils.enums;

public enum PlaceholderEnum {
    NO_NAME(""),
    NO_POINTS("");

    private String placeholder;
    PlaceholderEnum(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setValue(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String toString() {
        return this.placeholder;
    }
}
