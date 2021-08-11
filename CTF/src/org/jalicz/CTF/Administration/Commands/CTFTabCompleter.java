package org.jalicz.CTF.Administration.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jalicz.CTF.Administration.AdminManager;
import org.jalicz.CTF.Enums.Kit;
import org.jalicz.CTF.Game.Data.Players;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CTFTabCompleter implements TabCompleter {

    private final List<String> cmds = new ArrayList<>(); {
        cmds.add("help");
        cmds.add("admin");
        cmds.add("broadcast");
        cmds.add("build-mode");
        cmds.add("chat-history");
        cmds.add("config");
        cmds.add("console-output-readers");
        cmds.add("default-kit");
        cmds.add("execute");
        cmds.add("fly");
        cmds.add("invsee");
        cmds.add("ip");
        cmds.add("local-time");
        cmds.add("memory-usage");
        cmds.add("operation-system");
        cmds.add("players");
        cmds.add("powerup");
        cmds.add("punishment");
        cmds.add("stats");
        cmds.add("sudo");
        cmds.add("time");
        cmds.add("restart");
        cmds.add("whitelist");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> result = new ArrayList<>();

        if(!(sender instanceof Player)) return result;
        Player player = (Player) sender;

        if(!AdminManager.isAdmin(player.getName())) return result;

        if(args.length == 0) return cmds;
        String cmd = args[0].toLowerCase();

        if(args.length == 1) {
            for(String c: cmds) if(c.equalsIgnoreCase(args[0])) return result;
            result = cmds.stream().filter(c -> c.startsWith(cmd)).collect(Collectors.toList());
            return (result.size() == 0) ? cmds : result;
        }
        else if(args.length == 2) {
            if(cmd.equals("help") || cmd.equals("local-time") || cmd.equals("restart")) return result;
            switch (cmd) {
                case "admin":
                    result.add("add"); result.add("remove");
                    result.add("list"); result.add("message");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "build-mode":
                    result.add("allow"); result.add("deny");
                    if(args[1].toLowerCase().startsWith("a")) result.remove("deny");
                    else if(args[1].toLowerCase().startsWith("d")) result.remove("allow");
                    return result;

                case "chat-history":
                    result.add("get"); result.add("delete");
                    if(args[1].toLowerCase().startsWith("d")) result.remove("get");
                    else if(args[1].toLowerCase().startsWith("g")) result.remove("delete");
                    return result;

                case "config":
                    result.add("save"); result.add("reload");
                    result.add("read"); result.add("location");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "console-output-readers":
                    result.add("add"); result.add("remove");
                    result.add("list"); result.add("clear");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "players":
                    result.add("min"); result.add("max");
                    result.add("list"); result.add("offline-list");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "powerup":
                    result.add("bomb"); result.add("booster");
                    result.add("knockball"); result.add("prison");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "punishment":
                    result.add("kick"); result.add("mute");
                    result.add("ban"); result.add("unmute");
                    result.add("unban"); result.add("mute-list");
                    result.add("ban-list");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "stats":
                    result.add("get"); result.add("set");
                    result.add("delete");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());

                case "time":
                    result.add("lobby"); result.add("game");
                    if(args[1].toLowerCase().startsWith("l")) result.remove("game");
                    else if(args[1].toLowerCase().startsWith("g")) result.remove("lobby");
                    return result;

                case "whitelist":
                    result.add("on"); result.add("off");
                    result.add("add"); result.add("remove");
                    result.add("list"); result.add("reload");
                    return (result.stream().noneMatch(s -> s.startsWith(args[1].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            }
        }
        else if(cmd.equals("admin") && args[1].equalsIgnoreCase("remove")) return AdminManager.getAdminList();
        else if(cmd.equals("default-kit")) {
            if(args.length == 3) {
                for(Kit kit: Kit.values()) result.add(kit.toString());
                return (result.stream().noneMatch(s -> s.startsWith(args[2].toUpperCase()))) ? result :
                        result.stream().filter(s -> s.startsWith(args[2].toUpperCase())).collect(Collectors.toList());
            }
        }
        else if(cmd.equals("punishment")) {
            if(args.length > 3) {
                if(args[1].equalsIgnoreCase("mute") || args[1].equalsIgnoreCase("ban")) {
                    if(args.length != 5) return result;
                    result.add("seconds");
                    result.add("minutes");
                    result.add("hours");
                    result.add("days");
                    result.add("weeks");
                    result.add("months");
                    result.add("years");
                    return (result.stream().noneMatch(s -> s.startsWith(args[4].toLowerCase()))) ? result :
                            result.stream().filter(s -> s.startsWith(args[4].toLowerCase())).collect(Collectors.toList());
                }
            }
        }
        else if(cmd.equals("whitelist") && args[1].equalsIgnoreCase("remove")) {
            List<String> players = new ArrayList<>();
            Bukkit.getWhitelistedPlayers().forEach(p -> players.add(p.getName()));
            return players;

        } else if(cmd.equals("stats") && args[1].equalsIgnoreCase("set")) {
            if(args.length == 4) {
                result.add("games-played"); result.add("wins");
                result.add("losses"); result.add("score");
                result.add("collected-powerups"); result.add("froze-enemies");
                result.add("unfroze-teammates"); result.add("wrote-messages");
                return (result.stream().noneMatch(s -> s.startsWith(args[3].toLowerCase()))) ? result :
                        result.stream().filter(s -> s.startsWith(args[3].toLowerCase())).collect(Collectors.toList());
            }
        }
        else if(cmd.equals("help") || cmd.equals("local-time") || cmd.equals("restart") || (cmd.equals("admin") && (args[1].equalsIgnoreCase("list") ||
                args.length > 3)) || cmd.equals("build-mode") || cmd.equals("config") || cmd.equals("fly") || cmd.equals("invsee") || cmd.equals("ip") ||
                cmd.equals("players") || (cmd.equals("powerup") && args.length > 4) || cmd.equals("sudo") ||
                cmd.equals("time") || (cmd.equals("whitelist") && (args.length > 3 && !args[1].equalsIgnoreCase("add") &&
                !args[1].equalsIgnoreCase("remove"))) || cmd.equals("memory-usage") || cmd.equals("operation-system")) return result;

        return getPlayers(args[args.length-1]).size() == 0 ? getPlayers("") : getPlayers(args[args.length-1]);
    }

    private List<String> getPlayers(String nameStart) {
        List<String> names = new ArrayList<>();
        for(Player p: Players.get()) names.add(p.getName());
        return names.stream().filter(s -> s.toLowerCase().startsWith(nameStart.toLowerCase())).collect(Collectors.toList());
    }
}