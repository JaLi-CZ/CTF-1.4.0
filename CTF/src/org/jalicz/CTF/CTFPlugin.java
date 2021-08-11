package org.jalicz.CTF;

import org.jalicz.CTF.Game.Data.Players;
import org.bukkit.plugin.Plugin;

public class CTFPlugin {

    public static final Plugin plugin = Main.plugin;


    public static boolean reload() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
            Players.updateValues();
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}