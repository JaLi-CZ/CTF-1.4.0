package org.jalicz.CTF.OutGameData;

import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.File;
import java.io.FileWriter;

public class ConfigFileRestorer {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final String defaultConfigContent =
            "# CTF plugin configuration\n" +
            "# ----------------------------\n" +
            "# players.minimum - Minimal count of players needed to start the game. (waiting: 60s)\n" +
            "# players.maximum - Maximal count of players playing the game. (skip waiting to: 20s)\n" +
            "# ----------------------------\n" +
            "# if the worlds are in .zip format, they must to be named: lobby.zip and game.zip!\n" +
            "# maximum-game-duration: (in seconds) if the limit is reached, the game will be restarted.\n" +
            "# ----------------------------\n" +
            "players:\n" +
            "  minimum: 12\n" +
            "  maximum: 32\n" +
            "worlds:\n" +
            "  lobby:\n" +
            "    path: update/maps/CTF/lobby.zip\n" +
            "    time: 5000\n" +
            "    spawn:\n" +
            "      x: -8.5\n" +
            "      y: 67\n" +
            "      z: -0.5\n" +
            "      velocity-amount: 0.7\n" +
            "  game:\n" +
            "    path: update/maps/CTF/game.zip\n" +
            "    time: 5000\n" +
            "freeze:\n" +
            "  duration: 60\n" +
            "  randomize: 10\n" +
            "kick-messages:\n" +
            "  full-server: '&cServer is full!'\n" +
            "  whitelist: '&cServer is currently whitelisted!'\n" +
            "maximum-game-duration: 900\n" +
            "admins:\n" +
            "- DartCZ\n" +
            "- JaLi_CZ\n" +
            "build-mode: false\n" +
            "data-saving:\n" +
            "  chat-history: true\n" +
            "  statistics: true\n",

    directoryPath = plugin.getDataFolder().getPath();

    public ConfigFileRestorer() {
        File dir = new File(directoryPath);
        if(!dir.exists()) if(!dir.mkdirs()) {
            Message.console(Strings.GAME + C.RED + "Failed to create a directory for config.yml file!");
            return;
        }

        File config = new File(directoryPath + "/config.yml");
        if(!config.exists()) try {
            if(!config.createNewFile()) throw new Exception("Unable to create " + config.getPath() + "!");

            FileWriter writer = new FileWriter(config, false);
            writer.write(defaultConfigContent);
            writer.close();

        } catch (Exception e) {
            Message.console(Strings.GAME + C.RED + "Failed to restore a config.yml file! (" + e + ")");
            return;
        }
        Message.console(Strings.GAME + C.AQUA + "Config file \"" + config.getPath() + "\" was " + C.GREEN + "successfully" + C.AQUA + " restored!");
    }
}