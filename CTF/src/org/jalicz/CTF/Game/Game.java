package org.jalicz.CTF.Game;

import org.bukkit.*;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.Enums.Zone;
import org.jalicz.CTF.OutGameData.DefaultKitManager;
import org.jalicz.CTF.OutGameData.StatsManager;
import org.jalicz.CTF.Enums.Status;
import org.jalicz.CTF.Enums.Team;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Items.Powerups.BombManager;
import org.jalicz.CTF.Game.Items.Powerups.PowerupSpawner;
import org.jalicz.CTF.Game.Items.Powerups.PrisonManager;
import org.jalicz.CTF.Game.Movement.EffectManager;
import org.jalicz.CTF.Game.Movement.LobbyTransporter;
import org.jalicz.CTF.Game.Movement.PlayerSpeedManager;
import org.jalicz.CTF.Game.Movement.Teleporter;
import org.jalicz.CTF.Game.Spectating.Visibility;
import org.jalicz.CTF.Game.Worlds.Wall;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jalicz.CTF.Game.Data.*;
import org.jalicz.CTF.Game.Visual.*;
import java.util.Random;

public class Game {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final Random random = new Random();
    private static BukkitTask gameTask = null, inGameCountdownTask = null;
    public static final World lobby = WorldManager.LOBBY, game = WorldManager.GAME;
    public static Status status = Status.UNABLE_TO_START;
    public static boolean allPlayersLeftGameBeforeStart = false;
    public static int TIME = 0;


    public static String getTimeString() {
        int t = TIME;
        int sec = t % 60;
        t -= sec;
        int min = t/60;
        String s0 = "";
        if(sec < 10) s0 = "0";
        return C.GREEN + min + ":" + s0 + sec;
    }

    public static boolean isRunning() {
        return status == Status.RUNNING || status == Status.STARTING || status == Status.ENDING;
    }

    public static boolean isRunningTooLong() {
        return TIME >= plugin.getConfig().getInt("maximum-game-duration");
    }
    
