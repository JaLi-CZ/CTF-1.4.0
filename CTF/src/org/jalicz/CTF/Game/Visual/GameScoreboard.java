package org.jalicz.CTF.Game.Visual;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Game;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jalicz.CTF.Game.Data.*;

public class GameScoreboard {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final String IP = C.GREEN + "play.survival-games.cz";
    private static int lineValue = 11;

    public static void update(Player player) {
        Scoreboard board = Boards.get(player);

        Objective o = board.getObjective("board");
        if(o != null) o.unregister();
        o = board.registerNewObjective("board", "");
        o.setDisplayName(C.GOLD + "CTF " + C.DARK_GRAY + "(" + C.GRAY + "Beta" + C.DARK_GRAY + ")");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);

        lineValue = 11;

        if(Game.isRunning()) {
            newLine(o, " ");
            newLine(o, C.WHITE + "Time: " + C.GREEN + Game.getTimeString());
            newLine(o, C.WHITE + "Score: " + GameScore.getString());
            newLine(o, C.WHITE + "Teams: " + Teams.getString());
            newLine(o, "  ");

            if(Teams.get(player) != Team.SPECTATORS) {
                newLine(o, C.WHITE + "Zone: " + ZoneManager.getString(player));
                newLine(o, C.WHITE + "Your Team: " + Teams.getString(player));
                if(KitManager.get(player) != Kit.NONE) newLine(o, C.WHITE + "Kit: " + C.GREEN + KitManager.getString(player));
                if(Frozen.is(player)) {
                    newLine(o, "   ");
                    newLine(o, C.GOLD + "Unfreeze in: " + C.GREEN + C.BOLD + Frozen.getDuration(player));
                }
            }
        } else {
            newLine(o, " ");
            newLine(o, C.WHITE + "Start in: " + Countdown.getString());
            newLine(o, C.WHITE + "Players: " + C.GREEN + Players.count() + "/" + Players.MAXIMUM);
            newLine(o, "  ");
            newLine(o, C.WHITE + "Teams: " + Teams.getString());
            newLine(o, C.WHITE + "Your Team: " + Teams.getString(player));
            newLine(o, C.WHITE + "Kit: " + C.GREEN + KitManager.getString(player));
        }
        newLine(o, "    ");
        newLine(o, IP);

        Boards.set(player, board);
    }
    public static void update() {
        Players.get().forEach((GameScoreboard::update));
    }
    public static void updateAfterDelay(int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskLater(plugin, delay);
    }

    private static void newLine(Objective o, String name) {
        Score line = o.getScore(name);
        line.setScore(Math.max(lineValue, 0));
        lineValue--;
    }
}