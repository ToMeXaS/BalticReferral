package lt.tomexas.balticreferral.utils;

public class PlayerInfo {

    private String uuid;
    private String name;
    private String ip;
    private String discordId;
    private String discordName;
    private int playtime;
    private int points;

    public PlayerInfo(String uuid, String name, String ip, String discordId, String discordName, int playtime, int points) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.discordId = discordId;
        this.discordName = discordName;
        this.playtime = playtime;
        this.points = points;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discord) {
        this.discordId = discord;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }
}
