package org.jalicz.CTF.OutGameData;

import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jalicz.CTF.Enums.TimeUnit;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PunishManager {

    public static void kick(Player player, String reason) {
        if(reason == null) reason = "";
        boolean simpleText = reason.startsWith("!");
        if(simpleText) reason = reason.substring(1);

        player.kickPlayer(simpleText ? convertTextFormat(reason) : C.RED + "You were kicked by an Administrator!\n" + C.GOLD + "Reason: " + C.YELLOW +
                (reason.equals("") ? "Unspecified" : convertTextFormat(reason)));
    }


    public static void mute(String player, String reason, long value, TimeUnit unit) {
        try {
            File file = getMuteFile(player);
            if(file == null) throw new Exception("File is null!");
            FileUtils.write(file, RealTime.convertToSeconds(value, unit) + "\n" + reason);

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to mute " + player + "! (" + e + ")");
        }
    }

    public static void unmute(String player) {
        try {
            File file = getMuteFile(player);
            if(file == null) throw new Exception("File is null!");
            if(file.exists()) if(!file.delete()) throw new Exception("Unable to delete " + file.getPath() + "!");

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to unmute " + player + "! (" + e + ")");
        }
    }

    public static boolean isMuted(String player) {
        try {
            if(!hasMuteFile(player)) return false;

            File file = getMuteFile(player);
            if(file == null) throw new Exception("File is null!");
            if(!file.exists()) return false;

            boolean muted = Long.parseLong(FileUtils.readLines(file).get(0)) > RealTime.getTotalSeconds();
            if(!muted) unmute(player);
            return muted;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to check if " + player + " is muted! (" + e + ")");
            return false;
        }
    }

    private static String getMuteReason(String player) {
        try {
            File file = getMuteFile(player);
            if(file == null) throw new Exception("File is null!");

            List<String> lines = FileUtils.readLines(file);
            return lines.size() > 1 ? lines.get(1) : "Unspecified";
            
        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to get " + player + "'s mute reason! (" + e + ")");
            return "Unspecified";
        }
    }
    
    public static String getMuteMessage(String player) {
        return Strings.PUNISHMENTS + C.RED + "You were muted! (Time Left: " + RealTime.convertToString(getMuteTimeLeft(player)) + ") " +
                C.GOLD + "Reason: " + C.YELLOW + convertTextFormat(getMuteReason(player));
    }

    private static long getMuteTimeLeft(String player) {
        try {
            if(!isMuted(player)) return 0;
            File file = getMuteFile(player);
            if(file == null) throw new Exception("File is null!");
            
            String duration = FileUtils.readLines(file).get(0);
            return Long.parseLong(duration) - RealTime.getTotalSeconds();
                
        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to get a mute time-left for " + player + "! (" + e + ")");
            return 0;
        }
    }

    private static File getMuteFile(String player) {
        try {
            File file = new File(PlayerDataManager.getDirectory(player).getPath() + "/Mute.txt");
            if(!file.exists()) if(!file.createNewFile()) throw new Exception("Unable to create a file!");
            return file;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to get " + player + "'s mute file! (" + e + ")");
            return null;
        }
    }

    private static boolean hasMuteFile(String player) {
        return new File(PlayerDataManager.getDirectory(player).getPath() + "/Mute.txt").exists();
    }

    public static List<String> getMutedPlayers() {
        List<String> list = new ArrayList<>();
        for(File dir: PlayerDataManager.getDirectories()) if(isMuted(dir.getName())) list.add(C.GOLD + dir.getName() + C.GREEN + " -> " + C.AQUA +
                RealTime.convertToString(getMuteTimeLeft(dir.getName())) + C.GRAY + " (" + getMuteReason(dir.getName()) + C.GRAY + ")");
        return list;
    }


    public static void ban(String player, String reason, long value, TimeUnit unit) {
        try {
            File file = getBanFile(player);
            if(file == null) throw new Exception("File is null!");
            FileUtils.write(file, RealTime.convertToSeconds(value, unit) + "\n" + reason);

            Player target = Players.get(player);
            if(target != null) target.kickPlayer(getBanMessage(player));

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to ban " + player + " for \"" + reason + "\"! (" + e + ")");
        }
    }

    public static void unban(String player) {
        try {
            File file = getBanFile(player);
            if(file == null) throw new Exception("File is null!");
            if(file.exists()) if(!file.delete()) throw new Exception("Unable to delete " + file.getPath() + "!");

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to unban " + player + "! (" + e + ")");
        }
    }

    public static boolean isBanned(String player) {
        try {
            if(!hasBanFile(player)) return false;

            File file = getBanFile(player);
            if(file == null) throw new Exception("File is null!");
            if(!file.exists()) return false;

            boolean banned = Long.parseLong(FileUtils.readLines(file).get(0)) > RealTime.getTotalSeconds();
            if(!banned) unban(player);
            return banned;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to check if " + player + " is banned! (" + e + ")");
            return false;
        }
    }

    private static String getBanReason(String player) {
        try {
            File file = getBanFile(player);
            if(file == null) throw new Exception("File is null!");

            List<String> lines = FileUtils.readLines(file);
            return lines.size() > 1 ? convertTextFormat(lines.get(1)) : "Unspecified";

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to get " + player + "'s ban reason! (" + e + ")");
            return "Unspecified";
        }
    }

    public static String getBanMessage(String player) {
        String reason = getBanReason(player);
        boolean simpleText = reason.startsWith("!");
        if(simpleText) reason = reason.substring(1);

        return simpleText ? convertTextFormat(reason) : C.RED + "You were banned by an Administrator!\n" + C.PURPLE + "Time Left: " + C.PINK +
                RealTime.convertToString(getBanTimeLeft(player)) + "\n" + C.GOLD + "Reason: " + C.YELLOW + convertTextFormat(reason);
    }

    private static long getBanTimeLeft(String player) {
        try {
            if(!isBanned(player)) return 0;
            File file = getBanFile(player);
            if(file == null) throw new Exception("File is null!");

            String duration = FileUtils.readLines(file).get(0);
            return Long.parseLong(duration) - RealTime.getTotalSeconds();

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Failed to get a ban duration for " + player + "! (" + e + ")");
            return 0;
        }
    }

    private static File getBanFile(String player) {
        try {
            File file = new File(PlayerDataManager.getDirectory(player).getPath() + "/Ban.txt");
            if(!file.exists()) if(!file.createNewFile()) throw new Exception("Unable to create a file!");
            return file;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to get " + player + "'s ban file! (" + e + ")");
            return null;
        }
    }

    private static boolean hasBanFile(String player) {
        return new File(PlayerDataManager.getDirectory(player).getPath() + "/Ban.txt").exists();
    }

    public static List<String> getBannedPlayers() {
        List<String> list = new ArrayList<>();
        for(File dir: PlayerDataManager.getDirectories()) if(isBanned(dir.getName())) list.add(C.GOLD + dir.getName() + C.GREEN + " -> " + C.AQUA +
                RealTime.convertToString(getBanTimeLeft(dir.getName())) + C.GRAY + " (" + getBanReason(dir.getName()) + C.GRAY + ")");
        return list;
    }


    private static String convertTextFormat(String s) {
        s = C.translate(s);
        s = s.replaceAll("<br>", "\n");
        return s;
    }
}