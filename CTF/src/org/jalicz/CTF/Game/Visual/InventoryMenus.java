package org.jalicz.CTF.Game.Visual;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jalicz.CTF.OutGameData.DefaultKitManager;
import org.jalicz.CTF.OutGameData.StatsManager;
import org.jalicz.CTF.OutGameData.StatsOnTheStartOfWeek;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.KitManager;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryMenus {

    public static void showTeamMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 27, ItemManager.SELECT_TEAM.getItemMeta().getDisplayName());

        int[] red_slots = {0, 1, 2, 9, 10, 11, 18, 19, 20};
        int[] blue_slots = {6, 7, 8, 15, 16, 17, 24, 25, 26};
        for(int slot: red_slots) menu.setItem(slot, ItemManager.JOIN_RED);
        for(int slot: blue_slots) menu.setItem(slot, ItemManager.JOIN_BLUE);
        menu.setItem(13, ItemManager.CLOSE_MENU);
        if(Teams.get(player) != Team.NONE) menu.setItem(22, ItemManager.LEAVE_TEAM);

        player.openInventory(menu);
    }

    public static void showKitMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 9, ItemManager.SELECT_KIT.getItemMeta().getDisplayName());

        if(KitManager.get(player) != Kit.NONE) switch (KitManager.get(player)) {
            case DEFENDER:          ItemManager.DEFENDER_KIT.addUnsafeEnchantment(Enchantment.DURABILITY, 1); break;
            case FREEZE_IMMUNITY:   ItemManager.FREEZE_IMMUNITY_KIT.addUnsafeEnchantment(Enchantment.DURABILITY, 1); break;
            case RUSHER:            ItemManager.RUSHER_KIT.addUnsafeEnchantment(Enchantment.DURABILITY, 1); break;
            case POWERUP_MASTER:    ItemManager.POWERUP_MASTER_KIT.addUnsafeEnchantment(Enchantment.DURABILITY, 1); break;
        }

        menu.setItem(0, ItemManager.DEFENDER_KIT);
        menu.setItem(1, ItemManager.FREEZE_IMMUNITY_KIT);
        menu.setItem(2, ItemManager.RUSHER_KIT);
        menu.setItem(3, ItemManager.POWERUP_MASTER_KIT);
        menu.setItem(8, ItemManager.CLOSE_MENU);

        if(DefaultKitManager.hasDefaultKit(player.getName())) ItemManager.CHANGE_DEFAULT_KIT.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        menu.setItem(7, ItemManager.CHANGE_DEFAULT_KIT);
        ItemManager.CHANGE_DEFAULT_KIT.removeEnchantment(Enchantment.DURABILITY);

        if(KitManager.get(player) != Kit.NONE) switch (KitManager.get(player)) {
            case DEFENDER:          ItemManager.DEFENDER_KIT.removeEnchantment(Enchantment.DURABILITY);         break;
            case FREEZE_IMMUNITY:   ItemManager.FREEZE_IMMUNITY_KIT.removeEnchantment(Enchantment.DURABILITY);  break;
            case RUSHER:            ItemManager.RUSHER_KIT.removeEnchantment(Enchantment.DURABILITY);           break;
            case POWERUP_MASTER:    ItemManager.POWERUP_MASTER_KIT.removeEnchantment(Enchantment.DURABILITY);   break;
        }

        player.openInventory(menu);
    }

    public static void showSpectatorPlayerMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 36, C.AQUA + "Select Player");
        int i = 0;
        for(Player p: Players.get()) if(Teams.get(p) == Team.RED || Teams.get(p) == Team.BLUE) {
            if(i > 35) break;
            menu.setItem(i, Players.getSkull(p.getName()));
            i++;
        }
        player.openInventory(menu);
    }

    public static void showStatsMenu(Player player) {
        try {
            if(StatsManager.hasStatsFile(player.getName())) {
                Inventory menu = Bukkit.createInventory(player, 45, C.PINK + "My statistics");

                File file = StatsManager.getStatsFile(player.getName());
                if(file == null) throw new Exception("File is null");

                List<String> lines = FileUtils.readLines(file);

                ItemStack weekStats = new ItemStack(Material.GOLD_INGOT), totalStats = new ItemStack(Material.IRON_INGOT);
                ItemMeta weekMeta = weekStats.getItemMeta(), totalMeta = totalStats.getItemMeta();
                ArrayList<String> weekLore = new ArrayList<>(), totalLore = new ArrayList<>();

                weekMeta.setDisplayName(C.GREEN + "This Week");
                totalMeta.setDisplayName(C.GREEN + "Total Statistics");

                for(String line: lines) {
                    String[] data = line.split(": ");
                    totalLore.add(C.GOLD + data[0].toUpperCase() + ": " + C.AQUA + C.BOLD + data[1]);
                }

                if(StatsOnTheStartOfWeek.values.containsKey(player.getName())) {
                    StatsOnTheStartOfWeek oldStats = StatsOnTheStartOfWeek.values.get(player.getName());
                    for(int i=0; i<8; i++) {
                        String[] data = lines.get(i).split(": ");
                        int value = Integer.parseInt(data[1])-oldStats.getByInt(i);
                        weekLore.add(C.GOLD + data[0].toUpperCase() + ": " + C.AQUA + C.BOLD + value);
                    }
                    weekMeta.setLore(weekLore);
                } else weekMeta.setLore(totalLore);

                totalMeta.setLore(totalLore);
                weekStats.setItemMeta(weekMeta);
                totalStats.setItemMeta(totalMeta);

                menu.setItem(20, weekStats);
                menu.setItem(24, totalStats);
                menu.setItem(8, ItemManager.CLOSE_MENU);
                player.openInventory(menu);

            } else {
                Message.player(player, Strings.DATA_MANAGER + C.RED + "Your stats were not found. Sorry fot that :(. Try to play some games or write a message " +
                        "first, it may help.");
                Audio.play(player, Sound.NOTE_BASS_DRUM, 3f, 0.6f);
            }

        } catch (Exception e) {
            Message.player(player, Strings.DATA_MANAGER + C.RED + "Something went wrong :(.");
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to load " + player + "'s stats file! (" + e + ")");
        }
    }
}