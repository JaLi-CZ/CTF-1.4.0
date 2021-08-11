package org.jalicz.CTF.Game.Visual;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jalicz.CTF.Enums.Rank;
import java.util.HashMap;
import java.util.List;

public class RankManager {

    private static final HashMap<Player, Rank> ranks = new HashMap<>();


    private static Rank getRank(Player player) {
        if(ranks.containsKey(player)) return ranks.get(player);

        List<MetadataValue> metadata = player.getMetadata("RANK");
        Rank rank;
        if(metadata == null || metadata.size() == 0 || !(metadata.get(0).value() instanceof Rank)) rank = Rank.PLAYER;
        else rank = (Rank) metadata.get(0).value();
        ranks.put(player, rank);

        return rank;
    }
    public static String getPrefix(Player player, boolean chatPrefix) {
        if(!hasRank(player)) return "";
        String color = getColor(getRank(player)) + (chatPrefix ? C.BOLD : "");
        switch (getRank(player)) {
            case LEGEND:     return color + (chatPrefix ? "[" :"") + "LEGEND" +    (chatPrefix ? "] " : " " + C.RESET);
            case SWAGGER:    return color + (chatPrefix ? "[" :"") + "E.VIP" +     (chatPrefix ? "] " : " " + C.RESET);
            case VIP:        return color + (chatPrefix ? "[" :"") + "VIP" +       (chatPrefix ? "] " : " " + C.RESET);
            case OWNER:      return color + (chatPrefix ? "[" :"") + "OWNER" +     (chatPrefix ? "] " : " " + C.RESET);
            case ADMIN:      return color + (chatPrefix ? "[" :"") + "ADMIN" +     (chatPrefix ? "] " : " " + C.RESET);
            case DEVELOPER:  return color + (chatPrefix ? "[" :"") + "DEVELOPER" + (chatPrefix ? "] " : " " + C.RESET);
            case HL_BUILDER: return color + (chatPrefix ? "[" :"") + "H.BUILDER" + (chatPrefix ? "] " : " " + C.RESET);
            case HL_HELPER:  return color + (chatPrefix ? "[" :"") + "H.HELPER" +  (chatPrefix ? "] " : " " + C.RESET);
            case EHELPER:    return color + (chatPrefix ? "[" :"") + "E.HELPER" +  (chatPrefix ? "] " : " " + C.RESET);
            case EBUILDER:   return color + (chatPrefix ? "[" :"") + "E.BUILDER" + (chatPrefix ? "] " : " " + C.RESET);
            case YOUTUBE:    return color + (chatPrefix ? "[" :"") + "YOUTUBE" +   (chatPrefix ? "] " : " " + C.RESET);
            case HELPER:     return color + (chatPrefix ? "[" :"") + "HELPER" +    (chatPrefix ? "] " : " " + C.RESET);
            case BUILDER:    return color + (chatPrefix ? "[" :"") + "BUILDER" +   (chatPrefix ? "] " : " " + C.RESET);
            case ZK_HELPER:  return color + (chatPrefix ? "[" :"") + "ZK.HELPER" + (chatPrefix ? "] " : " " + C.RESET);
        }
        return "";
    }
    private static String getColor(Rank rank) {
        switch (rank) {
            case LEGEND: return C.GOLD;
            case SWAGGER: return C.BLUE;
            case VIP: return C.GREEN;
            case OWNER: case ADMIN: case DEVELOPER: case HL_BUILDER: case HL_HELPER: return C.DARK_RED;
            case EHELPER: case EBUILDER: return C.AQUA;
            case YOUTUBE: return C.RED;
            case HELPER: return C.DARK_GREEN;
            case BUILDER: return C.PURPLE;
            case ZK_HELPER: return C.GRAY;
        }
        return "";
    }
    public static boolean hasRank(Player player) {
        return getRank(player) != Rank.PLAYER;
    }

    public static void remove(Player player) {
        ranks.remove(player);
    }
}