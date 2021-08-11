package org.jalicz.CTF.Game.Visual;

import org.jalicz.CTF.Game.Data.Players;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jalicz.CTF.Game.Data.Strings;
import org.jalicz.CTF.Game.Items.ItemManager;

import java.util.ArrayList;
import java.util.Random;

public class Message {

    private static final Random random = new Random();
    public static final ArrayList<Player> consoleOutputReaders = new ArrayList<>();


    public static void player(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void console(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
        if(!consoleOutputReaders.isEmpty()) for(Player reader: consoleOutputReaders) player(reader, C.DARK_GRAY + "[" + C.GRAY + "Console" + C.DARK_GRAY + "] " + msg);
    }

    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(msg);
    }

    public static void title(Player player, String title, String sub, int in, int stay, int out) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, in, stay, out));
        player.sendTitle(title, sub);
    }

    public static void title(String title, String sub, int in, int stay, int out) {
        for(Player p: Players.get()) title(p, title, sub, in, stay, out);
    }

    public static void actionBar(Player player, String msg) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + msg + "\"}"), (byte) 2));
    }

    public static void actionBar(String msg) {
        for(Player p: Players.get()) actionBar(p, msg);
    }

    public static void xpBar(Player player, int i) {
        player.setLevel(i);
    }

    public static void xpBar(int i) {
        for(Player p: Players.get()) p.setLevel(i);
    }

    public static void joinInfoMessage(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(
                Strings.joinInfoMessage.replaceFirst("--TIP--", randomTip()))));
    }

    public static void commandHelpMessage(Player player) {
        Message.player(player, Strings.commandHelpMessage);
    }

    private static String randomTip() {
        switch (random.nextInt(14)) {
            case 0: return "If you found a hacker or vulgar player, report him using the link below.";
            case 1: return "The " + ItemManager.BOMB.getItemMeta().getDisplayName() + C.PINK + " will drop, slow down and blind your enemies!";
            case 2: return "You can use the " + ItemManager.PRISON.getItemMeta().getDisplayName() + C.PINK + " as a shield.";
            case 3: return "You can have maximally 1 powerup of each type in your inventory!";
            case 4: return "You can set or change your default kit by clicking with " + ItemManager.selectKitBannerName + C.PINK + " and then clicking on " +
                    ItemManager.CHANGE_DEFAULT_KIT.getItemMeta().getDisplayName() + C.PINK + "!";
            case 5: return "The " + ItemManager.BOMB.getItemMeta().getDisplayName() + C.PINK + " can affect players from 5.3 up to 5.8 block radius.";
            case 6: return "Almost everything in this game is random, just like this number " + C.AQUA + random.nextInt(1000000000) + C.PINK + ".";
            case 7: return "The powerups are spawning on the middle every 6 seconds.";
            case 8: return "Which team brings 5 enemy flags first, wins.";
            case 9: return "The enemy can freeze you in his half, and you can freeze him in your own.";
            case 10: return "Kits can affect the length of the freezing effect and the speed of movement.";
            case 11: return "Try to bring the flag of an enemy team, to be a little closer to victory.";
            case 12: return "You will receive a flag by entering the safe zone on the enemy half.";
            default: return "We are currently looking for some administrators. If you are interested, check this: " + C.AQUA + "https://bit.ly/3waJVe4" + C.PINK + ".";
        }
    }
}