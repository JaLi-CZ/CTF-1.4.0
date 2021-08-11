package org.jalicz.CTF.Game.Spectating;

import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Movement.Teleporter;
import org.bukkit.entity.Player;

public class Spectators {

    public static void add(Player player) {
        Visibility.hide(player);
        Visibility.hideSpectators(player);
        ItemManager.setSpectatorItems(player);
        Teleporter.toPlatform(player);
        Teams.set(player, Team.SPECTATORS);

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.8f);
    }
    public static void remove(Player player) {
        Visibility.showForOthers(player);
        ItemManager.setLobbyItems(player);
        Teleporter.toSpawn(player);
        Teams.set(player, Team.NONE);

        player.setAllowFlight(false);
        player.setFlying(false);
    }
    public static void remove() {
        for(Player p: Players.get()) remove(p);
    }
}