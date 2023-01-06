package lt.tomexas.balticreferral.db;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.utils.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private Connection connection;

    public Connection getConnection() throws SQLException {

        if (connection != null)
            return connection;

        String host = Main.getDb_data().get("db_host");
        String table = Main.getDb_data().get("db_table");
        String user = Main.getDb_data().get("db_user");
        String password = Main.getDb_data().get("db_password");

        String url = "jdbc:mysql://"+ host +"/" + table;
        this.connection = DriverManager.getConnection(url, user, password);

        Bukkit.getLogger().info("[BalticReferral] Connected to MYSQL database!");

        return this.connection;
    }

    public void initializeDatabase() throws SQLException{
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS players(uuid varchar(255), name varchar(255), ip varchar(255), discord_id varchar(255), discord_name varchar(255), playtime int(255), points int(255))";
        statement.execute(sql);

        statement.close();
    }

    public PlayerInfo getPlayerInfoFromDatabase(Player player) throws SQLException {
        PlayerInfo playerInfo = Main.getDatabase().findPlayerInfoByUUID(player.getUniqueId().toString());

        if (playerInfo == null) {
            playerInfo = new PlayerInfo(player.getUniqueId().toString(), player.getName(), "", "", "", 0, 0);

            Main.getDatabase().createPlayerInfo(playerInfo);

            return playerInfo;
        }

        return playerInfo;
    }

    public PlayerInfo findPlayerInfoByUUID(String uuid) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM players WHERE uuid = ?");
        statement.setString(1, uuid);

        ResultSet results = statement.executeQuery();

        if (results.next()) {
            String name = results.getString("name");
            String ip = results.getString("ip");
            String discordId = results.getString("discord_id");
            String discordName = results.getString("discord_name");
            int playtime = results.getInt("playtime");
            int points = results.getInt("points");

            PlayerInfo playerInfo = new PlayerInfo(uuid, name, ip, discordId, discordName, playtime, points);

            statement.close();

            return playerInfo;
        }

        statement.close();

        return null;
    }

    public List<PlayerInfo> fetchTopList() throws SQLException {
        for (Player player : Bukkit.getOnlinePlayers())
            updatePlayerInfo(Main.getPlayerInfo().get(player.getUniqueId()));


        Statement statement = getConnection().createStatement();
        String sql = "SELECT * FROM players WHERE points > 0 ORDER BY points DESC LIMIT 5";

        ResultSet results = statement.executeQuery(sql);
        List<PlayerInfo> playerInfo = new ArrayList<>();
        while (results.next()) {
            String uuid = results.getString("uuid");
            String name = results.getString("name");
            String ip = results.getString("ip");
            String discordId = results.getString("discord_id");
            String discordName = results.getString("discord_name");
            int playtime = results.getInt("playtime");
            int points = results.getInt("points");

            playerInfo.add(new PlayerInfo(uuid, name, ip, discordId, discordName, playtime, points));
        }

        statement.close();

        return playerInfo;
    }

    public void createPlayerInfo(PlayerInfo info) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO players(uuid, name, ip, discord_id, discord_name, playtime, points) VALUES(?,?,?,?,?,?,?)");
        statement.setString(1, info.getUuid());
        statement.setString(2, info.getName());
        statement.setString(3, info.getIp());
        statement.setString(4, info.getDiscordId());
        statement.setString(5, info.getDiscordName());
        statement.setInt(6, info.getPlaytime());
        statement.setInt(7, info.getPoints());

        statement.executeUpdate();
        statement.close();

    }

    public void updatePlayerInfo(PlayerInfo info) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE players SET name = ?, ip = ?, discord_id = ?, discord_name = ?, playtime = ?, points = ? WHERE uuid = ?");
        statement.setString(1, info.getName());
        statement.setString(2, info.getIp());
        statement.setString(3, info.getDiscordId());
        statement.setString(4, info.getDiscordName());
        statement.setInt(5, info.getPlaytime());
        statement.setInt(6, info.getPoints());
        statement.setString(7, info.getUuid());

        statement.executeUpdate();
        statement.close();

    }
}
