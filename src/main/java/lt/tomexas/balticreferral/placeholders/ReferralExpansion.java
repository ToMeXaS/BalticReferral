package lt.tomexas.balticreferral.placeholders;

import lt.tomexas.balticreferral.Main;
import lt.tomexas.balticreferral.utils.PlayerInfo;
import lt.tomexas.balticreferral.utils.enums.PlaceholderEnum;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReferralExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "referral";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ToMeXaS";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        List<PlayerInfo> info = Main.getTopList();
        if (params.equalsIgnoreCase("top_1_name")) {
            if (info.size() == 0) return PlaceholderEnum.NO_NAME.toString();
            return info.get(0).getName();
        }
        if (params.equalsIgnoreCase("top_1_points")) {
            if (info.size() == 0) return PlaceholderEnum.NO_POINTS.toString();
            return String.valueOf(info.get(0).getPoints());
        }

        if (params.equalsIgnoreCase("top_2_name")) {
            if (info.size() <= 1) return PlaceholderEnum.NO_NAME.toString();
            return info.get(1).getName();
        }
        if (params.equalsIgnoreCase("top_2_points")) {
            if (info.size() <= 1) return PlaceholderEnum.NO_POINTS.toString();
            return String.valueOf(info.get(1).getPoints());
        }

        if (params.equalsIgnoreCase("top_3_name")) {
            if (info.size() <= 2) return PlaceholderEnum.NO_NAME.toString();
            return info.get(2).getName();
        }
        if (params.equalsIgnoreCase("top_3_points")) {
            if (info.size() <= 2) return PlaceholderEnum.NO_POINTS.toString();
            return String.valueOf(info.get(2).getPoints());
        }

        if (params.equalsIgnoreCase("top_4_name")) {
            if (info.size() <= 3) return PlaceholderEnum.NO_NAME.toString();
            return info.get(3).getName();
        }
        if (params.equalsIgnoreCase("top_4_points")) {
            if (info.size() <= 3) return PlaceholderEnum.NO_POINTS.toString();
            return String.valueOf(info.get(3).getPoints());
        }

        if (params.equalsIgnoreCase("top_5_name")) {
            if (info.size() <= 4) return PlaceholderEnum.NO_NAME.toString();
            return info.get(4).getName();
        }
        if (params.equalsIgnoreCase("top_5_points")) {
            if (info.size() <= 4) return PlaceholderEnum.NO_POINTS.toString();
            return String.valueOf(info.get(4).getPoints());
        }

        if (params.equalsIgnoreCase("points"))
            return String.valueOf(Main.getPlayerInfo().get(player.getUniqueId()).getPoints());
        return null;
    }
}
