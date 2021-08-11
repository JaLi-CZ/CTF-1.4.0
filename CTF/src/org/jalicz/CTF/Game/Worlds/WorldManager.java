package org.jalicz.CTF.Game.Worlds;

import org.apache.commons.io.FileUtils;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.WorldType;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import java.io.File;

public class WorldManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final File worldDirectory = new File(plugin.getDataFolder().getPath() + "/worlds");
    public static World LOBBY, GAME;


    public static void loadWorlds() {
        final String lobbyPath = plugin.getConfig().getString("worlds.lobby.path"), gamePath = plugin.getConfig().getString("worlds.game.path");

        if(!worldDirectory.exists()) if(!worldDirectory.mkdirs()) Message.console(Strings.GAME + C.RED + "Failed to create a world directory! (" + worldDirectory.getPath() + ")");

        if(lobbyPath.endsWith(".zip")) {
            WorldUnzipper.unzip(lobbyPath, WorldType.LOBBY);
            LOBBY = new WorldCreator(worldDirectory + "/lobby").createWorld();
        } else LOBBY = new WorldCreator(lobbyPath).createWorld();

        if(gamePath.endsWith(".zip")) {
            WorldUnzipper.unzip(gamePath, WorldType.GAME);
            GAME = new WorldCreator(worldDirectory + "/game").createWorld();
        } else GAME = new WorldCreator(gamePath).createWorld();

        if(LOBBY == null) Message.console(Strings.GAME + C.RED + "Failed to load the lobby world!");
        if(GAME == null) Message.console(Strings.GAME + C.RED + "Failed to load the game world!");
    }

    public static void unloadWorlds() {
        for(World world: Bukkit.getWorlds()) Bukkit.getServer().unloadWorld(world, false);
    }

    public static void deleteWorlds() {
        try {
            FileUtils.deleteDirectory(worldDirectory);
            Message.console(Strings.GAME + C.PINK + "Worlds were successfully deleted!");
        } catch (Exception e) {
            Message.console(Strings.GAME + C.RED + "Unable to delete the world-directory! (" + e + ")");
        }
    }
}