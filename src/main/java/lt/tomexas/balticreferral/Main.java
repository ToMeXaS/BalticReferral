package lt.tomexas.balticreferral;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import lt.tomexas.balticreferral.cmd.Commands;
import lt.tomexas.balticreferral.db.Database;
import lt.tomexas.balticreferral.discord.DiscordBot;
import lt.tomexas.balticreferral.listeners.PlayerJoinLeaveListener;
import lt.tomexas.balticreferral.placeholders.ReferralExpansion;
import lt.tomexas.balticreferral.schedulers.PlayTimeScheduler;
import lt.tomexas.balticreferral.schedulers.TopListUpdateScheduler;
import lt.tomexas.balticreferral.utils.PlayerInfo;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {

    private static Main instance;

    private static Database database;
    private static final HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();
    private static List<PlayerInfo> topList = new ArrayList<>();

    private static JDA bot;
    private static final HashMap<UUID, String> codes = new HashMap<>();
    private static final HashMap<UUID, Integer> tasks = new HashMap<>();

    private File configFile;
    private static YamlConfiguration config;
    private static final HashMap<String, String> messages = new HashMap<>();
    private static final HashMap<String, String> db_data = new HashMap<>();
    private static final HashMap<String, String> cfg = new HashMap<>();
    private static final HashMap<String, String> placeholders = new HashMap<>();
    private static final HashMap<String, String> discord = new HashMap<>();

    public void onEnable() {

        instance = this;

        createConfig();
        retrieveDataFromConfig();

        try {
            database = new Database();
            database.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        this.getCommand("ref").setExecutor(new Commands());
        this.getCommand("ref").setTabCompleter(new Commands());
        this.getCommand("reftop").setExecutor(new Commands());
        this.getCommand("link").setExecutor(new Commands());

        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerInfo.put(player.getUniqueId(), database.getPlayerInfoFromDatabase(player));
                if (playerInfo.get(player.getUniqueId()).getPlaytime() < Integer.parseInt(cfg.get("playtime")))
                    new PlayTimeScheduler(player).runTaskTimer(this, 0, 20L);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new TopListUpdateScheduler().runTaskTimer(this, 0L, Integer.parseInt(cfg.get("update_interval")) * 20L);


        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ReferralExpansion().register();
        }

        DiscordBot.init();
    }

    public void onDisable() {
        try {
            for (PlayerInfo playerInfo : playerInfo.values())
                database.updatePlayerInfo(playerInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bot.shutdown();
    }

    private void createConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
            saveResource("config.yml", false);

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    private void retrieveDataFromConfig() {
        for (String key : config.getConfigurationSection("messages").getKeys(false))
            messages.put(key, config.getString("messages." + key));

        for (Map.Entry<String, String> msg : messages.entrySet()) {
            if (msg.getValue().contains("%prefix%")) {
                String message = msg.getValue();
                message = message.replace("%prefix%", messages.get("prefix"));
                messages.replace(msg.getKey(), message);
            }
        }

        for (String key : config.getConfigurationSection("database").getKeys(false))
            db_data.put(key, config.getString("database." + key));

        for (String key : config.getConfigurationSection("config").getKeys(false))
            cfg.put(key, config.getString("config." + key));

        for (String key : config.getConfigurationSection("placeholders").getKeys(false))
            placeholders.put(key, config.getString("placeholders." + key));

        for (String key : config.getConfigurationSection("discord").getKeys(false))
            discord.put(key, config.getString("discord." + key));
    }

    public static HashMap<UUID, PlayerInfo> getPlayerInfo() {
        return playerInfo;
    }

    public static List<PlayerInfo> getTopList() {
        return topList;
    }

    public static void setTopList(List<PlayerInfo> topList) {
        Main.topList = topList;
    }

    public static Main getInstance() {
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }

    public static HashMap<String, String> getDb_data() {
        return db_data;
    }

    public static HashMap<String, String> getMessages() {
        return messages;
    }

    public static HashMap<String, String> getCfg() {
        return cfg;
    }

    public static HashMap<String, String> getPlaceholders() {
        return placeholders;
    }

    public static HashMap<String, String> getDiscord() {
        return discord;
    }

    public static JDA getBot() {
        return bot;
    }

    public static void setBot(JDA bot) {
        Main.bot = bot;
    }

    public static HashMap<UUID, String> getCodes() {
        return codes;
    }

    public static HashMap<UUID, Integer> getTasks() {
        return tasks;
    }
}
