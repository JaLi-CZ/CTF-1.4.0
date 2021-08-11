package org.jalicz.CTF.Game.Movement;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Zone;
import org.jalicz.CTF.Game.Data.KitManager;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.ZoneManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerSpeedManager {

    private static BukkitTask task = null;
    private static final Plugin plugin = CTFPlugin.plugin;

    public static void runTask() {
        if(task == null) task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p: Players.get()) switch (KitManager.get(p)) {
                    case DEFENDER:
                        if(ZoneManager.isOnPlatform(p)) EffectManager.addIfAbsent(p, PotionEffectType.SPEED, 1, 2);
                        else EffectManager.remove(p, PotionEffectType.SPEED);
                        break;

                    case FREEZE_IMMUNITY:
                        EffectManager.addIfAbsent(p, PotionEffectType.SPEED, 1, 1);
                        break;

                    case RUSHER:
                        if(ZoneManager.get(p) == Zone.ENEMY_ZONE || ZoneManager.get(p) == Zone.SAFE_ZONE) EffectManager.addIfAbsent(p, PotionEffectType.SPEED, 1, 1);
                        break;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    public static void cancelTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }
}