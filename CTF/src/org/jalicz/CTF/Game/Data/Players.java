package org.jalicz.CTF.Game.Data;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Enums.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.Game.Visual.C;

import java.util.ArrayList;

public class Players {

    private static final Plugin plugin = CTFPlugin.plugin;
    public static int
            MINIMUM = plugin.getConfig().getInt("players.minimum"),
            MAXIMUM = plugin.getConfig().getInt("players.maximum"),
            MAXIMUM_ON_SERVER = Bukkit.getMaxPlayers();


    public static ArrayList<Player> get() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
    public static ArrayList<Player> get(Team team) {
        ArrayList<Player> players = new ArrayList<>();
        for(Player p: get()) if(Teams.get(p) == team) players.add(p);
        return players;
    }
    public static ArrayList<Player> get(Kit kit) {
        ArrayList<Player> players = new ArrayList<>();
        for(Player p: get()) if(KitManager.get(p) == kit) players.add(p);
        return players;
    }
    public static Player get(String name) {
        for(Player p: get()) if(p.getName().equalsIgnoreCase(name)) return p;
        return null;
    }
    public static ItemStack getSkull(String player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(C.GOLD + player);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(C.GRAY + "Click to teleport!");

        meta.setLore(lore);
        skull.setItemMeta(meta);

        return skull;
    }

    public static int count() {
        return get().size();
    }
    public static int count(Team team) {
        return get(team).size();
    }

    public static boolean isEnough() {
        return count() >= MINIMUM;
    }
    public static boolean isMax() {
        return count() >= MAXIMUM;
    }
    public static boolean isFullServer() {
        return count() >= MAXIMUM_ON_SERVER;
    }

    public static void updateValues() {
        MINIMUM = plugin.getConfig().getInt("players.minimum");
        MAXIMUM = plugin.getConfig().getInt("players.maximum");
        MAXIMUM_ON_SERVER = Bukkit.getMaxPlayers();
    }
}