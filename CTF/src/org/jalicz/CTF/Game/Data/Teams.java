package org.jalicz.CTF.Game.Data;

import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jalicz.CTF.Game.Visual.*;
import java.util.HashMap;
import java.util.Random;

public class Teams {

    private static final HashMap<Player, Team> teams = new HashMap<>();
    private static final Random random = new Random();


    public static Team get(Player player) {
        return teams.getOrDefault(player, Team.NONE);
    }

    public static Team getTeamByEntityName(String name) {
        switch (name) {
            case "R": return Team.RED;
            case "B": return Team.BLUE;
            default: return Team.NONE;
        }
    }

    public static String getEntityNameByTeam(Team team) {
        switch (team) {
            case RED: return "R";
            case BLUE: return "B";
            default: return "N";
        }
    }

    public static void set(Player player, Team team) {

        if(get(player) == team) {
            Audio.play(player, Sound.NOTE_BASS, 1, 1);
            Message.player(player, Strings.TEAM + C.YELLOW + "You are already joined in " + getString(player) + C.YELLOW + " team!");
            return;
        }
        if(team == Team.RED) {
            int i=0; if(get(player) == Team.BLUE) i++;

            if(Players.count(Team.RED) > Players.count(Team.BLUE)-i) {
                Audio.play(player, Sound.NOTE_BASS, 1, 1);
                Message.player(player, Strings.TEAM + C.RED + "RED " + C.YELLOW + "Team is full!");

            } else {
                remove(player);
                teams.put(player, Team.RED);
                Audio.play(player, Sound.SUCCESSFUL_HIT, 1, 1.3f);
                Message.player(player, Strings.TEAM + C.GREEN + "You joined to " + C.RED + "RED " + C.GREEN + "Team!");
                if(player.getInventory().getItem(0).getType() == Material.BANNER) player.getInventory().setItem(0, ItemManager.SELECT_TEAM_RED);
            }

        } else if(team == Team.BLUE) {
            int i=0; if(get(player) == Team.RED) i++;

            if(Players.get(Team.RED).size()-i < Players.get(Team.BLUE).size()) {
                Audio.play(player, Sound.NOTE_BASS, 1, 1);
                Message.player(player, Strings.TEAM + C.AQUA + "BLUE " + C.YELLOW + "Team is full!");

            } else {
                teams.put(player, Team.BLUE);
                Audio.play(player, Sound.SUCCESSFUL_HIT, 1, 1.3f);
                Message.player(player, Strings.TEAM + C.GREEN + "You joined to " + C.AQUA + "BLUE " + C.GREEN + "Team!");
                if(player.getInventory().getItem(0).getType() == Material.BANNER) player.getInventory().setItem(0, ItemManager.SELECT_TEAM_BLUE);
            }

        } else if(team == Team.NONE) {
            if(get(player) != Team.NONE) {
                Audio.play(player, Sound.NOTE_PLING, 1, 1.2f);
                Message.player(player, Strings.TEAM + C.YELLOW + "You left " + getString(player) + C.YELLOW + " team!");
                if(player.getInventory().getItem(0).getType() == Material.BANNER) player.getInventory().setItem(0, ItemManager.SELECT_TEAM);
            }
            remove(player);

        } else teams.put(player, Team.SPECTATORS);

        NameTagFormat.update(player);
        GameScoreboard.update();
    }

    public static String getString(Player player) {
        switch (get(player)) {
            case RED: return C.RED + "RED";
            case BLUE: return C.AQUA + "BLUE";
            case SPECTATORS: return C.GREEN + "SPECTATOR";
            default: return C.YELLOW + "None";
        }
    }

    public static String getString() {
        return C.RED + Players.count(Team.RED) + C.BLACK + " vs " + C.AQUA + Players.count(Team.BLUE);
    }

    public static void remove(Player player) {
        teams.remove(player);
    }

    public static void clear() {
        teams.clear();
    }

    public static void sort() {
        for(Player p: Players.get(Team.NONE)) {
            remove(p);

            if(Players.count(Team.RED) > Players.count(Team.BLUE)) teams.put(p, Team.BLUE);
            else if(Players.count(Team.RED) < Players.count(Team.BLUE)) teams.put(p, Team.RED);
            else {
                if(random.nextBoolean()) teams.put(p, Team.RED);
                else teams.put(p, Team.BLUE);
            }
            NameTagFormat.update(p);
            Message.player(p, Strings.TEAM + C.DARK_AQUA + "You were added to " + getString(p.getPlayer()) + C.DARK_AQUA + " team!");
        }
    }
}