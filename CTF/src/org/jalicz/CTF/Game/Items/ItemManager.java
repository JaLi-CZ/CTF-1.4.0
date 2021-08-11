package org.jalicz.CTF.Game.Items;

import org.jalicz.CTF.Game.Data.Players;
import org.jalicz.CTF.Game.Visual.C;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Random;

public class ItemManager {

    private static final Random random = new Random();
    public static final String selectKitBannerName = C.YELLOW + "Select Kit", selectTeamBannerName = C.YELLOW + "Select Team";
    public static final ItemStack
            JOIN_RED, JOIN_BLUE, LEAVE_TEAM, CLOSE_MENU,
            RED_FLAG, BLUE_FLAG,
            SELECT_KIT, SELECTED_KIT,
            SELECT_TEAM, SELECT_TEAM_RED, SELECT_TEAM_BLUE,
            LOBBY_CLOCK, TRACKING_COMPASS,
            FREEZE_STICK, UNFREEZE_STICK,
            KNOCK_BALL, BOOSTER, BOMB, PRISON,
            DEFENDER_KIT, FREEZE_IMMUNITY_KIT, RUSHER_KIT, POWERUP_MASTER_KIT,
            CHANGE_DEFAULT_KIT;

    static {
        JOIN_RED = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData()); {
            ItemMeta meta = JOIN_RED.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.RED + "RED Team");

            lore.add(C.GRAY + "Click me to join.");

