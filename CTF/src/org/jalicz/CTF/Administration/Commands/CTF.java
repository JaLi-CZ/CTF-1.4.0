package org.jalicz.CTF.Administration.Commands;

import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.jalicz.CTF.Administration.AdminManager;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.TimeUnit;
import org.jalicz.CTF.OutGameData.*;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Game;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jalicz.CTF.Game.Visual.RankManager;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CTF implements CommandExecutor {

    private final Plugin plugin = CTFPlugin.plugin;
    private final File config_file = new File(plugin.getDataFolder().getPath() + "/config.yml");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(Strings.COMMAND + C.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        if(!AdminManager.isAdmin(player.getName())) {
            Message.player(player, Strings.COMMAND + C.RED + "You're not allowed to do this!");
            return true;
        }
        if(args.length == 0) {
            Message.player(player, Strings.COMMAND + C.GOLD + "Add some arguments! (" + C.YELLOW + "type \"/ctf help\" for help!" + C.GOLD + ")");
            return true;
        }
        final String cmd = args[0].toLowerCase();

        switch (cmd) {
            case "help":
                Message.commandHelpMessage(player);
                break;

            case "admin":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf admins <list:add:remove> [player]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "list":
                        AdminManager.listAdmins(player);
                        return true;

                    case "promote":
                    case "add":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.YELLOW + "You need to specify which player do you want to promote.");
                            return true;
                        }
                        AdminManager.addAdmin(player, args[2]);
                        return true;

                    case "message":
                    case "msg":
                    case "m":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf admins <message> <text>");
                            return true;
                        }
                        StringBuilder message = new StringBuilder().append(Strings.ADMIN_CHAT).append(C.DARK_RED);
                        for(int arg = 2; arg < args.length; arg++) message.append(args[arg]).append(" ");
                        for(Player p : Players.get())
                            if(AdminManager.isAdmin(p.getName())) Message.player(p, message.toString());
                        return true;

                    case "demote":
                    case "remove":
                    case "rm":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.YELLOW + "You need to specify which admin do you want to demote.");
                            return true;
                        }
                        AdminManager.removeAdmin(player, args[2]);
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.YELLOW + "Unknown argument." + C.GOLD + "\nMaybe you mean one of these commands:" + C.AQUA +
                                "\n  /ctf admins list" +
                                "\n  /ctf admins add <player>" +
                                "\n  /ctf admins remove <player>");
                        return true;
                }

            case "broadcast": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf broadcast <message>");
                    return true;
                }
                StringBuilder builder = new StringBuilder();
                for(int i=1; i<args.length; i++) builder.append(args[i].replaceAll("<br>", "\n")).append(" ");
                Message.broadcast(C.translate(builder.toString()));
                return true;
            }

            case "build-mode":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf build-mode <allow:deny>");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "allow":
                    case "enable":
                    case "true":
                    case "on":
                        plugin.getConfig().set("build-mode", true);
                        Message.player(player, Strings.COMMAND + C.RED + "Placing and breaking blocks enabled!");
                        return true;

                    case "deny":
                    case "disable":
                    case "false":
                    case "off":
                        plugin.getConfig().set("build-mode", false);
                        Message.player(player, Strings.COMMAND + C.GREEN + "Placing and breaking blocks disabled!");
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.YELLOW + "Unknown argument. " + C.GOLD + "Usage: /ctf build-mode <allow:deny>");
                        return true;
                }

            case "chat-history":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf chat-history <get:delete> [player]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "get":
                        if(args.length == 2) {
                            try {
                                File file = ChatHistoryManager.getChatHistory();
                                if(file == null) throw new Exception("File is null");
                                List<String> lines = FileUtils.readLines(ChatHistoryManager.getChatHistory());

                                StringBuilder builder = new StringBuilder();
                                for(String line : lines) builder.append(" ").append(line).append("\n");
                                Message.player(player, C.AQUA + "Server chat-history:\n" + C.GOLD + builder.toString());

                            } catch (Exception e) {
                                Message.player(player, Strings.COMMAND + C.RED + "Unable to load a common chat history file! (" + e + ")");
                            }
                        } else
                            Message.player(player, Strings.COMMAND + C.AQUA + args[2] + "'s chat history:\n" + C.PINK + ChatHistoryManager.getString(args[2]));

                        return true;

                    case "delete":
                        if(args.length == 2)
                            Message.player(player, Strings.COMMAND + C.PINK + "Action was successful: " + C.YELLOW + ChatHistoryManager.deleteChatHistory());
                        else
                            Message.player(player, Strings.COMMAND + C.PINK + "Action was successful: " + C.YELLOW + ChatHistoryManager.delete(args[2]));

                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.RED + "Invalid argument! Use \"get\" or \"delete\"!");
                        return true;
                }

            case "config":
                if(args.length == 1) Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf config <location:read:reload>");
                else switch (args[1].toLowerCase()) {

                    case "location":
                    case "loc":
                        Message.player(player, Strings.COMMAND + C.AQUA + "Config file location: " + new File(plugin.getDataFolder().getPath() + "/config.yml").getAbsolutePath());
                        return true;

                    case "read":
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(config_file));
                            String line;
                            Message.player(player, Strings.COMMAND + C.DARK_GREEN + "Config file content:");
                            while ((line = reader.readLine()) != null) Message.player(player, C.DARK_GREEN + line);

                        } catch (Exception exception) {
                            Message.player(player, Strings.COMMAND + C.RED + "Exception has occurred while reading config file! (" + exception + ")");
                        }
                        return true;

                    case "reload":
                    case "load":
                    case "rl":
                        if(CTFPlugin.reload())
                            Message.player(player, Strings.COMMAND + C.GREEN + "Configuration reloaded!");
                        else Message.player(player, Strings.COMMAND + C.RED + "Failed to reload configuration!");
                        return true;

                    case "save":
                    case "sv":
                        plugin.saveConfig();
                        Message.player(player, Strings.COMMAND + C.GREEN + "Configuration saved!");
                        return true;

                    case "get":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf config get <path>");
                            return true;
                        }
                        Object object = plugin.getConfig().get(args[2]);
                        if(object == null) {
                            Message.player(player, Strings.COMMAND + C.RED + "Invalid path! " + C.GOLD + "Usage: /ctf config get <path>");
                            return true;
                        }
                        Message.player(player, Strings.COMMAND + C.PINK + "Result for \"" + C.AQUA + args[2] + C.PINK + "\" > " +
                                C.GREEN + object.toString());
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf config <location:read:reload>");
                        return true;
                }
                break;

            case "console-output-readers":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf console-output-readers <add:remove:list:clear> [player]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "add": {
                        if (args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player!");
                            return true;
                        }
                        Player target = Players.get(args[2]);
                        if (target == null) {
                            Message.player(player, Strings.COMMAND + C.RED + "This player is offline on this server!");
                            return true;
                        }
                        if (Message.consoleOutputReaders.contains(target)) Message.player(player, Strings.COMMAND + C.RED + "This player is already a console output reader!");
                        else {
                            Message.consoleOutputReaders.add(target);
                            Message.player(player, Strings.COMMAND + C.GREEN + target.getName() + " was successfully added to console output readers!");
                        }
                        return true;
                    }

                    case "remove":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player!");
                            return true;
                        }
                        Player target = Players.get(args[2]);
                        if(target == null) {
                            Message.player(player, Strings.COMMAND + C.RED + "This player is offline on this server!");
                            return true;
                        }
                        if(Message.consoleOutputReaders.contains(target)) {
                            Message.consoleOutputReaders.remove(target);
                            Message.player(player, Strings.COMMAND + C.RED + target.getName() + " was successfully removed from the console output readers!");
                        } else Message.player(player, Strings.COMMAND + C.RED + "This player isn't a console output reader already!");
                        return true;

                    case "list":
                        StringBuilder builder = new StringBuilder();
                        for(Player p: Message.consoleOutputReaders) builder.append("  ").append(C.GREEN).append(p.getName()).append(C.GOLD).append(", ");
                        Message.player(player, Strings.COMMAND + C.PINK + "Console output readers: " + builder.toString());
                        return true;

                    case "clear":
                        if(Message.consoleOutputReaders.isEmpty()) Message.player(player, Strings.COMMAND + C.RED + "Currently is nobody console output reader!");
                        else {
                            Message.consoleOutputReaders.clear();
                            Message.player(player, Strings.COMMAND + C.GREEN + "All console output readers were removed!");
                        }
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.RED + "Invalid argument! " + C.GOLD + "Type /ctf help or use a tab shortcut for help!");
                        return true;
                }

            case "default-kit":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf default-kit <player> [kit]");
                    return true;
                }
                if(!PlayerDataManager.hasDirectory(args[1])) {
                    Message.player(player, Strings.COMMAND + C.RED + "Unknown player!");
                    return true;
                }
                if(args.length == 2) {
                    Message.player(player, Strings.COMMAND + C.PINK + args[1] + "'s default kit: " + C.AQUA + DefaultKitManager.get(args[1]));
                    return true;
                }
                for(Kit kit : Kit.values())
                    if(args[2].toUpperCase().equals(kit.toString())) {
                        DefaultKitManager.set(args[1], kit);
                        Message.player(player, Strings.COMMAND + C.PINK + args[1] + "'s default-kit was changed to " + C.AQUA + kit.toString() + C.PINK + "!");
                        return true;
                    }
                Message.player(player, Strings.COMMAND + C.RED + "Unknown kit!");
                return true;

            case "execute":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf execute <command>");
                    return true;
                }
                {
                    StringBuilder builder = new StringBuilder();
                    for(int i = 1; i < args.length; i++) builder.append(args[i]).append(" ");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), builder.toString());
                    Message.player(player, Strings.COMMAND + C.GREEN + "Command \"" + builder.toString() + "\" was successfully executed!");
                    break;
                }

            case "fly":
                if(args.length == 1) {
                    if(player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        Message.player(player, Strings.COMMAND + C.AQUA + "Fly was " + C.RED + "disabled" + C.AQUA + "!");
                    } else {
                        player.setAllowFlight(true);
                        Message.player(player, Strings.COMMAND + C.AQUA + "Fly was " + C.GREEN + "enabled" + C.AQUA + "!");
                    }
                } else {
                    Player target = Players.get(args[1]);
                    if(target == null) {
                        Message.player(player, Strings.COMMAND + C.RED + "This player is offline on this server!");
                        return true;
                    }
                    if(target.getAllowFlight()) {
                        target.setAllowFlight(false);
                        Message.player(player, Strings.COMMAND + C.AQUA + "You " + C.RED + "disabled" + C.AQUA + " fly for " + target.getName() + "!");
                    } else {
                        target.setAllowFlight(true);
                        Message.player(player, Strings.COMMAND + C.AQUA + "You " + C.GREEN + "enabled" + C.AQUA + " fly for " + target.getName() + "!");
                    }
                }
                break;

            case "invsee": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player! " + C.GOLD + "\nUsage: /ctf invsee <player>");
                    return true;
                }
                Player target = Players.get(args[1]);
                if(target == null) {
                    Message.player(player, Strings.COMMAND + C.RED + "Player \"" + C.PINK + args[1] + C.RED + "\" is offline on this server.");
                    return true;
                }
                player.openInventory(target.getInventory());
                break;
            }

            case "ip": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player!");
                    return true;
                }
                Player target = Players.get(args[1]);
                if(target == null) {
                    Message.player(player, Strings.COMMAND + C.RED + "This player is offline on this server!");
                    return true;
                }
                Message.player(player, Strings.COMMAND + C.GREEN + target.getName() + "'s IP is: " + C.RED + target.getAddress().getHostName());

                if(args.length == 3 && args[2].equals("loc")) try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://ip-api.com/line/" + target.getAddress().getHostName() +
                            "?fields=49471").openStream()));
                    if(!reader.readLine().startsWith("success"))
                        throw new Exception("Failed to locate " + target.getName() + "'s geo location!");

                    ArrayList<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) lines.add(line);
                    reader.close();

                    Message.player(player, Strings.COMMAND + C.AQUA + player.getName() + "'s geo location is: " + C.GOLD + lines.get(0) + C.YELLOW +
                            " (" + lines.get(1) + ") " + C.GREEN + "--> " + C.RED + lines.get(4) + " (" + lines.get(5) + ")");

                } catch (Exception e) {
                    Message.player(player, Strings.COMMAND + C.RED + "An error has occurred! (" + e + ")");
                }
                break;
            }

            case "memory-usage":
                Runtime runtime = Runtime.getRuntime();
                final double
                        maxMB = Math.round(runtime.maxMemory() / 1048576. * 100.) / 100.,
                        freeMB = Math.round(runtime.freeMemory() / 1048576. * 100.) / 100.;

                Message.player(player, Strings.COMMAND + C.GOLD + "Current memory usage: " + C.BOLD + C.YELLOW + (maxMB - freeMB) + "MB" + C.GOLD + "/" + C.RED + maxMB + "MB" +
                        C.AQUA + " -> " + C.GREEN + freeMB + "MB free");
                break;

            case "operation-system":
                Message.player(player, C.AQUA + "Host-machine is running on " + C.WHITE + System.getProperty("os.name"));
                break;

            case "stats":
                if(args.length < 3 || (args.length < 5 && args[1].equalsIgnoreCase("set"))) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf stats <get:set:delete> <player> [statistic] [value]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "get":
                        Message.player(player, Strings.COMMAND + C.AQUA + args[2] + "'s stats:\n" + C.PINK + StatsManager.getString(args[2]));
                        return true;

                    case "set":
                        int statistic;
                        switch (args[3].toLowerCase()) {
                            case "games-played":
                                statistic = 0;
                                break;
                            case "wins":
                                statistic = 1;
                                break;
                            case "losses":
                                statistic = 2;
                                break;
                            case "score":
                                statistic = 3;
                                break;
                            case "collected-powerups":
                                statistic = 4;
                                break;
                            case "froze-enemies":
                                statistic = 5;
                                break;
                            case "unfroze-teammates":
                                statistic = 6;
                                break;
                            case "wrote-messages":
                                statistic = 7;
                                break;
                            default:
                                Message.player(player, Strings.COMMAND + C.RED + "Invalid argument!");
                                return true;
                        }
                        int value;
                        try {
                            value = Integer.parseInt(args[4]);
                        } catch (Exception e) {
                            Message.player(player, Strings.COMMAND + C.RED + "Invalid value!");
                            return true;
                        }
                        Message.player(player, Strings.COMMAND + C.PINK + "Action was successful: " + C.YELLOW + (StatsManager.hasStatsFile(args[2]) &&
                                StatsManager.set(args[2], statistic, value)));
                        return true;

                    case "delete":
                        Message.player(player, Strings.COMMAND + C.PINK + "Action was successful: " + C.YELLOW + StatsManager.delete(args[2]));
                        return true;
                }
                break;

            case "sudo": {
                if(args.length < 3) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "\nUsage: /ctf sudo <player> <message>");
                    return true;
                }
                Player target = Players.get(args[1]);
                if(target == null) {
                    Message.player(player, Strings.COMMAND + C.RED + "Player \"" + C.PINK + args[1] + C.RED + "\" is offline on this server.");
                    return true;
                }
                StringBuilder message = new StringBuilder();
                for(int i = 2; i < args.length; i++) message.append(args[i]).append(" ");
                target.chat(message.toString());
                break;
            }

            case "players": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf players <min:max> [value]");
                    return true;
                }
                String type = args[1].toLowerCase();
                if(type.equals("list")) {
                    StringBuilder builder = new StringBuilder();
                    for(Player p: Players.get()) {
                        Team team = Teams.get(p);
                        String str = RankManager.getPrefix(p, true) + C.WHITE + p.getName() + C.PINK + " - " + C.getByTeam(team) + team.toString() + "\n";
                        builder.append(str);
                    }
                    Message.player(player, C.BLUE + "Online players on this server:\n" + builder.toString());
                    return true;

                } else if(type.equals("offline-list")) {
                    StringBuilder builder = new StringBuilder();
                    File[] dirs = PlayerDataManager.getDirectories();
                    for(File dir: dirs) {
                        String name = dir.getName(), color;
                        if(name.charAt(0) > 115) color = C.PINK;
                        else if(name.charAt(0) > 100) color = C.AQUA;
                        else if(name.charAt(0) > 90) color = C.DARK_AQUA;
                        else if(name.charAt(0) > 80) color = C.DARK_GREEN;
                        else if(name.charAt(0) > 70) color = C.GREEN;
                        else if(name.charAt(0) > 60) color = C.YELLOW;
                        else if(name.charAt(0) > 50) color = C.GOLD;
                        else if(name.charAt(0) > 40) color = C.RED;
                        else color = C.DARK_RED;
                        builder.append(color).append(dir.getName()).append(C.GRAY).append(", ");
                    }
                    Message.player(player, C.GREEN + "All players that were on this server " + C.PINK + "(Total " + dirs.length + ")" + C.GREEN + ":\n" + builder.toString()
                            + C.PINK + " (Total " + dirs.length + ")");
                    return true;
                }
                if(!type.equals("min") && !type.equals("max")) {
                    Message.player(player, Strings.COMMAND + C.RED + "Invalid argument! " + C.GOLD + "Usage: /ctf players <min:max> [value]");
                    return true;
                }
                if(args.length == 2) {
                    Message.player(player, Strings.COMMAND + C.AQUA + "The " + type + "imum of players is: " + C.GOLD +
                            plugin.getConfig().getInt("players." + type + "imum") + C.AQUA + "!");
                    return true;
                }
                int value;
                try {
                    value = Integer.parseInt(args[2]);
                    plugin.getConfig().set("players." + type + "imum", value);
                    Players.updateValues();
                    Message.player(player, Strings.COMMAND + C.GREEN + "The " + type + "imum of players was successfully changed to " + value + "!");
                } catch (Exception e) {
                    Message.player(player, Strings.COMMAND + C.RED + "Unable to change the " + type + "imum of players! Invalid value -> " + args[2] + "! (" + e + ")");
                }
                break;
            }

            case "powerup": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf powerup <knockball:booster:bomb:prison> [amount] [player]");
                    return true;
                }
                ItemStack powerup;
                switch (args[1].toLowerCase()) {
                    case "knockball":
                    case "kb":
                    case "ball":
                        powerup = ItemManager.KNOCK_BALL;
                        break;

                    case "booster":
                    case "boost":
                        powerup = ItemManager.BOOSTER;
                        break;

                    case "bomb":
                    case "tnt":
                        powerup = ItemManager.BOMB;
                        break;

                    case "prison":
                    case "trap":
                        powerup = ItemManager.PRISON;
                        break;

                    default:
                        Message.player(player, Strings.COMMAND + C.RED + "Unknown powerup!\n" + C.PINK +
                                "Usage: /ctf powerup <knockball:booster:bomb:prison> [amount]");
                        return true;
                }
                if(args.length == 2) {
                    player.getInventory().addItem(powerup);
                    Message.player(player, Strings.COMMAND + powerup.getItemMeta().getDisplayName() + C.GREEN + " was successfully added to your inventory!");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception exception) {
                    Message.player(player, Strings.COMMAND + C.RED + "Invalid value! " + C.GREEN + "/ctf " + args[0] + " " + args[1] + C.RED + " " + args[2]);
                    return true;
                }
                if(amount > 1000) {
                    Message.player(player, Strings.COMMAND + C.RED + "Maximum value is 1000! " + C.AQUA + amount + " was changed to 1000.");
                    amount = 1000;
                }
                if(args.length == 3) {
                    for(int i = 0; i < amount; i++) player.getInventory().addItem(powerup);
                    Message.player(player, Strings.COMMAND + powerup.getItemMeta().getDisplayName() + C.GREEN + " was successfully added to your inventory " + amount + "x times!");
                    return true;
                }
                Player target = Players.get(args[3]);
                if(target == null) {
                    Message.player(player, Strings.COMMAND + C.RED + "Player \"" + C.PINK + args[1] + C.RED + "\" is offline on this server.");
                    return true;
                }
                for(int i = 0; i < amount; i++) target.getInventory().addItem(powerup);
                if(player.equals(target))
                    Message.player(player, Strings.COMMAND + powerup.getItemMeta().getDisplayName() + C.GREEN + " was successfully added to your inventory " +
                            amount + "x times!");
                else
                    Message.player(player, Strings.COMMAND + powerup.getItemMeta().getDisplayName() + C.GREEN + " was successfully added to " + target.getName() + "'s inventory " +
                            amount + "x times!");
                break;
            }

            case "punishment":
                if(args.length < 3 && !args[1].toLowerCase().endsWith("list") || (args.length < 5 && (args[1].equalsIgnoreCase("mute") ||
                        args[1].equalsIgnoreCase("ban")))) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Use tab shortcut or /ctf help for help.");
                    return true;
                }
                StringBuilder builder = new StringBuilder();
                switch (args[1].toLowerCase()) {
                    case "kick":
                        Player target = Players.get(args[2]);
                        if(target == null) {
                            Message.player(player, Strings.COMMAND + C.RED + "This player is offline on this server!");
                            return true;
                        }

                        for(int i=3; i<args.length; i++) builder.append(args[i]).append(" ");

                        PunishManager.kick(target, args.length > 3 ? builder.toString() : "");
                        Message.player(player, Strings.COMMAND + C.PINK + target + " was kicked for " +  C.YELLOW + (args.length > 3 ? builder.toString() : "Unspecified") + C.PINK + "!");
                        return true;

                    case "mute":
                        if(!PunishManager.isMuted(args[2])) {
                            long value;
                            try {
                                value = Long.parseLong(args[3]);
                            } catch (Exception e) {
                                Message.player(player, Strings.COMMAND + C.RED + "Invalid value! It can be number between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE + "!");
                                return true;
                            }
                            TimeUnit unit = null;
                            for(TimeUnit tu : TimeUnit.values()) if(args[4].equalsIgnoreCase(tu.toString())) unit = tu;
                            if(unit == null) {
                                Message.player(player, Strings.COMMAND + C.RED + "Invalid time unit! You can use tab for help.");
                                return true;
                            }

                            for(int i=5; i<args.length; i++) builder.append(args[i]).append(" ");

                            PunishManager.mute(args[2], args.length > 5 ? builder.toString():"", value, unit);
                            Message.player(player, Strings.COMMAND + C.PINK + args[2] + " was muted for " + C.YELLOW +
                                    (args.length > 5 ? builder.toString():"Unspecified"+"") + C.PINK + "!");

                        } else Message.player(player, Strings.COMMAND + C.YELLOW + "This player is already muted!");
                        return true;

                    case "ban":
                        if(!PunishManager.isBanned(args[2])) {
                            long value;
                            try {
                                value = Long.parseLong(args[3]);
                            } catch (Exception e) {
                                Message.player(player, Strings.COMMAND + C.RED + "Invalid value! It can be number between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE + "!");
                                return true;
                            }
                            TimeUnit unit = null;
                            for(TimeUnit tu : TimeUnit.values()) if(args[4].equalsIgnoreCase(tu.toString())) unit = tu;
                            if(unit == null) {
                                Message.player(player, Strings.COMMAND + C.RED + "Invalid time unit! You can use tab shortcut or /ctf help for help.");
                                return true;
                            }

                            for(int i = 5; i < args.length; i++) builder.append(args[i]).append(" ");

                            PunishManager.ban(args[2], args.length > 5 ? builder.toString() : "" + "", value, unit);
                            Message.player(player, Strings.COMMAND + C.PINK + args[2] + " was banned for " + C.YELLOW +
                                    (args.length > 5 ? builder.toString():"Unspecified") + C.PINK + "!");

                        } else Message.player(player, Strings.COMMAND + C.YELLOW + "This player is already banned!");
                        return true;

                    case "unmute":
                        if(PunishManager.isMuted(args[2])) {
                            PunishManager.unmute(args[2]);
                            Message.player(player, Strings.COMMAND + C.AQUA + args[2] + " was unmuted!");
                        } else Message.player(player, Strings.COMMAND + C.YELLOW + args[2] + " isn't muted already!");
                        return true;

                    case "unban":
                        if(PunishManager.isBanned(args[2])) {
                            PunishManager.unban(args[2]);
                            Message.player(player, Strings.COMMAND + C.AQUA + args[2] + " was unbanned!");
                        } else Message.player(player, Strings.COMMAND + C.YELLOW + args[2] + " isn't banned already!");
                        return true;

                    case "mute-list":
                        for(String muted: PunishManager.getMutedPlayers()) builder.append(muted).append("\n");
                        Message.player(player, Strings.DATA_MANAGER + C.PINK + "List of muted players:\n" + builder.toString());
                        return true;

                    case "ban-list":
                        for(String banned: PunishManager.getBannedPlayers()) builder.append(banned).append("\n");
                        Message.player(player, Strings.DATA_MANAGER + C.PINK + "List of banned players:\n" + builder.toString());
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.RED + "Invalid argument! " + C.AQUA + "Use tab shortcut or /ctf help for help.");
                        return true;
                }

            case "local-time":
                Message.player(player, Strings.COMMAND + C.GREEN + "Current local time -> " + C.GOLD + RealTime.getString());
                break;

            case "time": {
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument(s)! " + C.GOLD + "Usage: /ctf time <lobby:game> [value]");
                    return true;
                }
                if(!args[1].equalsIgnoreCase("lobby") && !args[1].equalsIgnoreCase("game")) {
                    Message.player(player, Strings.COMMAND + C.RED + "Invalid world! " + C.GOLD + "Usage: /ctf time <lobby:game> [value]");
                    return true;
                }
                World world = (args[1].equalsIgnoreCase("lobby")) ? WorldManager.LOBBY : WorldManager.GAME;
                if(args.length == 2) {
                    Message.player(player, Strings.COMMAND + C.AQUA + "Current time in " + args[1] + " world is: " + C.PINK + world.getFullTime());
                    return true;
                }
                int value;
                try {
                    value = Integer.parseInt(args[2]);
                    plugin.getConfig().set("worlds." + args[1].toLowerCase() + ".time", value);
                    Message.player(player, Strings.COMMAND + C.GREEN + "The time of " + args[1] + " world was successfully changed to " + value + "!");
                } catch (Exception e) {
                    Message.player(player, Strings.COMMAND + C.RED + "Invalid value! (" + e + ")");
                }
                break;
            }

            case "restart":
                Game.restart();
                Message.player(player, Strings.COMMAND + C.GREEN + "Game successfully restarted!");
                break;

            case "whitelist":
                if(args.length == 1) {
                    Message.player(player, Strings.COMMAND + C.RED + "Missing argument! " + C.GOLD + "Usage: /ctf whitelist <on:off:list:reload:add:remove> [player]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "on":
                        if(Bukkit.hasWhitelist()) {
                            Message.player(player, Strings.COMMAND + C.YELLOW + "This server is already whitelisted!");
                            return true;
                        }
                        Bukkit.setWhitelist(true);
                        Message.player(player, Strings.COMMAND + C.RED + "Whitelist turned on!");
                        return true;

                    case "off":
                        if(!Bukkit.hasWhitelist()) {
                            Message.player(player, Strings.COMMAND + C.YELLOW + "This server is already public!");
                            return true;
                        }
                        Bukkit.setWhitelist(false);
                        Message.player(player, Strings.COMMAND + C.GREEN + "Whitelist turned off!");
                        return true;

                    case "list":
                        Message.player(player, Strings.COMMAND + C.DARK_GREEN + "There are totally " + Bukkit.getWhitelistedPlayers().size() + " whitelisted players:");
                        int i = 0;
                        for(OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
                            i++;
                            Message.player(player, C.WHITE + i + ". " + C.GREEN + p.getName());
                        }
                        Message.player(player, C.GREEN + "------------------");
                        return true;

                    case "reload":
                        Bukkit.reloadWhitelist();
                        Message.player(player, Strings.COMMAND + C.GREEN + "Whitelist reloaded!");
                        return true;

                    case "add":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player! " + C.GOLD + "Usage: /ctf whitelist add <player>");
                            return true;
                        }
                        {
                            OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                            if(p == null) {
                                Message.player(player, Strings.COMMAND + C.RED + "Player not found!");
                                return true;
                            }
                            if(p.isWhitelisted()) {
                                Message.player(player, Strings.COMMAND + C.YELLOW + "This player is already whitelisted!");
                                return true;
                            }
                            p.setWhitelisted(true);
                            Message.player(player, Strings.COMMAND + C.GREEN + "You added " + p.getName() + " on whitelist!");
                            return true;
                        }

                    case "remove":
                        if(args.length == 2) {
                            Message.player(player, Strings.COMMAND + C.RED + "You must to specify a player! " + C.GOLD + "Usage: /ctf whitelist remove <player>");
                            return true;
                        }
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
                        if(p == null) {
                            Message.player(player, Strings.COMMAND + C.RED + "Player not found!");
                            return true;
                        }
                        if(!p.isWhitelisted()) {
                            Message.player(player, Strings.COMMAND + C.YELLOW + "This player isn't whitelisted already!");
                            return true;
                        }
                        p.setWhitelisted(false);
                        Message.player(player, Strings.COMMAND + C.RED + "You removed " + p.getName() + " from whitelist!");
                        return true;

                    default:
                        Message.player(player, Strings.COMMAND + C.YELLOW + "Unknown argument. " + C.GOLD + "Usage: Usage: /ctf whitelist <on:off:list:reload:add:remove> [player]");
                }
                break;

            default:
                Message.player(player, Strings.COMMAND + C.YELLOW + "Unknown argument. " + C.GOLD + "Type \"/ctf help\" or use tab shortcut for help!");
                break;
        }
        return true;
    }
}