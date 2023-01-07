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
import lt.tomexas.balticreferral.utils.enums.*;
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

    private static YamlConfiguration config;

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
                if (playerInfo.get(player.getUniqueId()).getPlaytime() < Integer.parseInt(ConfigEnum.PLAYTIME.toString()))
                    new PlayTimeScheduler(player).runTaskTimer(this, 0, 20L);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new TopListUpdateScheduler().runTaskTimer(this, 0L, Integer.parseInt(ConfigEnum.UPDATE_INTERVAL.toString()) * 20L);


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
        File configFile = new File(getDataFolder(), "config.yml");
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
        for (MessagesEnum msg : MessagesEnum.values())
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                if (!msg.name().equalsIgnoreCase(key)) continue;
                msg.setValue(config.getString("messages." + key));
            }

        for (DatabaseEnum db : DatabaseEnum.values())
            for (String key : config.getConfigurationSection("database").getKeys(false)) {
                if (!db.name().equalsIgnoreCase(key)) continue;
                db.setValue(config.getString("database." + key));
            }

        for (ConfigEnum cfg : ConfigEnum.values())
            for (String key : config.getConfigurationSection("config").getKeys(false)) {
                if (!cfg.name().equalsIgnoreCase(key)) continue;
                cfg.setValue(config.getString("config." + key));
            }

        for (PlaceholderEnum ph : PlaceholderEnum.values())
            for (String key : config.getConfigurationSection("placeholders").getKeys(false)) {
                if (!ph.name().equalsIgnoreCase(key)) continue;
                ph.setValue(config.getString("placeholders." + key));
            }

        for (DiscordEnum dc : DiscordEnum.values())
            for (String key : config.getConfigurationSection("discord").getKeys(false)) {
                if (!dc.name().equalsIgnoreCase(key)) continue;
                dc.setValue(config.getString("discord." + key));
            }
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

    public static YamlConfiguration getYaml() {
        return config;
    }
}
