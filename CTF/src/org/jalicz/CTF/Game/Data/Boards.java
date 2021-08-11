package org.jalicz.CTF.Game.Data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class Boards {

    private static final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private static final HashMap<Player, Scoreboard> boards = new HashMap<>();


    public static Scoreboard add(Player player) {
        if(boards.get(player) != null) return boards.get(player);

        Scoreboard board = scoreboardManager.getNewScoreboard();
        boards.put(player, board);
        player.setScoreboard(board);

        return board;
    }

    public static Scoreboard get(Player player) {
        Scoreboard board = boards.get(player);
        if(board == null) board = add(player);

        return board;
    }

    public static void set(Player player, Scoreboard board) {
        boards.put(player, board);
        player.setScoreboard(board);
    }

    public static void remove(Player player) {
        boards.remove(player);
    }
}