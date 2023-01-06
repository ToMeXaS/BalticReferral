package lt.tomexas.balticreferral.discord.events;

import lt.tomexas.balticreferral.Main;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            if (event.isFromType(ChannelType.TEXT)) {
                if (event.getChannel().asTextChannel().getId().equals(Main.getDiscord().get("text_channel_id"))) {
                    UUID uuid = codeExist(event.getMessage().getContentRaw());
                    if (uuid == null) {
                        MessageEmbed messageEmbed = new MessageEmbed(
                                null, null, Main.getDiscord().get("error_text"), EmbedType.UNKNOWN, null, Integer.parseInt(Main.getDiscord().get("error_color")), null, null, null, null, null, null, null
                        );
                        event.getMessage().replyEmbeds(messageEmbed).queue();
                    } else if (checkIfDiscordExistsInDB(event.getAuthor().getId())) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return;
                        MessageEmbed messageEmbed = new MessageEmbed(
                                null, null, Main.getDiscord().get("account_already_linked"), EmbedType.UNKNOWN, null, Integer.parseInt(Main.getDiscord().get("error_color")), null, null, null, null, null, null, null
                        );
                        event.getMessage().replyEmbeds(messageEmbed).queue();

                        Main.getCodes().remove(player.getUniqueId());
                        Bukkit.getScheduler().cancelTask(Main.getTasks().get(player.getUniqueId()));
                        Main.getTasks().remove(player.getUniqueId());
                    } else {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return;
                        String discord_msg = Main.getDiscord().get("success_text");
                        discord_msg = discord_msg.replace("%player%", player.getName());
                        MessageEmbed messageEmbed = new MessageEmbed(
                                null, null, discord_msg, EmbedType.UNKNOWN, null, Integer.parseInt(Main.getDiscord().get("success_color")), null, null, null, null, null, null, null
                        );
                        event.getMessage().replyEmbeds(messageEmbed).queue();
                        String server_msg = Main.getMessages().get("discord_account_linked_to");
                        server_msg = server_msg.replace("%dc-user%", event.getAuthor().getAsTag());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', server_msg));

                        Main.getPlayerInfo().get(player.getUniqueId()).setDiscordId(event.getAuthor().getId());
                        Main.getPlayerInfo().get(player.getUniqueId()).setDiscordName(event.getAuthor().getAsTag());
                        Main.getCodes().remove(player.getUniqueId());
                        Bukkit.getScheduler().cancelTask(Main.getTasks().get(player.getUniqueId()));
                        Main.getTasks().remove(player.getUniqueId());
                    }
                }
            }
        }
    }

    private boolean checkIfDiscordExistsInDB(String id) {
        try {
            PreparedStatement statement = Main.getDatabase().getConnection().prepareStatement("SELECT * FROM players WHERE discord_id = ?");
            statement.setString(1, id);

            ResultSet results = statement.executeQuery();

            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private UUID codeExist(String code) {
        for (Map.Entry<UUID, String> entry : Main.getCodes().entrySet())
            if (code.equalsIgnoreCase(entry.getValue()))
                return entry.getKey();
        return null;
    }
}
