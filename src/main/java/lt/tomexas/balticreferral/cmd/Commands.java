package lt.tomexas.balticreferral.cmd;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.utils.PlayerInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInfo playerInfo = Main.getPlayerInfo().get(player.getUniqueId());
            if (label.equalsIgnoreCase("ref")) {
                if (player.getAddress() == null) return false;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("usage")));
                    return false;
                }
                if (args[0].equalsIgnoreCase("points")) {
                    String msg = Main.getMessages().get("player_points");
                    msg = msg.replace("%points%", String.valueOf(playerInfo.getPoints()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    return false;
                }
                Player referralPlayer = Bukkit.getPlayer(args[0]);
                if (referralPlayer == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("player_offline")));
                    return false;
                }

                PlayerInfo referralPInfo = Main.getPlayerInfo().get(referralPlayer.getUniqueId());

                String discordID = playerInfo.getDiscordId();
                String referralDcId = referralPInfo.getDiscordId();
                if (dupeIPCheck(player.getAddress().getAddress().toString())) player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("used")));
                else if (discordID.isEmpty())
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("discord_unlinked")));
                else if (playerInfo.getPlaytime() < Integer.parseInt(Main.getCfg().get("playtime")))
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("playtime")));
                else if (referralPlayer.equals(player))
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("self_use")));
                else if (referralDcId == null)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("referral_discord_unlinked")));
                else {
                    referralPInfo.setPoints(referralPInfo.getPoints() + 1);

                    playerInfo.setIp(player.getAddress().getAddress().toString());

                    String you_used = Main.getMessages().get("you_used");
                    you_used = you_used.replace("%arg_player%", referralPlayer.getName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', you_used));

                    String player_used = Main.getMessages().get("player_used");
                    player_used = player_used.replace("%player%", player.getName());
                    referralPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', player_used));

                    // Updates database with new data
                    try {
                        for (PlayerInfo info : Main.getPlayerInfo().values())
                            Main.getDatabase().updatePlayerInfo(info);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (label.equalsIgnoreCase("reftop")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("referral_top_list")));
                if (Main.getTopList().size() == 0)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("list_empty")));
                else {
                    int i = 0;
                    for (PlayerInfo info : Main.getTopList()) {
                        i++;
                        String list_player = Main.getMessages().get("list_player");
                        list_player = list_player.replace("%n%", String.valueOf(i))
                                .replace("%list_player%", info.getName())
                                .replace("%points%", String.valueOf(info.getPoints()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', list_player));
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            Main.getMessages().get("update_interval")));
                }
            }

            if (label.equalsIgnoreCase("link")) {
                if (!Main.getPlayerInfo().get(player.getUniqueId()).getDiscordId().isEmpty()) {
                    String msg = Main.getMessages().get("discord_already_linked");
                    msg = msg.replace("%dc-user%", Main.getPlayerInfo().get(player.getUniqueId()).getDiscordName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else if (Main.getCodes().containsKey(player.getUniqueId())) {
                    String msg = Main.getMessages().get("discord_link_code_exist");
                    msg = msg.replace("%code%", Main.getCodes().get(player.getUniqueId()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    String code = getNewCode();
                    Main.getCodes().put(player.getUniqueId(), code);

                    String discord_link_msg = Main.getMessages().get("discord_link_msg");
                    discord_link_msg = discord_link_msg.replace("%code%", code)
                            .replace("%text-channel-name%", Main.getBot().getChannelById(TextChannel.class, Main.getDiscord().get("text_channel_id")).getName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', discord_link_msg));

                    Main.getTasks().put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        Main.getCodes().remove(player.getUniqueId());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("discord_code_expired")));
                    }, Integer.parseInt(Main.getDiscord().get("code_availability")) * 20L).getTaskId());
                }
            }

        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getMessages().get("player_only")));
        }
        return false;
    }

    private String getNewCode() {
        String code;
        do {
            Random random = new Random();
            code = String.format("%04d", random.nextInt(10000));
        } while (codeExist(code));
        return code;
    }

    private boolean codeExist(String code) {
        return Main.getCodes().containsValue(code);
    }

    private boolean dupeIPCheck(String ip) {
        try {
            PreparedStatement statement = Main.getDatabase().getConnection().prepareStatement("SELECT * FROM players WHERE ip = ?");
            statement.setString(1, ip);

            ResultSet results = statement.executeQuery();

            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
