package org.jalicz.CTF.Game.Data;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;

import java.util.HashMap;

public class SpectatorCompassFocus {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final HashMap<Player, Player> map = new HashMap<>();
    private static BukkitTask task = null;


    public static void set(Player spectator, Player target) {
        remove(spectator);
        spectator.setCompassTarget(target.getLocation());
        map.put(spectator, target);
    }

    public static void remove(Player spectator) {
        map.remove(spectator);
    }

    public static void clear() {
        map.clear();
    }

    public static void runTask() {
        if(task == null) task = new BukkitRunnable() {
            @Override
            public void run() {
                if(!map.isEmpty()) for(Player p: map.keySet()) {
                    Player target = map.get(p);
                    if(p == null || target == null) {
                        remove(p);
                        continue;
                    }
                    p.setCompassTarget(target.getLocation());
                    Message.actionBar(p, C.GOLD + C.BOLD + target.getName() + C.RESET + C.GREEN + " is " +
                            (Math.round(p.getLocation().distance(target.getLocation())*10.)/10.) + " blocks away!");
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public static void cancelTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }
}