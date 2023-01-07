package lt.tomexas.balticreferral.listeners;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.schedulers.PlayTimeScheduler;
import lt.tomexas.balticreferral.utils.enums.ConfigEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            Main.getPlayerInfo().put(player.getUniqueId(), Main.getDatabase().getPlayerInfoFromDatabase(player));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (Main.getPlayerInfo().get(player.getUniqueId()).getPlaytime() < Integer.parseInt(ConfigEnum.PLAYTIME.toString()))
            new PlayTimeScheduler(player).runTaskTimer(Main.getInstance(), 0, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            Main.getDatabase().updatePlayerInfo(Main.getPlayerInfo().get(player.getUniqueId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
