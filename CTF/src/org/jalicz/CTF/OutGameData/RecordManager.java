package org.jalicz.CTF.OutGameData;

import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.RecordCategory;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecordManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final File
            dataDirectory = new File(plugin.getDataFolder() + "/data"),
            recordsDirectory = new File(dataDirectory.getPath() + "/Records"),

            totalRecords = new File(recordsDirectory.getPath() + "/Total"),
            weekRecords = new File(recordsDirectory.getPath() + "/Week"),

            totalWins = new File(totalRecords.getPath() + "/Wins.txt"),
            totalScore = new File(totalRecords.getPath() + "/Score.txt"),
            totalPowerupsCollected = new File(totalRecords.getPath() + "/PowerupsCollected.txt"),
            totalFrozenEnemies = new File(totalRecords.getPath() + "/FrozenEnemies.txt"),

            weekWins = new File(weekRecords.getPath() + "/Wins.txt"),
            weekScore = new File(weekRecords.getPath() + "/Score.txt"),
            weekPowerupsCollected = new File(weekRecords.getPath() + "/PowerupsCollected.txt"),
            weekFrozenEnemies = new File(weekRecords.getPath() + "/FrozenEnemies.txt");

    public static RecordData get(RecordCategory category, int place) {
        try {
            if(place > 5) place = 5;
            else if(place < 1) place = 1;

            File file = getFile(category);
            if(file == null) throw new Exception("File is null!");
            List<String> lines = FileUtils.readLines(file);
            if(lines.size() < place) return new RecordData(C.GRAY + "---", C.GRAY + "0 " + getCategoryString(category), place);
            String[] data = lines.get(place-1).split(": ");

            return new RecordData(C.GREEN + data[0], C.GOLD + data[1] + " " + getCategoryString(category), place);

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to get data of " + place + ". place in category " + category.toString() + "!");
            return new RecordData(C.GRAY + "---", C.GRAY + "0 " + getCategoryString(category), place);
        }
    }

    public static void add(String player, RecordCategory category, int value) {
        try {
            File file = getFile(category);
            if(file == null) throw new Exception("File is null!");
            if(!file.exists()) if(!file.createNewFile()) {
                Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create file at " + file.getPath() + " to save" + player + "'s record data!'");
                return;
            }
            List<String> lines = FileUtils.readLines(file);
            if(lines.size() == 0) {
                FileUtils.write(file, player + ": " + value);
                return;
            }
            if(lines.size() >= 5 && Integer.parseInt(lines.get(4).split(": ")[1]) >= value) return;
            for(int i=0; i<5 && lines.size()>i; i++) if(lines.get(i).toLowerCase().startsWith(player.toLowerCase())) lines.remove(i);

            boolean alreadyWasAdded = false;
            StringBuilder builder = new StringBuilder();
            int o = 0;
            for(int i=0; i<5; i++) {
                if(lines.size() <= o) {
                    if(!alreadyWasAdded) builder.append(player).append(": ").append(value);

                    break;
                }
                if(Integer.parseInt(lines.get(o).split(": ")[1]) < value && !alreadyWasAdded) {
                    builder.append(player).append(": ").append(value).append("\n");
                    alreadyWasAdded = true;
                } else {
                    builder.append(lines.get(o)).append("\n");
                    o++;
                }
            }
            FileUtils.write(file, builder.toString());

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to check and maybe add " + player + " to the record board! (" + e + ")");
        }
    }

    public static void delete(String player) {
        try {
            for(RecordCategory category: Arrays.stream(RecordCategory.values()).limit(8).collect(Collectors.toList())) {
                File file = getFile(category);
                if(file == null) continue;

                List<String> lines = FileUtils.readLines(file);
                StringBuilder builder = new StringBuilder();
                for(String line: lines) if(!line.split(": ")[0].equalsIgnoreCase(player)) builder.append(line).append("\n");
                FileUtils.write(file, builder.toString());
            }
        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to delete possible " + player + "'s records! (" + e + ")");
        }
    }

    public static void deleteWeekRecords() {
        try {
            FileUtils.deleteDirectory(weekRecords);
            if(!weekRecords.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to recreate a world directory after deleting it!");
        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to delete the week records! Please delete it manually, or fix it :(. (" + e + ")");
        }
    }

    private static File getFile(RecordCategory category) {
        switch (category) {
            case WINS: return weekWins;
            case SCORE: return weekScore;
            case COLLECTED_POWERUPS: return weekPowerupsCollected;
            case FROZEN_ENEMIES: return weekFrozenEnemies;

            case TOTAL_WINS: return totalWins;
            case TOTAL_SCORE: return totalScore;
            case TOTAL_COLLECTED_POWERUPS: return totalPowerupsCollected;
            case TOTAL_FROZEN_ENEMIES: return totalFrozenEnemies;
        }
        return null;
    }

    private static String getCategoryString(RecordCategory category) {
        if(category.toString().contains("WINS")) return "Wins";
        if(category.toString().contains("SCORE")) return "Score";
        if(category.toString().contains("COLLECTED")) return "Powerups";
        return "Frozen E.";
    }

    public static void createFiles() {
        try {
            if(!dataDirectory.exists()) if(!dataDirectory.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a directory for saving data!");
            if(!recordsDirectory.exists()) if(!recordsDirectory.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a directory for saving records!");

            if(!totalRecords.exists()) if(!totalRecords.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a directory for saving total-records!");
            if(!weekRecords.exists()) if(!weekRecords.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a directory for saving week-records!");

            if(!totalWins.exists()) if(!totalWins.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving total-wins record!");
            if(!totalScore.exists()) if(!totalScore.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving total-score record!");
            if(!totalPowerupsCollected.exists()) if(!totalPowerupsCollected.createNewFile())
                Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving total-powerups-collected record!");
            if(!totalFrozenEnemies.exists()) if(!totalFrozenEnemies.createNewFile())
                Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving total-frozen-enemies record!");

            if(!weekWins.exists()) if(!weekWins.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving week-wins record!");
            if(!weekScore.exists()) if(!weekScore.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving week-score record!");
            if(!weekPowerupsCollected.exists()) if(!weekPowerupsCollected.createNewFile())
                Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving week-powerups-collected record!");
            if(!weekFrozenEnemies.exists()) if(!weekFrozenEnemies.createNewFile())
                Message.console(Strings.DATA_MANAGER + C.RED + "Failed to create a file for saving week-frozen-enemies record!");

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An error has occurred while creating new files for saving record data! (" + e + ")");
        }
    }
}