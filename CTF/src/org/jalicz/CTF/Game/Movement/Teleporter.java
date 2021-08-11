package org.jalicz.CTF.Game.Movement;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Data.KitManager;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import java.util.Random;

public class Teleporter {
    
    private static final World lobby = WorldManager.LOBBY, game = WorldManager.GAME;
    private static final Plugin plugin = CTFPlugin.plugin;
    private static final Random random = new Random();
    private static final String path = "worlds.lobby.spawn.";


    public static void toSpawn(Player player) {
        Location spawn = new Location(lobby, plugin.getConfig().getDouble(path + "x"), plugin.getConfig().getDouble(path + "y"),
                plugin.getConfig().getDouble(path + "z"), random.nextInt(361)-180, random.nextInt(31)-10);
        player.teleport(spawn);
        player.setVelocity(new Vector(Math.random()-0.5, Math.random()/4+1, Math.random()-0.5).multiply(plugin.getConfig().getDouble(path +"velocity-amount")));
        Audio.play(spawn, Sound.NOTE_PLING, 1.3f, 1.7f);
    }
    public static void toSpawn() {
        for(Player p: Players.get()) toSpawn(p);
    }

    public static void toPlatform(Player player) {
        switch (Teams.get(player)) {
            case RED:
                switch (KitManager.get(player)) {
                    case DEFENDER:          player.teleport(new Location(game, Math.random()*5+23, 100, Math.random()*21-10, 90, 0));    return;
                    case FREEZE_IMMUNITY:   player.teleport(new Location(game, Math.random()*8+12, 100, Math.random()*25-12, 90, 0));    return;
                    case RUSHER:            player.teleport(new Location(game, Math.random()*8+2, 100, Math.random()*29-14, 90, 0));     return;
                    case POWERUP_MASTER:    player.teleport(new Location(game, Math.random()*5+2, 100, Math.random()*13-6, 90, 0));      return;
                    default:                player.teleport(new Location(game, Math.random()*22+4, 100, Math.random()*23-11, 90, 0));    return;
                }
            case BLUE:
                switch (KitManager.get(player)) {
                    case DEFENDER:          player.teleport(new Location(game, -(Math.random()*5+23)+1, 100, Math.random()*21-10, -90, 0));  return;
                    case FREEZE_IMMUNITY:   player.teleport(new Location(game, -(Math.random()*8+12)+1, 100, Math.random()*25-12, -90, 0));  return;
                    case RUSHER:            player.teleport(new Location(game, -(Math.random()*8+2)+1, 100, Math.random()*29-14, -90, 0));   return;
                    case POWERUP_MASTER:    player.teleport(new Location(game, -(Math.random()*5+2)+1, 100, Math.random()*13-6, -90, 0));    return;
                    default:                player.teleport(new Location(game, -(Math.random()*22+4)+1, 100, Math.random()*23-11, -90, 0));  return;
                }
            default: player.teleport(new Location(game, 0.5, 115, 0.5, random.nextInt(361)-180, 90));
        }
    }
    public static void toPlatform() {
        for(Player p: Players.get()) toPlatform(p);
    }
}