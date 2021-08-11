package org.jalicz.CTF;

import org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jalicz.CTF.Administration.Commands.CTF;
import org.jalicz.CTF.Administration.Commands.CTFTabCompleter;
import org.jalicz.CTF.OutGameData.ConfigFileRestorer;
import org.jalicz.CTF.OutGameData.RecordManager;
import org.jalicz.CTF.OutGameData.StatsManager;
import org.jalicz.CTF.Game.Data.Countdown;
import org.jalicz.CTF.OutGameData.StatsOnTheStartOfWeek;
import org.jalicz.CTF.Game.EventListener;
import org.jalicz.CTF.Game.Game;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Spectating.Visibility;
import org.jalicz.CTF.Game.Movement.Teleporter;
import org.jalicz.CTF.Game.Visual.*;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Strings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    /**
     * Author: ¯\_(ツ)_/¯
     * Version: 1.4
     * Project started: 18.4.2021
     * Project ended: ???
     *
     * All rights not reserved.
     */

    public static Plugin plugin;
    public static World lobby;

    public Main() {
        plugin = this;
    }


    @Override
    public void onEnable() {
        Message.console(Strings.GAME + C.GREEN + "Enabling CTF plugin " + C.AQUA + "v1.4" + C.GREEN + "!");

        WorldManager.deleteWorlds();
        if(!new File(getDataFolder().getPath() + "/config.yml").exists()) new ConfigFileRestorer();

        WorldManager.loadWorlds();
        Teleporter.toSpawn();
        getCommand("ctf").setExecutor(new CTF());
        getCommand("ctf").setTabCompleter(new CTFTabCompleter());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        ItemManager.setLobbyItems();

        for(Player p: Players.get()) {
            p.setMaxHealth(8);
            p.setAllowFlight(false);
            p.setGameMode(GameMode.ADVENTURE);
            Visibility.showEveryoneTo(p);
        }
        GameScoreboard.update();
        NameTagFormat.update();

        Game.runDayTimeTask();
        Countdown.runTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File sourceDir = new File(plugin.getDataFolder().getPath() + "/data/Players");
                    if(!sourceDir.exists()) if(!sourceDir.mkdirs()) throw new Exception("Source dir at " + sourceDir.getPath() + " not exist and an error has occurred " +
                            "while creating it!");

                    File[] dataDirs = new File(sourceDir.getPath()).listFiles(File::isDirectory);
                    StatsOnTheStartOfWeek.values.clear();
                    if(dataDirs == null) Message.console(Strings.DATA_MANAGER + C.RED + "Unable to save stats on the start of the week! Directories are null. " +
                            "(The possible reason is, if this is running on the first time...)");

                    else for(File dir: dataDirs) {
                        try {
                            File file = StatsManager.getStatsFile(dir.getName());
                            if(file == null) {
                                Message.console("Stats file at " + dir.getPath() + "/Stats.txt does not exist and an error has occurred while creating it!");
                                continue;
                            }
                            List<String> lines = FileUtils.readLines(file);
                            int[] values = new int[8];
                            for(int i = 0; i < 8; i++) values[i] = Integer.parseInt(lines.get(i).split(": ")[1]);
                            StatsOnTheStartOfWeek.values.put(dir.getName(), new StatsOnTheStartOfWeek(values));

                        } catch (Exception e) {
                            Message.console(Strings.DATA_MANAGER + C.RED + "Unable to get a week records from " + dir.getPath() + "! (" + e + ")");
                        }
                    }
                } catch (Exception e) {
                    Message.console(Strings.DATA_MANAGER + C.RED + "Unable to setup a week records! This is pretty important, please try to fix it. (" + e + ")");
                }

                RecordManager.deleteWeekRecords();
                Message.console(Strings.DATA_MANAGER + C.YELLOW + "Week records were deleted!" + C.GRAY + " (this action will happen every week)");
                RecordManager.createFiles();
                RecordDisplay.updateAll();
            }
        }.runTaskTimer(this, 0, 20*3600*24*7);
    }

    @Override
    public void onDisable() {
        Message.console(Strings.GAME + C.PINK + "Deleting worlds...");
        Bukkit.getScheduler().cancelTasks(this);
        for(Player p: Players.get()) p.kickPlayer(Strings.GAME + C.GOLD + "Server is restarting...\n" + C.DARK_GREEN + "Try to rejoin after few minutes.");
        WorldManager.unloadWorlds();
        WorldManager.deleteWorlds();
        Message.console(Strings.GAME + C.RED + "Plugin successfully Disabled!");
    }
}