package org.jalicz.CTF.Game.Visual;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Teams;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import java.util.Random;

public class FireworkSpawner {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final Random random = new Random();

    public static void spawn(Player player, boolean isEnd) {
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation().add(new Vector(0, 2, 0)), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.setPower(random.nextInt(4));

        if(isEnd) {
            meta.addEffect(FireworkEffect.builder().with(getRandomType()).withColor(getRandomColor()).withFade(getRandomColor()).flicker(random.nextBoolean()).trail(random.nextBoolean()).build());
            fw.setFireworkMeta(meta);

        } else {
            Color c = Color.WHITE;
            if(Teams.get(player) == Team.RED) c = Color.RED;
            else if(Teams.get(player) == Team.BLUE) c = Color.AQUA;

            meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(c).withFade(Color.BLACK).build());
            fw.setFireworkMeta(meta);

            new BukkitRunnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }.runTaskLater(plugin, 5);
        }
    }

    private static Color getRandomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
    private static FireworkEffect.Type getRandomType() {
        switch (random.nextInt(6)) {
            case 1:
                return FireworkEffect.Type.BALL;
            case 2:
                return FireworkEffect.Type.BURST;
            case 3:
                return FireworkEffect.Type.CREEPER;
            case 4:
                return FireworkEffect.Type.STAR;
            default:
                return FireworkEffect.Type.BALL_LARGE;
        }
    }
}