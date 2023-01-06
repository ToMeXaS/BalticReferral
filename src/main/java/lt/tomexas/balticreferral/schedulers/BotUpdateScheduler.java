package lt.tomexas.balticreferral.schedulers;

import lt.tomexas.balticreferral.Main;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BotUpdateScheduler extends BukkitRunnable {

    @Override
    public void run() {
        String message = Main.getDiscord().get("activity_playing");
        String online = String.valueOf(Bukkit.getOnlinePlayers().size());
        String maxOnline = String.valueOf(Bukkit.getMaxPlayers());

        message = message.replace("%online%", online)
                .replace("%max-online%", maxOnline);
        Main.getBot().getPresence().setActivity(Activity.playing(message));
    }
}
