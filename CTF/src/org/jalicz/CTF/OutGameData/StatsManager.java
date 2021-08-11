package org.jalicz.CTF.OutGameData;

import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.RecordCategory;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.*;
import java.util.List;

public class StatsManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final String defaultStats =
            "games-played: 0\n" +
            "wins: 0\n" +
            "losses: 0\n" +
            "score: 0\n" +
            "collected-powerups: 0\n" +
            "froze-enemies: 0\n" +
            "unfroze-teammates: 0\n" +
            "wrote-messages: 0";


    public static boolean set(String player, int statistics, int value) {
        try {
            File file = getStatsFile(player);
            if(file == null) throw new Exception("Stats file is null!");

            List<String> lines = FileUtils.readLines(file);
            lines.set(statistics, lines.get(statistics).split(": ")[0] + ": " + value);
            StringBuilder builder = new StringBuilder();
            lines.forEach(line -> builder.append(line).append("\n"));
            FileUtils.write(file, builder.toString());

            if(statistics == 1 || (statistics > 2 && statistics < 6)) {
                int onTheStartOfWeek = (StatsOnTheStartOfWeek.values.containsKey(player)) ? StatsOnTheStartOfWeek.values.get(player).getByInt(statistics) : 0;

                switch (statistics) {
                    case 1:
                        RecordManager.add(player, RecordCategory.TOTAL_WINS, value);
                        RecordManager.add(player, RecordCategory.WINS, value - onTheStartOfWeek);
                        break;

                    case 3:
                        RecordManager.add(player, RecordCategory.TOTAL_SCORE, value);
                        RecordManager.add(player, RecordCategory.SCORE, value - onTheStartOfWeek);
                        break;

                    case 4:
                        RecordManager.add(player, RecordCategory.TOTAL_COLLECTED_POWERUPS, value);
                        RecordManager.add(player, RecordCategory.COLLECTED_POWERUPS, value - onTheStartOfWeek);
                        break;

                    case 5:
                        RecordManager.add(player, RecordCategory.TOTAL_FROZEN_ENEMIES, value);
                        RecordManager.add(player, RecordCategory.FROZEN_ENEMIES, value - onTheStartOfWeek);
                        break;
                }
            }
            return true;

        } catch (Exception e) {
            Message.console(C.RED + "Unable to set " + C.GOLD + player + "'s" + C.RED + " stats [" + statistics + ": " + value + "]! (" + e + ")");
            return false;
        }
    }

    public static void add(String player, int statistics) {
        if(!plugin.getConfig().getBoolean("data-saving.statistics")) return;
        set(player, statistics, get(player, statistics) + 1);
    }

    public static int get(String player, int statistics) {
        try {
            File file = getStatsFile(player);
            if(file == null) throw new Exception("Stats file is null!");
            return Integer.parseInt(FileUtils.readLines(file).get(statistics).split(": ")[1]);

        } catch (Exception e) {
            Message.console(C.RED + "Unable to get " + C.GOLD + player + "'s" + C.RED + " stats! (" + e + ")");
            return 0;
        }
    }

    public static String getString(String player) {
        try {
            if(!hasStatsFile(player)) return "This player has no stats!";

            File file = getStatsFile(player);
            if(file == null) return "NullPointerException :(";

            StringBuilder builder = new StringBuilder();
            for(String line: FileUtils.readLines(file)) builder.append(line).append("\n");
            return builder.toString();
        } catch (Exception e) {
            return "Failed to get " + player + "'s stats! (" + e + ")";
        }
    }

    public static boolean delete(String player) {
        try {
            RecordManager.delete(player);
            if(!hasStatsFile(player)) return true;

            File file = getStatsFile(player);
            if(file == null) return false;
            if(!file.exists()) return true;
            return file.delete();

        } catch (Exception e) {
            return false;
        }
    }

    public static File getStatsFile(String player) {
        try {
            final File file = new File(PlayerDataManager.getDirectory(player).getPath() + "/Stats.txt");
            if(!file.exists()) {
                if(!file.createNewFile()) throw new Exception("An error has occurred while crating " + file.getPath() + "!");

                FileWriter writer = new FileWriter(file);
                writer.write(defaultStats);
                writer.close();
            }
            return file;

        } catch (Exception e) {
            Message.console(C.RED + "Unable to create a statistics file for " + C.GOLD + player + C.RED + "! (" + e + ")");
            return null;
        }
    }

    public static boolean hasStatsFile(String player) {
        if(!PlayerDataManager.hasDirectory(player)) return false;
        return new File(PlayerDataManager.getDirectory(player).getPath() + "/Stats.txt").exists();
    }
}