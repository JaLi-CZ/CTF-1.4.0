package org.jalicz.CTF.Game.Visual;

import org.jalicz.CTF.Enums.Status;
import org.jalicz.CTF.Game.Data.*;
import org.jalicz.CTF.Game.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameTagFormat {

    public static void update(Player player) {
        Scoreboard board = Boards.get(player);
        Team team = board.getTeam(player.getName());
        if(team == null) team = board.registerNewTeam(player.getName());

        String prefix;
        switch (Teams.get(player)) {
            case RED:        prefix = C.RED;        break;
            case BLUE:       prefix = C.AQUA;       break;
            case SPECTATORS: prefix = C.GREEN;       break;
            default:         prefix = C.GRAY;
        }
        if(Game.status == Status.RUNNING) { if(player.getInventory().contains(Material.BANNER)) prefix = C.PURPLE + "[" + C.PINK + "Flag" + C.PURPLE + "] " + prefix; }
        else if(!Game.isRunning()) prefix = RankManager.getPrefix(player, false) + prefix;
        team.setPrefix(prefix);

        if(Frozen.is(player)) {
            int sec = Frozen.getDuration(player);

            String secColor;
            if     (sec > 90)   secColor = C.DARK_RED;
            else if(sec > 60)   secColor = C.RED;
            else if(sec > 45)   secColor = C.GOLD;
            else if(sec > 30)   secColor = C.YELLOW;
            else if(sec > 20)   secColor = C.GREEN;
            else if(sec > 10)   secColor = C.AQUA;
            else                secColor = C.PINK;

            team.setSuffix(C.DARK_AQUA + " [F] - " + secColor + sec + "s");

        } else team.setSuffix("");

        team.addEntry(player.getName());

        for(Player p: Players.get()) {
            if(p == player) continue;

            Scoreboard hisBoard = Boards.get(p);
            Team hisTeam = hisBoard.getTeam(player.getName());
            if(hisTeam == null) hisTeam = hisBoard.registerNewTeam(player.getName());

            hisTeam.setPrefix(team.getPrefix());
            hisTeam.setSuffix(team.getSuffix());

            hisTeam.addEntry(player.getName());
        }
    }

    public static void update() {
        Players.get().forEach(NameTagFormat::update);
    }
}