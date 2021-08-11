package org.jalicz.CTF.Game.Worlds;

import org.jalicz.CTF.CTFPlugin;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.Enums.WorldType;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WorldUnzipper {

    private static final Plugin plugin = CTFPlugin.plugin;

    public static void unzip(String worldPath, WorldType worldType) {
        try {
            if(!worldPath.endsWith(".zip")) {
                Message.console(Strings.GAME + C.RED + "This file is not of the .zip type! (\"" + worldPath + "\")");
                return;
            }
            File world = new File(worldPath);
            if(!world.exists()) {
                Message.console(Strings.GAME + C.RED + "Failed to load a " + worldType.toString().toLowerCase() + " world! (\"" + worldPath + "\" not exist!)");
                return;
            }

            File outputDirectory = new File(plugin.getDataFolder().getPath() + "/worlds/" + worldType.toString().toLowerCase());
            ZipInputStream input = new ZipInputStream(new FileInputStream(world));

            ZipEntry entry;
            while((entry = input.getNextEntry()) != null) {
                File file = new File(outputDirectory + "/" + entry.getName());

                if(entry.isDirectory()) file.mkdir();
                else {
                    File parent = file.getParentFile();
                    if(!parent.exists()) parent.mkdir();

                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) != -1 && input.available() != 0) output.write(buffer, 0, len);
                    output.close();
                }
                input.closeEntry();
            }
            input.close();
            Message.console(Strings.GAME + C.GREEN + worldType.toString() + " world was extracted successfully!");

        } catch (Exception e) {
            Message.console(Strings.GAME + C.RED + "Failed to unzip \"" + worldPath + "\"! (" + e + ")");
        }
    }
}