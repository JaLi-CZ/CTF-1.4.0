package org.jalicz.CTF.Game.Movement;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import org.jalicz.CTF.Game.Visual.Audio;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class LobbyTransporter {

    private static final HashMap<Player, BukkitTask> tasks = new HashMap<>();
    private static final Plugin plugin = CTFPlugin.plugin;


    public static void clickedWithClock(Player player) {
        if(isTaskRunning(player)) {
            cancelTask(player);
            Message.player(player, C.RED + C.BOLD + "Teleport cancelled!");
            Audio.play(player, Sound.NOTE_BASS, 1, 0.7f);
        } else {
            newTask(player);
            Message.player(player, C.GREEN + C.BOLD + "You will be teleported to the lobby in 3 seconds.\n Right-click again to cancel the teleport!");
            Audio.play(player, Sound.CLICK, 1, 1.5f);
        }
    }

    public static void newTask(Player player) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                player.performCommand("lobby");
                tasks.remove(player);
                cancel();
            }
        }.runTaskLater(plugin, 60);
        tasks.put(player, task);
    }
    public static void cancelTask(Player player) {
        if(tasks.get(player) != null) {
            tasks.get(player).cancel();
            tasks.remove(player);
        }
    }
    public static void cancelAllTasks() {
        if(!tasks.isEmpty()) {
            tasks.forEach((player, task) -> task.cancel());
            tasks.clear();
        }
    }
    public static boolean isTaskRunning(Player player) {
        return tasks.containsKey(player);
    }
}