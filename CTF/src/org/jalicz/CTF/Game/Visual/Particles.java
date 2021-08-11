package org.jalicz.CTF.Game.Visual;

import org.bukkit.Effect;
import org.bukkit.Location;

public class Particles {

    public static void spawn(Effect effect, Location location, int amount) {
        for(int i=0; i<amount; i++) location.getWorld().playEffect(location, effect, 100);
    }
}