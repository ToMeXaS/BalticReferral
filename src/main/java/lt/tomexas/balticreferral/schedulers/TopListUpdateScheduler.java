package lt.tomexas.balticreferral.schedulers;

import lt.tomexas.balticreferral.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class TopListUpdateScheduler extends BukkitRunnable {
    @Override
    public void run() {
        try {
            Main.setTopList(Main.getDatabase().fetchTopList());
            Bukkit.getLogger().info("[BalticReferral] Updated referral top list!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
