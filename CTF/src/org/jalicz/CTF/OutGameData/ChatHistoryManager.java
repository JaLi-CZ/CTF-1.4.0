package org.jalicz.CTF.OutGameData;

import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import java.io.*;

public class ChatHistoryManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final File
            dataDirectory = new File(plugin.getDataFolder() + "/data"),
            chatHistoryFile = new File(dataDirectory.getPath() + "/ChatHistory.txt");
    private static PrintWriter chatHistoryWriter;

    static {
        try {
            if(!dataDirectory.exists()) if(!dataDirectory.mkdirs()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create a directory for saving data!");
            if(!chatHistoryFile.exists()) if(!chatHistoryFile.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create a file for saving chat-history!");
            chatHistoryWriter = new PrintWriter(new FileWriter(chatHistoryFile, true));
        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An error has occurred while preparing ChatHistoryManager! (" + e + ")");
        }
    }


    public static File getChatHistory(String player) {
        try {
            File file = new File(PlayerDataManager.getDirectory(player).getPath() + "/ChatHistory.txt");
            if(!file.exists()) if(!file.createNewFile()) throw new Exception("Failed to create a chat history file!");
            return file;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create or get a " + C.GOLD + player + "'s" + C.RED + " chat history file! (" + e + ")");
            return null;
        }
    }

    public static boolean hasChatHistory(String player) {
        try {
            return new File(PlayerDataManager.playerDataDirectory.getPath() + "/" + player + "/ChatHistory.txt").exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static File getChatHistory() {
        try {
            if(!chatHistoryFile.exists()) if(!chatHistoryFile.createNewFile()) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to create a file for saving chat-history!");
            return chatHistoryFile;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to get a common chat history file! (" + e + ")");
            return null;
        }
    }

    public static boolean deleteChatHistory() {
        try {
            FileUtils.write(chatHistoryFile, "");
            return true;

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "An exception has occurred while deleting common chat history file! (" + e + ")");
            return false;
        }
    }

    public static void saveMessage(String player, String message) {
        if(!plugin.getConfig().getBoolean("data-saving.chat-history")) return;
        try {
            chatHistoryWriter.println("[" + RealTime.getString() + "] " + player + ": " + message);
            chatHistoryWriter.flush();

            File file = ChatHistoryManager.getChatHistory(player);
            if(file == null) throw new Exception(player + "'s chat history file is null!");

            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println("[" + RealTime.getString() + "] " + player + ": " + message);
            writer.close();

        } catch (Exception e) {
            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to save a message! (" + player + ": " + message + ") - " + e);
        }
    }

    public static boolean delete(String player) {
        try {
            if(!hasChatHistory(player)) return true;

            StringBuilder builder = new StringBuilder();
            for(String line: FileUtils.readLines(chatHistoryFile)) if(!line.split("] ")[1].toLowerCase().startsWith(player.toLowerCase())) builder.append(line).append("\n");

            FileWriter writer = new FileWriter(chatHistoryFile, false);
            writer.write(builder.toString());
            writer.close();

            File file = new File(PlayerDataManager.getDirectory(player).getPath() + "/ChatHistory.txt");
            return file.delete();

        } catch (Exception e) {
            return false;
        }
    }

    public static String getString(String player) {
        try {
            StringBuilder builder = new StringBuilder();
            File file = getChatHistory(player);
            if(file == null) return "File is null!";
            for(String line: FileUtils.readLines(file)) builder.append(line).append("\n");
            return builder.toString();

        } catch (Exception e) {
            return "Failed to load a file! (" + e + ")";
        }
    }
}