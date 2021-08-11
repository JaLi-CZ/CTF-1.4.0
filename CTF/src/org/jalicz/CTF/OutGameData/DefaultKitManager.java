package org.jalicz.CTF.OutGameData;

import org.apache.commons.io.FileUtils;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.File;
import java.util.List;

public class DefaultKitManager {

    public static Kit get(String player) {
        try {
            File file = getFile(player);
            if(file == null) throw new Exception("File is null!");

            List<String> lines = FileUtils.readLines(file);
            String content = lines.size() > 0 ? lines.get(0) : "NONE";

            return getKitByString(content);

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An exception has occurred while getting " + player + "'s default kit! (" + e + ")");
            return Kit.NONE;
        }
    }

    public static void set(String player, Kit kit) {
        try {
            File file = getFile(player);
            if(file == null) throw new Exception("File is null!");

            FileUtils.write(file, kit.toString());

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An exception has occurred while setting " + player + "'s default kit! (" + e + ")");
        }
    }

    public static boolean hasDefaultKit(String player) {
        return get(player) != Kit.NONE;
    }

    private static File getFile(String player) {
        try {
            File dir = PlayerDataManager.getDirectory(player);
            File file = new File(dir.getPath() + "/DefaultKit.txt");
            if(!file.exists()) {
                if(!file.createNewFile()) throw new Exception("Unable to create a " + file.getPath() +"!");
                else FileUtils.write(file, "NONE");
            }
            return file;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An exception has occurred while getting " + player + "'s default kit file! (" + e + ")");
            return null;
        }
    }

    private static Kit getKitByString(String s) {
        switch (s.toUpperCase()) {
            case "DEFENDER": return Kit.DEFENDER;
            case "FREEZE_IMMUNITY": return Kit.FREEZE_IMMUNITY;
            case "RUSHER": return Kit.RUSHER;
            case "POWERUP_MASTER": return Kit.POWERUP_MASTER;
            default: return Kit.NONE;
        }
    }
}