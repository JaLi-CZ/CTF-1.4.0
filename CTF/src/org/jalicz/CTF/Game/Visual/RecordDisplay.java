package org.jalicz.CTF.Game.Visual;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jalicz.CTF.OutGameData.RecordData;
import org.jalicz.CTF.OutGameData.RecordManager;
import org.jalicz.CTF.Enums.RecordCategory;
import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Worlds.WorldManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RecordDisplay {

    private static final World lobby = WorldManager.LOBBY;
    private static final HashMap<ArmorStand, ArmorStand[]> stands = new HashMap<>();


    public static void updateAll() {
        for(RecordCategory category: Arrays.stream(RecordCategory.values()).limit(8).collect(Collectors.toList()))
            for(int place=1; place<=5; place++) update(getMainStand(category, place), RecordManager.get(category, place));
    }

    private static void update(ArmorStand stand, RecordData data) {
        if(stand == null) {
            Message.console(Strings.GAME + C.RED + "Unable to display record [" + data.toString() + C.RED + "], because the armor stand is null!");
            return;
        }
        if(stand.isCustomNameVisible()) stand.setCustomNameVisible(false);

        ArmorStand[] children = stands.get(stand);
        if(children == null) {
            children = spawnChildren(stand);
            stands.put(stand, children);
        }
        children[0].setCustomName(data.PLAYER);
        children[1].setCustomName(data.SCORE);
        children[2].setCustomName(data.PLACE);

        if(data.PLAYER.contains("-")) stand.setHelmet(new ItemStack(Material.AIR));
        else stand.setHelmet(Players.getSkull(data.PLAYER.substring(2)));
    }

    private static ArmorStand[] spawnChildren(ArmorStand stand) {
        ArmorStand[] children = {
                (ArmorStand) lobby.spawnEntity(stand.getLocation().add(0, 1, 0), EntityType.ARMOR_STAND),
                (ArmorStand) lobby.spawnEntity(stand.getLocation().add(0, 1.25, 0), EntityType.ARMOR_STAND),
                (ArmorStand) lobby.spawnEntity(stand.getLocation().add(0, 1.5, 0), EntityType.ARMOR_STAND)
        };
        for(ArmorStand c: children) {
            c.setGravity(false);
            c.setMarker(true);
            c.setVisible(false);
            c.setCustomNameVisible(true);
        }
        return children;
    }

    private static ArmorStand getMainStand(RecordCategory category, int place) {
        for(Entity e: lobby.getEntities()) if(e instanceof ArmorStand) {
            String name = e.getName();
            if(!name.startsWith("TOP", 2) || !name.substring(6, 7).equals(place+"")) continue;
            if(category.toString().contains("TOTAL")) { if(name.charAt(0) != 'P') continue; }
            else if(name.charAt(0) != 'M') continue;
            char lastChar = name.charAt(name.length()-1);
            if((category.toString().contains("WINS") && lastChar == '3') || (category.toString().contains("SCORE") && lastChar == '1') ||
                    (category.toString().contains("COLLECTED") && lastChar == '2') || (category.toString().contains("FROZEN") && lastChar == '4')) {
                return (ArmorStand) e;
            }
        }
        return null;
    }
}
