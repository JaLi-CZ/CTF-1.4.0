package org.jalicz.CTF.Game.Data;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Status;
import org.jalicz.CTF.Game.Game;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.GameScoreboard;
import org.jalicz.CTF.Game.Visual.Message;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Countdown {

    public static int countdown = 60;
    private static BukkitTask task = null;
    private static final Plugin plugin = CTFPlugin.plugin;

    public static void subtract() {
        countdown--;
    }
    public static void skip() {
        if(countdown > 20) countdown = 20;
    }
    public static void stop() {
        if(task != null) {
            task.cancel();
            task = null;
        }
        countdown = 60;
        Message.actionBar("");
        Message.xpBar(0);
        GameScoreboard.update();
    }

    public static void doActions() {
        for(Player p: Players.get()) p.setLevel(countdown);

        if(countdown < 6 && countdown > 0 || countdown == 40 || countdown == 30 || countdown == 20 || countdown == 15 || countdown == 10) {
            Message.actionBar(C.BLACK + "The Game starts in: " + C.GREEN + C.BOLD + countdown + "s");
            Audio.play(Sound.CLICK, 0.6f, 1f);
        }
        if(countdown < 11 && countdown > 1) Audio.play(Sound.NOTE_PLING, 0.4f, 1.2f);
        else if(countdown == 1) Audio.play(Sound.NOTE_PLING, 1.4f, 1.8f);
    }

    public static String getString() {
        if(Game.status == Status.UNABLE_TO_START) return C.YELLOW + "Waiting..";
        else if(Game.status == Status.WAITING) return C.GREEN + C.BOLD + countdown + "s";
        else return C.GREEN + "---";
    }

    public static void runTask() {
        if(Game.isRunning() || !Players.isEnough()) return;

        if(task == null) task = new BukkitRunnable() {
            @Override
            public void run() {
                Game.status = Status.WAITING;
                if(Players.isEnough()) {
                    subtract();
                    if(Players.isMax()) skip();
                    doActions();
                    GameScoreboard.update();

                    if(countdown < 1) {
                        stop();
                        task = null;
                        Game.start();
                        cancel();
                    }
                } else {
                    Game.status = Status.UNABLE_TO_START;
                    stop();
                    task = null;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}