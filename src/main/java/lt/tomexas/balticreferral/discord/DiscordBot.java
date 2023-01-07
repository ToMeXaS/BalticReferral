package lt.tomexas.balticreferral.discord;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.discord.events.MessageReceivedListener;
import lt.tomexas.balticreferral.schedulers.BotUpdateScheduler;
import lt.tomexas.balticreferral.utils.enums.DiscordEnum;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

    public static void init() {

        Main.setBot(JDABuilder.createDefault(DiscordEnum.TOKEN.toString())
                .addEventListeners(new MessageReceivedListener())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build());

        new BotUpdateScheduler().runTaskTimer(Main.getInstance(), 0, 20L);
    }

}
