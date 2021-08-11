package org.jalicz.CTF.Game.Visual;

import org.bukkit.ChatColor;
import org.jalicz.CTF.Enums.Team;

public class C {

    public static final String
        BLACK = ChatColor.BLACK.toString(),
        DARK_GRAY = ChatColor.DARK_GRAY.toString(),
        GRAY = ChatColor.GRAY.toString(),
        WHITE = ChatColor.WHITE.toString(),
        DARK_RED = ChatColor.DARK_RED.toString(),
        RED = ChatColor.RED.toString(),
        GOLD = ChatColor.GOLD.toString(),
        YELLOW = ChatColor.YELLOW.toString(),
        GREEN = ChatColor.GREEN.toString(),
        DARK_GREEN = ChatColor.DARK_GREEN.toString(),
        AQUA = ChatColor.AQUA.toString(),
        DARK_AQUA = ChatColor.DARK_AQUA.toString(),
        BLUE = ChatColor.BLUE.toString(),
        PINK = ChatColor.LIGHT_PURPLE.toString(),
        PURPLE = ChatColor.DARK_PURPLE.toString(),
        BOLD = ChatColor.BOLD.toString(),
        ITALIC = ChatColor.ITALIC.toString(),
        RESET = ChatColor.RESET.toString();

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String getByTeam(Team team) {
        switch (team) {
            case RED: return RED;
            case BLUE: return AQUA;
            case SPECTATORS: return GREEN;
            default: return GRAY;
        }
    }
}