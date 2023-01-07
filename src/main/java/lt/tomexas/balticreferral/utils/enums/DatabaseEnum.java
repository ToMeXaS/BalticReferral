package lt.tomexas.balticreferral.utils.enums;

public enum DatabaseEnum {
    DB_HOST(""),
    DB_USER(""),
    DB_PASSWORD(""),
    DB_TABLE("");

    private String db;
    DatabaseEnum(String db) {
        this.db = db;
    }

    public void setValue(String db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return this.db;
    }
}