            meta.setLore(lore);
            JOIN_RED.setItemMeta(meta);
        }
        JOIN_BLUE = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIGHT_BLUE.getWoolData()); {
            ItemMeta meta = JOIN_BLUE.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.AQUA + "BLUE Team");

            lore.add(C.GRAY + "Click me to join.");

            meta.setLore(lore);
            JOIN_BLUE.setItemMeta(meta);
        }
        LEAVE_TEAM = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData()); {
            ItemMeta meta = LEAVE_TEAM.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GREEN + "Leave Team");

            lore.add(C.GRAY + "Click me to leave you current team.");

            meta.setLore(lore);
            LEAVE_TEAM.setItemMeta(meta);
        }
        CLOSE_MENU = new ItemStack(Material.BARRIER); {
            ItemMeta meta = CLOSE_MENU.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.DARK_RED + "Close");

            lore.add(C.GRAY + "Click me to close this menu.");

            meta.setLore(lore);
            CLOSE_MENU.setItemMeta(meta);
        }

        RED_FLAG = new ItemStack(Material.BANNER, 1, DyeColor.RED.getDyeData()); {
            ItemMeta meta = RED_FLAG.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.RED + "RED Flag");

            lore.add(C.GRAY + "Bring me to your team platform and score!");

            meta.setLore(lore);
            RED_FLAG.setItemMeta(meta);
        }
        BLUE_FLAG = new ItemStack(Material.BANNER, 1, DyeColor.LIGHT_BLUE.getDyeData()); {
            ItemMeta meta = BLUE_FLAG.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.AQUA + "BLUE Flag");

            lore.add(C.GRAY + "Bring me to your team platform and score!");

            meta.setLore(lore);
            BLUE_FLAG.setItemMeta(meta);
        }

        SELECT_KIT = new ItemStack(Material.FEATHER); {
            ItemMeta meta = SELECT_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(selectKitBannerName);

            lore.add(C.GRAY + "Click me to choose your kit!");

            meta.setLore(lore);
            SELECT_KIT.setItemMeta(meta);
        }
        SELECTED_KIT = new ItemStack(Material.FEATHER); {
            ItemMeta meta = SELECTED_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(selectKitBannerName);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "Click me to change your kit!");

            meta.setLore(lore);
            SELECTED_KIT.setItemMeta(meta);
        }

        SELECT_TEAM = new ItemStack(Material.BANNER, 1, DyeColor.WHITE.getDyeData()); {
            ItemMeta meta = SELECT_TEAM.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(selectTeamBannerName);

            lore.add(C.GRAY + "Click with me and choose your team!");

            meta.setLore(lore);
            SELECT_TEAM.setItemMeta(meta);
        }
        SELECT_TEAM_RED = new ItemStack(Material.BANNER, 1, DyeColor.RED.getDyeData()); {
            ItemMeta meta = SELECT_TEAM_RED.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(selectTeamBannerName);

            lore.add(C.GRAY + "Click with me to change your team!");
            lore.add(C.GRAY + "Your current team -> " + C.BOLD + C.RED + "RED Team" + C.ITALIC + C.GRAY + ".");

            meta.setLore(lore);
            SELECT_TEAM_RED.setItemMeta(meta);
        }
        SELECT_TEAM_BLUE = new ItemStack(Material.BANNER, 1, DyeColor.LIGHT_BLUE.getDyeData()); {
            ItemMeta meta = SELECT_TEAM_BLUE.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(selectTeamBannerName);

            lore.add(C.GRAY + "Click with me to change your team!");
            lore.add(C.GRAY + "Your current team -> " + C.BOLD + C.AQUA + "BLUE Team" + C.ITALIC + C.GRAY + ".");

            meta.setLore(lore);
            SELECT_TEAM_BLUE.setItemMeta(meta);
        }

        LOBBY_CLOCK = new ItemStack(Material.WATCH); {
            ItemMeta meta = LOBBY_CLOCK.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GREEN + "Return to Lobby");

            lore.add(C.GRAY + "Click with me to return to the lobby!");

            meta.setLore(lore);
            LOBBY_CLOCK.setItemMeta(meta);
        }
        TRACKING_COMPASS = new ItemStack(Material.COMPASS); {
            ItemMeta meta = TRACKING_COMPASS.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Tracking Compass");

            lore.add(C.GRAY + "Click with me to open a player tracking menu!");

            meta.setLore(lore);
            TRACKING_COMPASS.setItemMeta(meta);
        }

        FREEZE_STICK = new ItemStack(Material.STICK); {
            ItemMeta meta = FREEZE_STICK.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.AQUA + "Freeze Stick");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "Hit an enemy with me to freeze him!");

            meta.setLore(lore);
            FREEZE_STICK.setItemMeta(meta);
        }
        UNFREEZE_STICK = new ItemStack(Material.BLAZE_ROD); {
            ItemMeta meta = UNFREEZE_STICK.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Unfreeze Stick");

            lore.add(C.GRAY + "Hit a teammate with me to unfreeze him!");

            meta.setLore(lore);
            UNFREEZE_STICK.setItemMeta(meta);
        }

        KNOCK_BALL = new ItemStack(Material.SLIME_BALL); {
            ItemMeta meta = KNOCK_BALL.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GREEN + "Knock " + C.WHITE + "Ball");
            meta.addEnchant(Enchantment.KNOCKBACK, 5, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.AQUA + "Knockback V.");
            lore.add(C.GRAY + "Hit an enemy with me and punch him far away!");

            meta.setLore(lore);
            KNOCK_BALL.setItemMeta(meta);
        }
        BOOSTER = new ItemStack(Material.FEATHER); {
            ItemMeta meta = BOOSTER.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.RED + "B" + C.GOLD + "o" + C.YELLOW + "o" + C.GREEN + "s" + C.AQUA + "t" + C.PINK + "e" + C.PURPLE + "r");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "Click with me and I will boost you");
            lore.add(C.GRAY + " in which direction you're looking!");

            meta.setLore(lore);
            BOOSTER.setItemMeta(meta);
        }
        BOMB = new ItemStack(Material.TNT); {
            ItemMeta meta = BOMB.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.RED + "Bomb");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "Throw me on your enemies to");
            lore.add(C.GRAY + " slow and blind them!");

            meta.setLore(lore);
            BOMB.setItemMeta(meta);
        }
        PRISON = new ItemStack(Material.IRON_FENCE); {
            ItemMeta meta = PRISON.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.YELLOW + "Prison");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "Throw me on your enemies to trap them!");
            lore.add(C.GRAY + "Or you can use me as a shield!");

            meta.setLore(lore);
            PRISON.setItemMeta(meta);
        }

        DEFENDER_KIT = new ItemStack(Material.IRON_CHESTPLATE); {
            ItemMeta meta = DEFENDER_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Defender");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "1x " + BOOSTER.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "1x " + PRISON.getItemMeta().getDisplayName());
            lore.add(C.WHITE + "Speed II." + C.GRAY + " on your platform!");
            lore.add(C.GRAY + "When you freeze an enemy:");
            lore.add(C.GRAY + " > + 70% duration of your freezing effect!");
            lore.add(C.GRAY + " > " + C.WHITE + "Speed III." + C.GRAY + " effect for 8s!");
            lore.add(C.GRAY + " > " + C.PINK + "50% chance" + C.GRAY + " to receive a" + C.AQUA + " random powerup" + C.GRAY + "!");
            lore.add(C.GRAY + "Speed effect is available " + C.DARK_RED + "only on team platform" + C.GRAY + "!");

            meta.setLore(lore);
            DEFENDER_KIT.setItemMeta(meta);
        }
        FREEZE_IMMUNITY_KIT = new ItemStack(Material.BLAZE_POWDER); {
            ItemMeta meta = FREEZE_IMMUNITY_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Freeze Immunity");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.WHITE + "Speed I." + C.GRAY + " everywhere!");
            lore.add(C.PINK + "15% chance" + C.GRAY + " to be unfrozen instantly!");
            lore.add(C.GRAY + "-50% duration of enemy freezing effect!");
            lore.add(C.GRAY + "Gives a slowness II. and blindness");
            lore.add(C.GRAY + " effect - for 5s to enemy who froze you!");
            lore.add(C.PINK + "75% chance" + C.GRAY + " that after unfreezing");
            lore.add(C.GRAY + " your teammate you will get a " + C.AQUA + "random powerup" + C.GRAY + "!");

            meta.setLore(lore);
            FREEZE_IMMUNITY_KIT.setItemMeta(meta);
        }
        RUSHER_KIT = new ItemStack(Material.DIAMOND_SWORD); {
            ItemMeta meta = RUSHER_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Rusher");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "1x " + KNOCK_BALL.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "1x " + BOOSTER.getItemMeta().getDisplayName());
            lore.add(C.WHITE + "Speed I." + C.GRAY + " on enemy team platform!");
            lore.add(C.GRAY + "-20% duration of enemy freezing effect!");
            lore.add(C.GRAY + "When you pickup a Flag you will receive:");
            lore.add(C.GRAY + " > " + C.AQUA + "Random powerup" + C.GRAY + "!");
            lore.add(C.GRAY + " > " + C.WHITE + "Speed II." + C.GRAY + " effect for 10s!");

            meta.setLore(lore);
            RUSHER_KIT.setItemMeta(meta);
        }
        POWERUP_MASTER_KIT = new ItemStack(Material.TNT); {
            ItemMeta meta = POWERUP_MASTER_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GOLD + "Powerup Master");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            lore.add(C.GRAY + "1x " + KNOCK_BALL.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "1x " + BOOSTER.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "1x " + BOMB.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "1x " + PRISON.getItemMeta().getDisplayName());
            lore.add(C.GRAY + "When you pickup a powerup you will get");
            lore.add(C.WHITE + " Speed II." + C.GRAY + " and" + C.GREEN + " Jump-boost II." + C.GRAY + " effect for 7s!");
            lore.add(C.PINK + "15% chance" + C.GRAY + " that after using a powerup");
            lore.add(C.GRAY + " you will get another!");

            meta.setLore(lore);
            POWERUP_MASTER_KIT.setItemMeta(meta);
        }

        CHANGE_DEFAULT_KIT = new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData()); {
            ItemMeta meta = CHANGE_DEFAULT_KIT.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            meta.setDisplayName(C.GREEN + "Change Default Kit");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            lore.add(C.GRAY + "Click me to change your default kit!");
            lore.add(C.GRAY + "Your default kit will be changed");
            lore.add(C.GRAY + " to your currently selected kit.");

            meta.setLore(lore);
            CHANGE_DEFAULT_KIT.setItemMeta(meta);
        }
    }


    public static ItemStack getRandomPowerup() {
        switch (random.nextInt(4)) {
            case 0: return KNOCK_BALL;
            case 1: return BOOSTER;
            case 2: return BOMB;
            default: return PRISON;
        }
    }

    public static void setLobbyItems(Player player) {
        Inventory inv = player.getInventory();
        clearItems(player);
        inv.setItem(0, SELECT_TEAM);
        inv.setItem(1, SELECT_KIT);
        inv.setItem(8, LOBBY_CLOCK);
    }

    public static void setLobbyItems() {
        for(Player p: Players.get()) setLobbyItems(p);
    }

    public static void setGameItems(Player player) {
        Inventory inv = player.getInventory();
        clearItems(player);
        inv.setItem(0, FREEZE_STICK);
        inv.setItem(1, UNFREEZE_STICK);
    }

    public static void setGameItems() {
        for(Player p: Players.get()) setGameItems(p);
    }

    public static void setSpectatorItems(Player player) {
        Inventory inv = player.getInventory();
        clearItems(player);
        inv.setItem(7, TRACKING_COMPASS);
        inv.setItem(8, LOBBY_CLOCK);
    }

    public static void clearItems(Player player) {
        player.getInventory().clear();
    }

    public static void clearItems() {
        for(Player p: Players.get()) clearItems(p);
    }
}