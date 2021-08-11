package org.jalicz.CTF.Game.Worlds;

import org.bukkit.Material;
import org.bukkit.World;

public class Wall {

    private static final World world = WorldManager.GAME;


    public static void build() {
        for(int z=-24; z<=24; z++) for(int y=100; y<106; y++) {
                if(z < 17 && z > -17)   world.getBlockAt(0, y, z).setType(Material.GLASS);
                else                    world.getBlockAt(0, y, z).setType(Material.BARRIER);
            }
    }

    public static void destroy() {
        for(int z=-24; z<=24; z++) for(int y=100; y<106; y++) world.getBlockAt(0, y, z).setType(Material.AIR);
    }
}