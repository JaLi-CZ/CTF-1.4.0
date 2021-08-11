package org.jalicz.CTF.Game.Spectating;

import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Players;
import org.bukkit.entity.Player;

public class Visibility {

    public static void showForOthers(Player player) {
        for(Player p: Players.get()) if(!p.equals(player)) p.showPlayer(player);
    }
    public static void showSpectators() {
        for(Player p: Players.get(Team.SPECTATORS)) showForOthers(p);
    }
    public static void showEveryoneTo(Player player) {
        for(Player p: Players.get()) if(!p.equals(player)) player.showPlayer(p);
    }
    public static void hide(Player player) {
        for(Player p: Players.get()) if(!p.equals(player)) p.hidePlayer(player);
    }
    public static void hideSpectators(Player player) {
        for(Player p: Players.get(Team.SPECTATORS)) if(!p.equals(player)) player.hidePlayer(p);
    }
}