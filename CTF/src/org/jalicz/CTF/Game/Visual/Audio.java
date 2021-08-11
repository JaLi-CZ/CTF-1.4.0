package org.jalicz.CTF.Game.Visual;

import org.jalicz.CTF.Game.Data.Players;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Audio {

    public static void play(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    public static void play(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
    public static void play(Sound sound, float volume, float pitch) {
        for(Player p: Players.get()) p.playSound(p.getLocation(), sound, volume, pitch);
    }
}