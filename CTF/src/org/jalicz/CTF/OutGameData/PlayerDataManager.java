package org.jalicz.CTF.OutGameData;

import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.File;

public class PlayerDataManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    public static final File playerDataDirectory = new File(plugin.getDataFolder() + "/data/Players");

    static {
        if(!playerDataDirectory.exists()) if(!playerDataDirectory.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create a directory for saving player data!");
    }

    public static File getDirectory(String player) {
        File dir = new File(playerDataDirectory.getPath() + "/" + player);
        if(!dir.exists()) if(!dir.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create a directory for saving " + C.AQUA + player + "'s" + C.RED + " data!");
        return dir;
    }

    @SuppressWarnings("all")
    public static boolean hasDirectory(String player) {
        try {
            return new File(playerDataDirectory.getPath() + "/" + player).exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static File[] getDirectories() {
        return playerDataDirectory.listFiles(File::isDirectory);
    }
}