package org.jalicz.CTF.Game.Movement;

import org.jalicz.CTF.Game.Data.Players;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectManager {

    public static void add(Player player, PotionEffectType type, int duration, int level) {
        remove(player, type);
        player.addPotionEffect(new PotionEffect(type, duration, level < 0 ? 0 : level-1, false, false));
    }
    public static void addIfAbsent(Player player, PotionEffectType type, int duration, int level) {
        if(!player.hasPotionEffect(type)) player.addPotionEffect(new PotionEffect(type, duration, level < 0 ? 0 : level-1, false, false));
    }
    public static void remove(Player player, PotionEffectType type) {
        player.removePotionEffect(type);
    }
    public static void clear(Player player) {
        for(PotionEffect effect: player.getActivePotionEffects()) remove(player, effect.getType());
    }
    public static void clear() {
        for(Player p: Players.get()) clear(p);
    }
}