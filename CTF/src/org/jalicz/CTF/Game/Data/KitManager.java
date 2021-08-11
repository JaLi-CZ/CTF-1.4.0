package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.GameScoreboard;
import org.jalicz.CTF.Game.Visual.Message;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import java.util.HashMap;

public class KitManager {

    private static final HashMap<Player, Kit> kits = new HashMap<>();


    public static Kit get(Player player) {
        return kits.getOrDefault(player, Kit.NONE);
    }

    public static String getString(Player player) {
        switch (get(player)) {
            case DEFENDER: return "Defender";
            case FREEZE_IMMUNITY: return "Freeze Immunity";
            case RUSHER: return "Rusher";
            case POWERUP_MASTER: return "Powerup Master";
            default: return C.YELLOW + "None";
        }
    }

    public static void set(Player player, Kit kit) {
        kits.put(player, kit);
        GameScoreboard.update(player);
        Audio.play(player, Sound.SUCCESSFUL_HIT, 1, 1.4f);
        Message.player(player, Strings.KIT + C.GRAY + "You equipped " + C.GREEN + C.BOLD + getString(player) + " Kit" + C.GRAY + C.ITALIC + ".");
    }

    public static void remove(Player player) {
        kits.remove(player);
    }

    public static void clear() {
        kits.clear();
    }

    public static void givePowerups() {
        for(Player p: Players.get()) {
            PlayerInventory i = p.getInventory();
            
            switch (KitManager.get(p.getPlayer())) {

                case DEFENDER:
                    Audio.play(p, Sound.SUCCESSFUL_HIT, 1, 0.8f);
                    i.addItem(ItemManager.BOOSTER);
                    i.addItem(ItemManager.PRISON);
                    Message.player(p, Strings.KIT + C.GREEN + "Powerups received!");
                    break;

                case RUSHER:
                    Audio.play(p, Sound.SUCCESSFUL_HIT, 1, 0.8f);
                    i.addItem(ItemManager.KNOCK_BALL);
                    i.addItem(ItemManager.BOOSTER);
                    Message.player(p, Strings.KIT + C.GREEN + "Powerups received!");
                    break;

                case POWERUP_MASTER:
                    Audio.play(p, Sound.SUCCESSFUL_HIT, 1, 0.8f);
                    i.addItem(ItemManager.KNOCK_BALL);
                    i.addItem(ItemManager.BOOSTER);
                    i.addItem(ItemManager.BOMB);
                    i.addItem(ItemManager.PRISON);
                    Message.player(p, Strings.KIT + C.GREEN + "Powerups received!");
            }
        }
    }
}