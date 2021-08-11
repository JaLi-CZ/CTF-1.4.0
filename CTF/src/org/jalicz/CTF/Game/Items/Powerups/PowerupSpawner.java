package org.jalicz.CTF.Game.Items.Powerups;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Particles;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.Random;

public class PowerupSpawner {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final World world = WorldManager.GAME;
    private static final Location spawn = new Location(world, 0.5, 100, 0.5);
    private static final Random random = new Random();
    private static BukkitTask task = null;
    private static ArmorStand stand = null;


    public static void start() {
        if(standExist()) killStands();
        spawnStand();

        if(task == null) task = new BukkitRunnable() {
            int spawnIn = 60;

            @Override
            public void run() {
                stand.setCustomNameVisible(true);
                setStandStatus(spawnIn);
                if(spawnIn < 1) {
                    spawnIn = 60;
                    spawn();
                }
                spawnIn--;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    public static void stop() {
        if(task != null) {
            task.cancel();
            task = null;
        }
        killStands();
        killItems();
    }

    public static void spawn() {
        int powerupCount = 0;
        boolean knockBallExist = false, boosterExist = false, bombExist = false, prisonExist = false;

        for(Entity e: world.getEntities()) if(e instanceof Item && !e.hasMetadata("UNPICKABLE")) {
            ItemStack item = ((Item) e).getItemStack();

            if(item.equals(ItemManager.KNOCK_BALL))   if(!knockBallExist) knockBallExist = true; else e.remove();
            else if(item.equals(ItemManager.BOOSTER)) if(!boosterExist)   boosterExist =   true; else e.remove();
            else if(item.equals(ItemManager.BOMB))    if(!bombExist)      bombExist =      true; else e.remove();
            else if(item.equals(ItemManager.PRISON))  if(!prisonExist)    prisonExist =    true; else e.remove();
            else continue;

            if(powerupCount >= 4) e.remove();
            else powerupCount++;
        }

        ItemStack powerup;
        switch (powerupCount) {
            case 0: powerup = ItemManager.getRandomPowerup(); break;
            case 1:
                if(knockBallExist)    powerup = selectRandomPowerup(ItemManager.BOOSTER,    ItemManager.BOMB,    ItemManager.PRISON);
                else if(boosterExist) powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.BOMB,    ItemManager.PRISON);
                else if(bombExist)    powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.BOOSTER, ItemManager.PRISON);
                else if(prisonExist)  powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.BOOSTER, ItemManager.BOMB);
                else return;
                break;

            case 2:
                if(knockBallExist) {
                    if(boosterExist)   powerup = selectRandomPowerup(ItemManager.BOMB,    ItemManager.PRISON);
                    else if(bombExist) powerup = selectRandomPowerup(ItemManager.BOOSTER, ItemManager.PRISON);
                    else               powerup = selectRandomPowerup(ItemManager.BOOSTER, ItemManager.BOMB);

                } else if(boosterExist) {
                    if(bombExist) powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.PRISON);
                    else          powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.BOMB);

                } else if(bombExist && prisonExist) powerup = selectRandomPowerup(ItemManager.KNOCK_BALL, ItemManager.BOOSTER);
                else return;
                break;

            case 3:
                if(!knockBallExist)    powerup = ItemManager.KNOCK_BALL;
                else if(!boosterExist) powerup = ItemManager.BOOSTER;
                else if(!bombExist)    powerup = ItemManager.BOMB;
                else if(!prisonExist)  powerup = ItemManager.PRISON;
                else return;
                break;

            default: return;
        }

        Item item = world.dropItem(spawn, powerup);
        item.setVelocity(new Vector(Math.random()*0.4-0.2, 0.8+Math.random()/5, Math.random()*0.4-0.2));

        for(int i=0; i<30; i++) {
            Particles.spawn(Effect.FLAME, new Location(world, Math.random()*2 -0.5, Math.random()*2 +100, Math.random()*2 -0.5), 1);
            Particles.spawn(Effect.FIREWORKS_SPARK, new Location(world, Math.random()*2 -0.5, Math.random()*2 +100, Math.random()*2 -0.5), 2);
        }
        Audio.play(spawn, Sound.CHICKEN_EGG_POP, 1.1f, 0.6f);
    }

    private static ItemStack selectRandomPowerup(ItemStack... powerups) {
        return powerups[random.nextInt(powerups.length)];
    }

    public static void killItems() {
        for(Entity e: world.getEntities()) if(e instanceof Item && !e.hasMetadata("UNPICKABLE")) e.remove();
    }

    private static void spawnStand() {
        killStands();
        stand = (ArmorStand) world.spawnEntity(new Location(world, 0.5, 104, 0.5), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setCustomNameVisible(false);
    }

    public static void killStands() {
        for(Entity e: world.getEntities()) if(e instanceof ArmorStand) e.remove();
        stand = null;
    }

    public static boolean standExist() {
        return stand != null;
    }

    private static void setStandStatus(int spawnIn) {
        if(standExist()) {
            stand.teleport(new Location(world, 0.5, 100.2+(spawnIn/15.0), 0.5));

            double x = Math.cos(spawnIn*6)*0.7, y = stand.getLocation().getY(), z = Math.sin(spawnIn*6)*0.7;
            Particles.spawn(Effect.HAPPY_VILLAGER, new Location(world, x + 0.5, y, z + 0.5), 1);
            Particles.spawn(Effect.HAPPY_VILLAGER, new Location(world, -x + 0.5, y, -z + 0.5), 1);

            String color;
            if(spawnIn > 50)      color = C.WHITE;
            else if(spawnIn > 40) color = C.PURPLE;
            else if(spawnIn > 30) color = C.PINK;
            else if(spawnIn > 25) color = C.AQUA;
            else if(spawnIn > 20) color = C.GREEN;
            else if(spawnIn > 15) color = C.YELLOW;
            else if(spawnIn > 10) color = C.GOLD;
            else if(spawnIn > 5)  color = C.RED;
            else color = C.DARK_RED;

            stand.setCustomName(world.getEntities().stream().filter(e -> e instanceof Item).count() >= 4 ? C.RED+ "FULL!" : C.BLACK + "Spawn in: " + color + (spawnIn/10.0) + "s");
        }
    }
}