    public static void start() {
        status = Status.STARTING;
        LobbyTransporter.cancelAllTasks();
        Teams.sort();
        Teleporter.toPlatform();
        ItemManager.setGameItems();
        for(Player p: Players.get()) {
            p.getInventory().setHeldItemSlot(2);
            p.setGameMode(GameMode.SURVIVAL);
            StatsManager.add(p.getName(), Statistics.GAMES_PLAYED);
        }
        Audio.play(Sound.ENDERMAN_TELEPORT, 1.2f, 0.75f);
        Wall.build();
        BombManager.runTask();
        PrisonManager.runTask();
        PlayerSpeedManager.runTask();
        SpectatorCompassFocus.runTask();
        GameScoreboard.update();
        PowerupSpawner.killStands();
        PowerupSpawner.killItems();
        NameTagFormat.update();

        inGameCountdownTask = new BukkitRunnable() {
            int c = 8;
            @Override
            public void run() {
                c--;
                if(c < 1) {
                    status = Status.RUNNING;
                    Message.title(C.GREEN + "Game Started!", C.GOLD + "Good Luck!", 5, 30, 15);
                    Audio.play(Sound.ENDERDRAGON_GROWL, 2.2f, 1.15f);
                    Wall.destroy();
                    PowerupSpawner.start();
                    KitManager.givePowerups();
                    runGameTask();

                    if(allPlayersLeftGameBeforeStart) {
                        if(Players.get(Team.RED).size() == 0) {
                            Message.broadcast(Strings.GAME + C.AQUA + C.BOLD + "BLUE" + C.GREEN + " team wins! (" + C.ITALIC + C.PINK + "All " + C.RED + "RED " + C.PINK +
                                    "players left the Game!" + C.BOLD + C.GREEN + ")");
                            end(Team.BLUE);
                        }
                        else if(Players.get(Team.BLUE).size() == 0) {
                            Message.broadcast(Strings.GAME + C.RED + C.BOLD + "RED" + C.GREEN + " team wins! (" + C.ITALIC + C.PINK + "All " + C.AQUA + "BLUE " + C.PINK +
                                    "players left the Game!" + C.GREEN + C.BOLD + ")");
                            end(Team.RED);
                        }
                        allPlayersLeftGameBeforeStart = false;
                    }
                    cancel();

                } else {
                    Message.title("", C.AQUA + c, 0, 8, 4);
                    Audio.play(Sound.CLICK, 0.6f, 1.65f);
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public static void end(Team winner) {
        if(winner != Team.RED && winner != Team.BLUE) return;

        status = Status.ENDING;
        PowerupSpawner.stop();
        cancelGameTimeTask();
        Players.get().stream().filter(p -> Teams.get(p) != Team.SPECTATORS).forEach(ItemManager::clearItems);
        BombManager.cancelTask();
        PrisonManager.cancelTask();
        GameScoreboard.update();

        Audio.play(Sound.ENDERDRAGON_DEATH, 5, 1f);

        StringBuilder builder = new StringBuilder(C.GOLD + "The WINNERS: ");
        String separator = C.GREEN + "--------------------\n";

        if(winner == Team.RED) {

            for(Player p: Players.get()) {

                if(Teams.get(p) == Team.RED) {
                    Message.title(p, C.GREEN + "You Won!", C.RED + "RED " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
                    StatsManager.add(p.getName(), Statistics.WINS);
                    builder.append(C.RED).append(p.getName()).append(C.GRAY).append(", ");

                } else if(Teams.get(p) == Team.BLUE) {
                    Message.title(p, C.RED + "You Lose!", C.RED + "RED " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
                    StatsManager.add(p.getName(), Statistics.LOSSES);

                } else Message.title(p, C.YELLOW + "Game Over!", C.RED + "RED " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
            }

            Message.broadcast(separator + Strings.GAME + C.YELLOW + "Game over! " + C.BOLD + C.RED + "RED" + C.RESET + C.PINK + " Team is a " +
                    C.BOLD + C.GOLD + "WINNER!\n" + C.RESET + C.DARK_AQUA + "The final score is " + C.BOLD + GameScore.getString() + C.RESET +
                    C.DARK_AQUA + "!\n" + builder.toString() + "\n" + separator);

        } else {

            for(Player p: Players.get()) {

                if(Teams.get(p) == Team.BLUE) {
                    Message.title(p, C.GREEN + "You Won!", C.AQUA + "BLUE " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
                    StatsManager.add(p.getName(), Statistics.WINS);
                    builder.append(C.AQUA).append(p.getName()).append(C.GRAY).append(", ");

                } else if(Teams.get(p) == Team.RED) {
                    Message.title(p, C.RED + "You Lose!", C.AQUA + "BLUE " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
                    StatsManager.add(p.getName(), Statistics.LOSSES);

                } else Message.title(p, C.YELLOW + "Game Over!", C.AQUA + "BLUE " + C.GOLD + "Team won! " + GameScore.getString(), 5, 60, 40);
            }

            Message.broadcast(separator + Strings.GAME + C.YELLOW + "Game over! " + C.BOLD + C.AQUA + "BLUE" + C.RESET + C.PINK + " Team is a " +
                    C.BOLD + C.GOLD + "WINNER!\n" + C.RESET + C.DARK_AQUA + "The final score is " + C.BOLD + GameScore.getString() + C.RESET +
                    C.DARK_AQUA + "!\n" + builder.toString() + "\n" + separator);
        }

        new BukkitRunnable() {
            int cycle = 0;
            @Override
            public void run() {
                cycle++;

                if(cycle < 20) {
                    if(winner == Team.RED) for(Player p: Players.get(Team.RED)) FireworkSpawner.spawn(p, true);
                    else for(Player p: Players.get(Team.BLUE)) FireworkSpawner.spawn(p, true);

                } else if(cycle > 27) {
                    restart();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    public static void restart() {
        if(Players.isMax() && !Players.get(Team.SPECTATORS).isEmpty())
            while (Players.count() > Players.MAXIMUM && !Players.get(Team.SPECTATORS).isEmpty())
                Players.get(Team.SPECTATORS).get(random.nextInt(Players.get(Team.SPECTATORS).size())).kickPlayer
                        (C.translate(plugin.getConfig().getString("kick-messages.full-server")));

        if(inGameCountdownTask != null) {
            inGameCountdownTask.cancel();
            inGameCountdownTask = null;
        }
        status = Status.UNABLE_TO_START;
        GameScore.reset();
        Countdown.stop();
        cancelGameTimeTask();
        PrisonManager.cancelTask();
        PowerupSpawner.stop();
        PlayerSpeedManager.cancelTask();
        SpectatorCompassFocus.cancelTask();
        SpectatorCompassFocus.clear();
        EffectManager.clear();
        Visibility.showSpectators();
        RecordDisplay.updateAll();
        Teleporter.toSpawn();
        ItemManager.setLobbyItems();
        Teams.clear();
        KitManager.clear();
        for(Player p: Players.get()) {
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().setHeldItemSlot(2);
            if(DefaultKitManager.hasDefaultKit(p.getName())) KitManager.set(p, DefaultKitManager.get(p.getName()));
        }
        Frozen.clear();
        Countdown.runTask();
        GameScoreboard.update();
        NameTagFormat.update();
    }

    public static void runDayTimeTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                lobby.setFullTime(plugin.getConfig().getInt("worlds.lobby.time"));
                game.setFullTime(plugin.getConfig().getInt("worlds.game.time"));
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public static void runGameTask() {
        if(gameTask == null) gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(isRunningTooLong()) {
                    TIME = 0;
                    Message.broadcast(Strings.GAME + C.RED + C.BOLD + "The Game has been stopped because it is running too long!");
                    restart();
                    gameTask = null;
                    GameScoreboard.update();
                    cancel();
                } else {
                    TIME++;
                    Frozen.subtractSecond();
                    GameScoreboard.update();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public static void cancelGameTimeTask() {
        if(gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }
    }

    public static void score(Player player) {
        if(Game.status != Status.RUNNING) return;

        FireworkSpawner.spawn(player, false);
        StatsManager.add(player.getName(), Statistics.SCORE);

        if(Teams.get(player) == Team.RED) {
            GameScore.RED++;
            Message.broadcast(Strings.GAME + C.PINK + "Player " + player.getName() + " from " + C.RED + "RED " + C.PINK + "Team scored! " + GameScore.getString());

            if(GameScore.RED < 5) for(Player p: Players.get()) {
                if(Teams.get(p) == Team.RED) {
                    Audio.play(p, Sound.LEVEL_UP, 1, 0.8f);
                    Message.title(p, C.GREEN + "Your Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                    Message.actionBar(p, C.GREEN + C.BOLD + "+ " + C.GOLD + (p.equals(player) ? "You" : player.getName() + C.PINK + " from Your Team") +
                            C.PINK + " scored! " + GameScore.getString());

                } else if(Teams.get(p) == Team.BLUE) {
                    Audio.play(p, Sound.ENDERDRAGON_GROWL, 1, 0.8f);
                    Message.title(p, C.RED + "Enemy Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                    Message.actionBar(p, C.DARK_RED + C.BOLD + "- " + C.RED + player.getName() + C.RED + " from Enemy Team scored! " + GameScore.getString());

                } else {
                    Audio.play(p, Sound.EXPLODE, 1, 0.8f);
                    Message.title(p, C.RED + "RED " + C.GOLD + "Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                }
            } else Game.end(Team.RED);

        } else if(Teams.get(player) == Team.BLUE) {
            GameScore.BLUE++;
            Message.broadcast(Strings.GAME + C.PINK + "Player " + player.getName() + " from " + C.AQUA + "BLUE " + C.PINK + "Team scored! " + GameScore.getString());

            if(GameScore.BLUE < 5) for(Player p: Players.get()) {
                if(Teams.get(p) == Team.BLUE) {
                    Audio.play(p, Sound.LEVEL_UP, 1, 0.8f);
                    Message.title(p, C.GREEN + "Your Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                    Message.actionBar(p, C.GREEN + C.BOLD + "+ " + C.GOLD + (p.equals(player) ? "You" : player.getName() + C.PINK + " from Your Team") +
                            C.PINK + " scored! " + GameScore.getString());

                } else if(Teams.get(p) == Team.RED) {
                    Audio.play(p, Sound.ENDERDRAGON_GROWL, 1, 0.8f);
                    Message.title(p, C.RED + "Enemy Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                    Message.actionBar(p, C.DARK_RED + C.BOLD + "- " + C.RED + player.getName() + C.RED + " from Enemy Team scored! " + GameScore.getString());

                } else {
                    Audio.play(p, Sound.EXPLODE, 1, 0.8f);
                    Message.title(p, C.AQUA + "BLUE " + C.GOLD + "Team SCORED!", C.PINK + player.getName() + " scored! " + GameScore.getString(), 5, 30, 15);
                }
            } else Game.end(Team.BLUE);
        }
    }

    public static boolean canKnock(Player attacker, Player target) {
        if(Teams.get(attacker) == Teams.get(target)) {
            Message.player(attacker, Strings.GAME + C.RED + "You cannot punch your teammates!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return false;
        }
        if(Frozen.is(target)) {
            Message.player(attacker, Strings.GAME + C.RED + "You cannot punch frozen enemies!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return false;
        }
        if(ZoneManager.get(target) == Zone.SAFE_ZONE) {
            Message.player(attacker, Strings.GAME + C.RED + "Enemies are protected in their safe zone!");
            Audio.play(attacker, Sound.NOTE_BASS, 1, 0.7f);
            return false;
        }
        return true;
    }
}