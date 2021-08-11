package org.jalicz.CTF.Game.Items.Powerups;

import org.bukkit.metadata.FixedMetadataValue;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Data.Frozen;
import org.jalicz.CTF.Game.Data.Teams;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Visual.Audio;
import org.jalicz.CTF.Game.Visual.Particles;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrisonManager {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final ArrayList<Item> prisons = new ArrayList<>();
    private static final World world = WorldManager.GAME;
    private static final Random random = new Random();
    private static BukkitTask task = null;
    private static int currentPrisonID = 0;


    public static void spawn(Player player) {
        String team = Teams.getEntityNameByTeam(Teams.get(player));
        if(team.equals("N")) return;

        Item prison = world.dropItem(player.getLocation().add(new Vector(0, 1, 0)), ItemManager.PRISON);
        prison.setVelocity(player.getLocation().getDirection().add(new Vector(0,0.35,0)));
        prison.setCustomNameVisible(false);
        prison.setCustomName(team);
        prison.setMetadata("UNPICKABLE", new FixedMetadataValue(plugin, null));

        prisons.add(prison);
    }

    private static void build(int x, int z) {
        new BukkitRunnable() {
            final int ID = currentPrisonID;
            int status = 0;

            @Override
            public void run() {
                if(status <= 5) {
                    getPrisonBlocksAtY(x, status+100, z).forEach(b -> placeGlass(b, ID));
                    Audio.play(new Location(world, x, status+100, z), status == 5 ? Sound.ANVIL_LAND : Sound.DIG_STONE, 2.3f, status == 5 ? 0.8f : 0.55f);

                } else if(status >= 35) {
                    if(status > 40) {
                        cancel();
                        return;
                    }
                    int y = -status+140;
                    getPrisonBlocksAtY(x, y, z).forEach(b -> breakGlass(b, ID));
                    Audio.play(new Location(world, x, y, z), Sound.NOTE_BASS, 2.3f, 0.7f);
                    if(y == 100) Audio.play(new Location(world, x, y, z), Sound.IRONGOLEM_HIT, 2.3f, 0.5f);
                }
                status++;
            }
        }.runTaskTimer(plugin, 0, 4);
        currentPrisonID++;
    }

    private static void placeGlass(Block block, int ID) {
        if(world.getBlockAt(block.getLocation().getBlockX(), 98, block.getLocation().getBlockZ()).getType() != Material.BARRIER) return;
        block.setType(Material.STAINED_GLASS);
        block.setData((byte) (random.nextInt(15)+1));
        block.setMetadata("ID", new FixedMetadataValue(plugin, ID));

        Particles.spawn(Effect.CRIT,     block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 105 ? 6 : 3);
        Particles.spawn(Effect.LAVA_POP, block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 105 ? 4 : 2);
        Particles.spawn(Effect.SPELL,    block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 105 ? 2 : 1);
    }

    private static void breakGlass(Block block, int ID) {
        if(block.getType() != Material.STAINED_GLASS || !block.hasMetadata("ID") || (int) block.getMetadata("ID").get(0).value() != ID) return;
        block.setType(Material.AIR);
        block.removeMetadata("ID", plugin);

        Particles.spawn(Effect.FLAME, block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 100 ? 6 : 3);
        Particles.spawn(Effect.CRIT,  block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 100 ? 5 : 3);
        Particles.spawn(Effect.SMOKE, block.getLocation().add(new Vector(0.5, 0, 0.5)), block.getLocation().getBlockY() == 100 ? 4 : 2);
    }

    private static List<Block> getPrisonBlocksAtY(int x, int y, int z) {
        y = y < 100 ? 100 : Math.min(y, 105);

        List<Block> blocks = new ArrayList<>();
        if(y == 105) for(int relX=-3; relX<=3; relX++) for(int relZ=-3; relZ<=3; relZ++) blocks.add(world.getBlockAt(x+relX, y, z+relZ));
        else {
            int[][] locations = {
                    new int[] {x+2, y, z+2}, new int[] {x+2, y, z+1}, new int[] {x+2, y, z}, new int[]{x+2, y, z-1},
                    new int[] {x+2, y, z-2}, new int[] {x+1, y, z-2}, new int[] {x, y, z-2}, new int[]{x-1, y, z-2},
                    new int[] {x-2, y, z-2}, new int[] {x-2, y, z-1}, new int[] {x-2, y, z}, new int[]{x-2, y, z+1},
                    new int[] {x-2, y, z+2}, new int[] {x-1, y, z+2}, new int[] {x, y, z+2}, new int[]{x+1, y, z+2}
            };
            for(int[] loc: locations) {
                Block block = world.getBlockAt(loc[0], loc[1], loc[2]);
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static void runTask() {
        if(task == null) task = new BukkitRunnable() {
            @Override
            public void run() {
                if(prisons.isEmpty()) return;
                ArrayList<Item> remove = new ArrayList<>();

                for(Item prison: prisons) {
                    int x = prison.getLocation().getBlockX(), y = prison.getLocation().getBlockY(), z = prison.getLocation().getBlockZ();
                    if(world.getBlockAt(x, y - 1, z).getType() != Material.AIR || world.getBlockAt(x + 1, y, z).getType() != Material.AIR ||
                            world.getBlockAt(x - 1, y, z).getType() != Material.AIR || world.getBlockAt(x, y, z + 1).getType() != Material.AIR ||
                            world.getBlockAt(x, y, z - 1).getType() != Material.AIR || world.getBlockAt(x, y + 1, z).getType() != Material.AIR) {
                        build(x, z);
                        remove.add(prison);
                        prison.remove();
                    } else {
                        List<Entity> nearby = prison.getNearbyEntities(0.3, 0.8, 0.3);
                        if(!nearby.isEmpty()) for(Entity entity: nearby) if(entity instanceof Player) {
                                Player player = (Player) entity;
                                if(Teams.get(player) != Team.SPECTATORS && Teams.get(player) != Teams.getTeamByEntityName(prison.getCustomName()) && !Frozen.is(player)) {
                                    build(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
                                    remove.add(prison);
                                    prison.remove();
                                    break;
                                }
                            }
                        }
                    }
                remove.forEach(prisons::remove);
            }
        }.runTaskTimer(plugin, 0, 0);
    }
    
    public static void cancelTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
        if(!prisons.isEmpty()) {
            prisons.forEach(Item::remove);
            prisons.clear();
        }
    }
}