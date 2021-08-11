package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.Enums.Zone;
import org.jalicz.CTF.Game.Visual.C;
import org.bukkit.entity.Player;

public class ZoneManager {

    public static Zone get(Player player) {
        double x = player.getLocation().getX(), z = player.getLocation().getZ();

        switch (Teams.get(player)) {
            case RED:
                if(x > 0.5) {
                    if((x > 29 && x < 35) && (z < 5 && z > -4)) return Zone.ENEMY_SAFE_ZONE;
                    else return Zone.TEAM_ZONE;
                } else {
                    if((x < -28 && x > -34) && (z < 5 && z > -4)) return Zone.SAFE_ZONE;
                    else return Zone.ENEMY_ZONE;
                }
            case BLUE:
                if(x < 0.5) {
                    if((x < -28 && x > -34) && (z < 5 && z > -4)) return Zone.ENEMY_SAFE_ZONE;
                    else return Zone.TEAM_ZONE;
                } else {
                    if((x > 29 && x < 35) && (z < 5 && z > -4)) return Zone.SAFE_ZONE;
                    else return Zone.ENEMY_ZONE;
                }
            default:
                return Zone.OTHER;
        }
    }
    public static String getString(Player player) {
        switch (get(player)) {
            case TEAM_ZONE: return C.DARK_GREEN + "Team";
            case ENEMY_ZONE: return C.DARK_RED + "Enemy";
            case SAFE_ZONE: return C.DARK_AQUA + "Safe-Zone";
            case ENEMY_SAFE_ZONE: return C.GOLD + "Enemy Safe-Zone";
            default: return C.YELLOW + "Other";
        }
    }
    public static boolean isOnPlatform(Player player) {
        return get(player) == Zone.TEAM_ZONE || get(player) == Zone.ENEMY_SAFE_ZONE;
    }
}