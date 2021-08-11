package org.jalicz.CTF.Game.Data;

import org.bukkit.inventory.ItemStack;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.OutGameData.StatsManager;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Enums.Zone;
import org.jalicz.CTF.Game.Movement.EffectManager;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Movement.Teleporter;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import org.jalicz.CTF.Game.Visual.NameTagFormat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Frozen {

    private static final HashMap<Player, Integer> frozen = new HashMap<>();
    public static final Plugin plugin = CTFPlugin.plugin;
    private static final Random random = new Random();

    public static boolean is(Player player) {
        return frozen.containsKey(player);
    }
    public static void add(Player player, int sec) {
        frozen.remove(player);
        frozen.put(player, sec);
    }
    public static void remove(Player player) {
        frozen.remove(player);
    }
    public static void clear() {
        frozen.clear();
    }

    public static int getDuration(Player player) {
        return frozen.getOrDefault(player, 0);
    }

    public static void tryFreeze(Player attacker, Player target) {
        if(Teams.get(attacker) == Teams.get(target)) {
            Message.player(attacker, Strings.GAME + C.RED + "You cannot freeze your teammates!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        if(is(target)) {
            Message.player(attacker, Strings.GAME + C.YELLOW + "This enemy is already frozen!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        if(!ZoneManager.isOnPlatform(attacker) || ZoneManager.isOnPlatform(target)) {
            Message.player(attacker, Strings.GAME + C.RED + "You cannot freeze enemies on their own platform!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        if(ZoneManager.get(target) == Zone.SAFE_ZONE) {
            Message.player(attacker, Strings.GAME + C.RED + "Enemies are protected in their safe zone!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        StatsManager.add(attacker.getName(), Statistics.FROZE_ENEMIES);

        if(KitManager.get(target) == Kit.FREEZE_IMMUNITY) {
            EffectManager.add(attacker, PotionEffectType.SLOW, 100, 2);
            EffectManager.add(attacker, PotionEffectType.BLINDNESS, 100, 1);
            Message.player(target, Strings.KIT + C.GREEN + "Blindness and Slowness II. added to " + attacker.getName() + " for 5s!");
            Message.player(attacker, Strings.GAME + C.RED + "You received Blindness and Slowness II. for 5s!");

            if(random.nextInt(8) == 0) {
                ItemManager.setGameItems(target);
                Teleporter.toPlatform(target);
                NameTagFormat.update(target);

                Audio.play(target, Sound.LEVEL_UP, 1, 1.2f);
                Message.player(target, Strings.KIT + C.GREEN + "15% chance successful -> " + C.GOLD + C.BOLD + "You were instantly unfrozen!");
                Message.title(target, "", C.GREEN + "You were unfrozen!", 5, 30, 10);
                Audio.play(attacker, Sound.CLICK, 1, 1.2f);
                Message.player(attacker, Strings.GAME + C.GREEN + "You froze " + target.getName() + " for " + C.RED + "0 seconds" + C.GREEN + "!" +
                        C.YELLOW + "\n (he has a FreezeImmunity Kit -> 15% chance to be unfrozen instantly)");
                Message.actionBar(attacker, C.GREEN + C.BOLD + "+ " + C.AQUA + "You froze " + target.getName() + " for " + C.RED + "0 seconds" + C.AQUA + "!");
                return;
            }
        }
        ItemManager.clearItems(target);
        int duration = createDuration(attacker, target);
        add(target, duration);
        NameTagFormat.update(target);

        Audio.play(attacker, Sound.SUCCESSFUL_HIT, 1, 1.1f);
        Message.player(attacker, Strings.GAME + C.GREEN + "You froze " + target.getName() + " for " + getDuration(target) + " seconds!");
        Audio.play(target, Sound.CLICK, 1, 0.7f);
        Message.player(target, Strings.GAME + C.RED + "You were frozen by " + attacker.getName() + " for " + getDuration(target) + " seconds!");
        Message.title(target, C.RED + "You were frozen!", C.GOLD + "By " + attacker.getName() + " for " + getDuration(target) + " seconds!", 3, 30, 15);
        Message.actionBar(attacker, C.GREEN + C.BOLD + "+ " + C.AQUA + "You froze " + target.getName() + " for " + C.GREEN + duration + " seconds" + C.AQUA + "!");
        Message.actionBar(target, C.DARK_RED + C.BOLD + "- " + C.RED + "You were frozen by " + C.GOLD + attacker.getName() + C.RED + " for " +
                C.DARK_RED + duration + " seconds" + C.RED + "!");

        if(KitManager.get(attacker) == Kit.DEFENDER) {
            EffectManager.add(attacker, PotionEffectType.SPEED, 160, 3);
            Message.player(attacker, Strings.KIT + C.GREEN + "You received Speed III. for 8s!");

            if(random.nextBoolean()) {
                ItemStack powerup = ItemManager.getRandomPowerup();
                if(attacker.getInventory().contains(powerup) || attacker.getItemOnCursor().equals(powerup)) return;
                attacker.getInventory().addItem(powerup);

                Message.player(attacker, Strings.KIT + C.GREEN + "50% chance successful -> You received " +
                        powerup.getItemMeta().getDisplayName() + C.GREEN + "!");
            }
        }
    }
    public static void tryUnfreeze(Player attacker, Player target) {
        if(Teams.get(attacker) != Teams.get(target)) {
            Message.player(attacker, Strings.GAME + C.RED + "You cannot unfreeze your enemies!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        if(!is(target)) {
            Message.player(attacker, Strings.GAME + C.YELLOW + "This teammate is already unfrozen!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return;
        }
        StatsManager.add(attacker.getName(), Statistics.UNFROZE_TEAMMATES);

        Audio.play(attacker, Sound.SUCCESSFUL_HIT, 1, 1.1f);
        Message.player(attacker, Strings.GAME + C.GREEN + "You unfroze " + target.getName() + " and saved him " + getDuration(target) + " seconds!");
        Audio.play(target, Sound.LEVEL_UP, 1, 0.8f);
        Message.player(target, Strings.GAME + C.GREEN + "You were unfrozen by " + attacker.getName() + " - " + getDuration(target) + " seconds saved!");
        Message.title(target, C.GREEN + "You were unfrozen!", C.GOLD + "By " + attacker.getName() + " - " + getDuration(target) + " " +
                "seconds saved!", 0, 30, 15);
        Message.actionBar(attacker, C.GREEN + C.BOLD + "+ " + C.GOLD + "You unfroze " + C.GREEN + target.getName() + C.GOLD + " and saved him " + C.AQUA +
                getDuration(target) + " seconds" + C.GOLD + "!");
        Message.actionBar(target, C.GREEN + C.BOLD + "+ " + C.GOLD + "You were unfrozen by " + C.GREEN + attacker.getName() + C.GOLD + "! (" + C.AQUA +
                getDuration(target) + " seconds " + C.GOLD + "Saved)");

        ItemManager.setGameItems(target);
        Teleporter.toPlatform(target);
        remove(target);
        Message.xpBar(target, 0);
        NameTagFormat.update(target);

        if(KitManager.get(attacker) == Kit.FREEZE_IMMUNITY && random.nextInt(4) != 0) {
            ItemStack powerup = ItemManager.getRandomPowerup();
            if(attacker.getInventory().contains(powerup) || attacker.getItemOnCursor().equals(powerup)) return;
            attacker.getInventory().addItem(powerup);

            Message.player(attacker, Strings.KIT + C.GREEN + "75% chance successful -> You received " +
                    powerup.getItemMeta().getDisplayName() + C.GREEN + "!");
        }
    }

    private static int createDuration(Player attacker, Player target) {
        int average = plugin.getConfig().getInt("freeze.duration"),
            randomValue = random.nextInt(plugin.getConfig().getInt("freeze.randomize")+1);
        if(random.nextBoolean()) randomValue *= -1;

        double duration = average+randomValue;

        if(KitManager.get(attacker) == Kit.DEFENDER) duration *= 1.7;
        if(KitManager.get(target) == Kit.FREEZE_IMMUNITY) duration /= 2;
        else if(KitManager.get(target) == Kit.RUSHER) duration *= 0.8;

        return (int) duration;
    }

    public static void subtractSecond() {
        if(!frozen.isEmpty()) {
            ArrayList<Player> thenRemove = new ArrayList<>();

            frozen.forEach(((p, sec) -> {
                sec--;
                if(sec < 1) {
                    Message.player(p, Strings.GAME + C.GREEN + "You were unfrozen!");
                    Message.title(p, C.GOLD + "Your time is up!", C.GREEN + "You were unfrozen!", 0, 30, 15);
                    Audio.play(p, Sound.LEVEL_UP, 1, 0.8f);
                    Teleporter.toPlatform(p);
                    ItemManager.setGameItems(p);
                    thenRemove.add(p);

                } else frozen.put(p, sec);

                Message.xpBar(p, sec);
                NameTagFormat.update(p);
            }));
            thenRemove.forEach(p -> {
                remove(p);
                Message.xpBar(p, 0);
                NameTagFormat.update(p);
            });
        }
    }
}