package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.CTFPlugin;
import org.bukkit.plugin.Plugin;

public class BuildMode {

    private static final Plugin plugin = CTFPlugin.plugin;

    public static boolean isDisabled() {
        return !plugin.getConfig().getBoolean("build-mode");
    }
}