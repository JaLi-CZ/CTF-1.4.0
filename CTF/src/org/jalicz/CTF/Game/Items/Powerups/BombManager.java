package org.jalicz.CTF.Game.Items.Powerups;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Frozen;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Movement.EffectManager;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.Particles;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BombManager {

    public static final ArrayList<TNTPrimed> bombs = new ArrayList<>();
    private static final World world = WorldManager.GAME;
    private static final Plugin plugin = CTFPlugin.plugin;
    private static BukkitTask task = null;


    public static void spawn(Player player) {
        String team;

             if(Teams.get(player) == Team.RED) team = "R";
        else if(Teams.get(player) == Team.BLUE) team = "B";
        else return;

        TNTPrimed   bomb = (TNTPrimed) world.spawnEntity(player.getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
                    bomb.setVelocity(player.getLocation().getDirection().add(new Vector(0, 0.35, 0)));
                    bomb.setFuseTicks(50);
                    bomb.setCustomNameVisible(false); bomb.setCustomName(team);
                    bombs.add(bomb);
    }

    public static void explode(TNTPrimed bomb) {
        Team bombTeam = Teams.getTeamByEntityName(bomb.getCustomName());

        Particles.spawn(Effect.EXPLOSION_HUGE,  bomb.getLocation(), 1);
        Particles.spawn(Effect.FLAME,           bomb.getLocation(), 250);
        Particles.spawn(Effect.LARGE_SMOKE,     bomb.getLocation(), 150);

        Audio.play(bomb.getLocation(), Sound.EXPLODE, 2, 0.8f);

        Arrays.stream(world.getPlayers().toArray())
                .filter(player ->   ((Player) player).getLocation().distance(bomb.getLocation()) < 5.3+Math.random()/2 &&
                                    Teams.get((Player) player) != bombTeam &&
                                    Teams.get((Player) player) != Team.SPECTATORS &&
                                    !Frozen.is((Player) player))
                .forEach(p -> {
                    Player player = (Player) p;

                    EffectManager.add(player, PotionEffectType.SLOW,      80,  2);
                    EffectManager.add(player, PotionEffectType.BLINDNESS, 100, 1);
                    EffectManager.add(player, PotionEffectType.CONFUSION, 140, 1);

                    double  velocityX = (player.getLocation().getX() - bomb.getLocation().getX())*0.4,
                            velocityZ = (player.getLocation().getZ() - bomb.getLocation().getZ())*0.4,
                            velocityY,
                            distance = bomb.getLocation().distance(player.getLocation());

                    if(velocityX < 0) velocityX = -(Math.sqrt(-velocityX)); else velocityX = Math.sqrt(velocityX);
                    if(velocityZ < 0) velocityZ = -(Math.sqrt(-velocityZ)); else velocityZ = Math.sqrt(velocityZ);

                    if     (distance > 5.4)  velocityY = 0.3;
                    else if(distance > 5.1)  velocityY = 0.4;
                    else if(distance > 4.2)  velocityY = 0.6;
                    else if(distance > 3.4)  velocityY = 0.7;
                    else if(distance > 2.6)  velocityY = 0.9;
                    else if(distance > 2.1)  velocityY = 1.0;
                    else if(distance > 1.7)  velocityY = 1.1;
                    else if(distance > 1.3)  velocityY = 1.2;
                    else if(distance > 1.0)  velocityY = 1.4;
                    else if(distance > 0.6)  velocityY = 1.6;
                    else                     velocityY = 1.7;

                    player.setVelocity(new Vector(velocityX, velocityY, velocityZ));
                });
    }

    public static void runTask() {
        if(task == null) task = new BukkitRunnable() {
            @Override
            public void run() {
                if(bombs.isEmpty()) return;

                ArrayList<TNTPrimed> thenRemove = new ArrayList<>();

                for(TNTPrimed bomb: bombs) {
                    int x=bomb.getLocation().getBlockX(), y=bomb.getLocation().getBlockY(), z=bomb.getLocation().getBlockZ();

                    if(world.getBlockAt(x, y - 1, z).getType() != Material.AIR || world.getBlockAt(x + 1, y, z).getType() != Material.AIR ||
                            world.getBlockAt(x - 1, y, z).getType() != Material.AIR || world.getBlockAt(x, y, z + 1).getType() != Material.AIR ||
                            world.getBlockAt(x, y, z - 1).getType() != Material.AIR || world.getBlockAt(x, y + 1, z).getType() != Material.AIR) {
                        explode(bomb);
                        thenRemove.add(bomb);
                        bomb.remove();

                    } else {
                        List<Entity> nearby = bomb.getNearbyEntities(0.45, 0.9, 0.45);
                        if(!nearby.isEmpty()) for(Entity entity: nearby) if(entity instanceof Player) {
                                Player player = (Player) entity;
                                if(Teams.get(player) != Team.SPECTATORS && Teams.get(player) != Teams.getTeamByEntityName(bomb.getCustomName()) && !Frozen.is(player)) {
                                    explode(bomb);
                                    thenRemove.add(bomb);
                                    bomb.remove();
                                    break;
                                }
                            }
                    }
                }
                if(!thenRemove.isEmpty()) thenRemove.forEach(bombs::remove);
            }
        }.runTaskTimer(plugin, 0, 0);
    }
    public static void cancelTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
        if(!bombs.isEmpty()) {
            bombs.forEach(TNTPrimed::remove);
            bombs.clear();
        }
    }
}