package lt.tomexas.balticreferral.schedulers;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.utils.PlayerInfo;
import lt.tomexas.balticreferral.utils.enums.ConfigEnum;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayTimeScheduler extends BukkitRunnable {

    private final Player player;

    public PlayTimeScheduler(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) cancel();

        PlayerInfo playerInfo = Main.getPlayerInfo().get(player.getUniqueId());
        playerInfo.setPlaytime(playerInfo.getPlaytime()+1);

        if (playerInfo.getPlaytime() >= Integer.parseInt(ConfigEnum.PLAYTIME.toString()))
            cancel();

    }
}
