package org.jalicz.CTF.Game;

import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.InventoryHolder;
import org.jalicz.CTF.Administration.AdminManager;
import org.jalicz.CTF.CTFPlugin;
import org.jalicz.CTF.OutGameData.ChatHistoryManager;
import org.jalicz.CTF.OutGameData.DefaultKitManager;
import org.jalicz.CTF.OutGameData.PunishManager;
import org.jalicz.CTF.OutGameData.StatsManager;
import org.jalicz.CTF.Enums.*;
import org.jalicz.CTF.Game.Items.ItemManager;
import org.jalicz.CTF.Game.Items.Powerups.BombManager;
import org.jalicz.CTF.Game.Items.Powerups.PrisonManager;
import org.jalicz.CTF.Game.Movement.EffectManager;
import org.jalicz.CTF.Game.Movement.LobbyTransporter;
import org.jalicz.CTF.Game.Movement.Teleporter;
import org.jalicz.CTF.Game.Spectating.Spectators;
import org.jalicz.CTF.Game.Spectating.Visibility;
import org.jalicz.CTF.Game.Worlds.WorldManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jalicz.CTF.Game.Data.*;
import org.jalicz.CTF.Game.Visual.*;

import java.util.Random;

public class EventListener implements Listener {

    private static final Plugin plugin = CTFPlugin.plugin;
    private static final Random random = new Random();
    private static final World
            lobby = WorldManager.LOBBY,
            game = WorldManager.GAME;


    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        e.setCancelled(BuildMode.isDisabled());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        e.setCancelled(BuildMode.isDisabled());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        
        if(PunishManager.isMuted(player.getName())) {
            e.setCancelled(true);
            Message.player(player, PunishManager.getMuteMessage(player.getName()));
            Audio.play(player, Sound.NOTE_BASS, 3f, 0.6f);
            return;
        }

        StatsManager.add(player.getName(), Statistics.WROTE_MESSAGES);
        ChatHistoryManager.saveMessage(player.getName(), message);

        String chatPrefix;
        switch (Teams.get(player)) {
            case SPECTATORS: chatPrefix = C.GRAY + "[SPEC] "; break;
            case RED: chatPrefix = C.RED; break;
            case BLUE: chatPrefix = C.AQUA; break;
            default: chatPrefix = C.WHITE;
        }
        chatPrefix = RankManager.getPrefix(player, true) + chatPrefix;
        e.setFormat(chatPrefix + player.getName() + C.GREEN + " » " + (RankManager.hasRank(player) ? C.WHITE:C.GRAY) + message);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosion(EntityExplodeEvent e) {
        e.setCancelled(true);
        if(e.getEntity() instanceof TNTPrimed) {
            TNTPrimed bomb = (TNTPrimed) e.getEntity();
            BombManager.explode(bomb);
            BombManager.bombs.remove(bomb);
            bomb.remove();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if(player.getWorld() == lobby) { if(player.getLocation().getBlockY() < -60) Teleporter.toSpawn(player); }
        else if(player.getWorld() == game) {
            GameScoreboard.update();

            Location loc = player.getLocation();
            final int x = loc.getBlockX(),
                      y = loc.getBlockY(),
                      z = loc.getBlockZ();

            if(game.getBlockAt(x, 98, z).getType() != Material.BARRIER) {
                Location velocityBlock = new Location(game, x*0.85 +0.5, y+4, z*0.7 +0.5);
                player.setVelocity(velocityBlock.toVector().subtract(loc.toVector()).multiply(0.15));
                Audio.play(player, Sound.NOTE_STICKS, 1, 1);

            } else if(Frozen.is(player) && Game.status == Status.RUNNING) {
                Location from = e.getFrom(), to = e.getTo();
                if(from.getX() != to.getX() || from.getZ() != to.getZ()) e.setTo(from);

            } else if(ZoneManager.get(player) == Zone.ENEMY_SAFE_ZONE) {
                if(Teams.get(player) == Team.RED) {
                    Location center = new Location(game, 32, loc.getY()+30, 0.5);
                    player.setVelocity(player.getLocation().subtract(center).toVector().multiply(0.3));
                    Audio.play(player, Sound.NOTE_STICKS, 1, 1);

                } else if(Teams.get(player) == Team.BLUE) {
                    Location center = new Location(game, -31, loc.getY()+30, 0.5);
                    player.setVelocity(player.getLocation().subtract(center).toVector().multiply(0.3));
                    Audio.play(player, Sound.NOTE_STICKS, 1, 1);
                }

            } else if(ZoneManager.get(player) == Zone.SAFE_ZONE && Game.status == Status.RUNNING) {
                if(!player.getInventory().contains(Material.BANNER) && (Teams.get(player) == Team.RED || Teams.get(player) == Team.BLUE)) {
                    if(Teams.get(player) == Team.RED) player.getInventory().addItem(ItemManager.RED_FLAG);
                    else if(Teams.get(player) == Team.BLUE) player.getInventory().addItem(ItemManager.BLUE_FLAG);

                    Audio.play(player, Sound.SUCCESSFUL_HIT, 1, 0.8f);
                    if(StatsManager.get(player.getName(), Statistics.GAMES_PLAYED) < 5) Message.player(player, Strings.GAME + C.BOLD + C.GREEN +
                            "You pickup a Flag! " + C.GOLD + C.ITALIC+ "Bring it to your team platform and score!");
                    Message.actionBar(player, C.GREEN + C.BOLD + "+ " + C.GOLD + "You picked up a " + C.AQUA + "Flag" + C.GOLD + "!");
                    NameTagFormat.update(player);

                    if(KitManager.get(player) == Kit.RUSHER) {
                        ItemStack powerup = ItemManager.getRandomPowerup();
                        EffectManager.add(player, PotionEffectType.SPEED, 200, 2);

                        if(player.getInventory().contains(powerup) || player.getItemOnCursor().equals(powerup))
                            Message.player(player, Strings.KIT + C.GREEN + "You received Speed II. for 10s!");
                        else {
                            player.getInventory().addItem(powerup);
                            Message.player(player, Strings.KIT + C.GREEN + "You received " + powerup.getItemMeta().getDisplayName() + C.GREEN +
                                    " and Speed II. for 10s!");
                        }
                    }
                }
            } else if(player.getInventory().contains(Material.BANNER) && ZoneManager.get(player) == Zone.TEAM_ZONE) {
                player.getInventory().remove(Material.BANNER);
                NameTagFormat.update(player);
                Game.score(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent e) {
        if(e.hasBlock() && e.getClickedBlock().getState() instanceof InventoryHolder) e.setCancelled(true);
        if(e.getItem() == null) return;

        Player player = e.getPlayer();
        Action a = e.getAction();
        ItemStack item = e.getItem();

        if(item.equals(ItemManager.LOBBY_CLOCK)) {
            if(a == Action.LEFT_CLICK_BLOCK || a == Action.PHYSICAL) return;
            LobbyTransporter.clickedWithClock(player);
            return;
        }
        if(player.getWorld() == lobby) {
            if(a == Action.LEFT_CLICK_BLOCK || a == Action.PHYSICAL) return;

            if(item.getType() == Material.BANNER) InventoryMenus.showTeamMenu(player);
            else if(item.getType() == Material.FEATHER) InventoryMenus.showKitMenu(player);

        } else if(player.getWorld() == game) {

            if(item.equals(ItemManager.BOOSTER)) {
                Audio.play(player.getLocation(), Sound.FIREWORK_LAUNCH, 3, 1.2f);
                player.setVelocity(player.getLocation().getDirection().multiply(1.5+Math.random()/6).add(new Vector(0, 0.5+Math.random()/8, 0)));
                Particles.spawn(Effect.FLAME, player.getLocation(), 60);

                if(KitManager.get(player) == Kit.POWERUP_MASTER && random.nextInt(8) == 0) Message.player(player, Strings.KIT + C.GREEN +
                        "15% chance successful -> " + item.getItemMeta().getDisplayName() + C.GREEN + " saved!");
                else player.getInventory().remove(item);

                new BukkitRunnable() {
                    int particles = 8;
                    @Override
                    public void run() {
                        Particles.spawn(Effect.FLAME, player.getLocation(), particles);
                        if(random.nextBoolean()) particles--;
                        if(particles < 1) cancel();
                    }
                }.runTaskTimer(plugin, 0, 1);

            } else if(item.equals(ItemManager.BOMB)) {
                Audio.play(player, Sound.FUSE, 1, 1.6f);
                BombManager.spawn(player);

                if(KitManager.get(player) == Kit.POWERUP_MASTER && random.nextInt(8) == 0) Message.player(player, Strings.KIT + C.GREEN +
                        "15% chance successful -> " + item.getItemMeta().getDisplayName() + C.GREEN + " saved!");
                else player.getInventory().remove(item);

            } else if(item.equals(ItemManager.PRISON)) {
                Audio.play(player, Sound.ANVIL_BREAK, 1, 1.4f);
                PrisonManager.spawn(player);

                if(KitManager.get(player) == Kit.POWERUP_MASTER && random.nextInt(8) == 0) Message.player(player, Strings.KIT + C.GREEN +
                        "15% chance successful -> " + item.getItemMeta().getDisplayName() + C.GREEN + " saved!");
                else player.getInventory().remove(item);

            } else if(item.equals(ItemManager.TRACKING_COMPASS)) {
                if(Teams.get(player) != Team.SPECTATORS) return;
                Audio.play(player, Sound.NOTE_PLING, 3f, 0.8f);
                InventoryMenus.showSpectatorPlayerMenu(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if(entity instanceof ArmorStand && entity.getName().contains("My stats")) InventoryMenus.showStatsMenu(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getCurrentItem() == null) return;

        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if(item.getType().equals(Material.BARRIER)) {
            player.closeInventory();
            Audio.play(player, Sound.NOTE_BASS, 1, 1.2f);
            return;
        }

        if(player.getWorld() == lobby) {
            e.setCancelled(true);

            if(e.getInventory().getTitle().equals(ItemManager.selectTeamBannerName)) {
                if(item.equals(ItemManager.JOIN_RED)) Teams.set(player, Team.RED);
                else if(item.equals(ItemManager.JOIN_BLUE)) Teams.set(player, Team.BLUE);
                else if(item.equals(ItemManager.LEAVE_TEAM)) Teams.set(player, Team.NONE);
                else if(item.getType() == Material.BARRIER) Audio.play(player, Sound.NOTE_BASS, 1, 1.2f);
                else return;
                player.closeInventory();

            } else if(e.getInventory().getTitle().equals(ItemManager.selectKitBannerName)) {
                player.closeInventory();

                switch (item.getType()) {
                    case IRON_CHESTPLATE:
                        if(KitManager.get(player) != Kit.DEFENDER) KitManager.set(player, Kit.DEFENDER);
                        else {
                            Audio.play(player, Sound.NOTE_PLING, 1, 0.8f);
                            Message.player(player, Strings.KIT + C.YELLOW + "You already have selected " + C.BOLD + C.GOLD + KitManager.getString(player) + " Kit" + C.ITALIC + C.YELLOW + "!");
                        }
                        player.getInventory().setItem(1, ItemManager.SELECTED_KIT);
                        return;

                    case BLAZE_POWDER:
                        if(KitManager.get(player) != Kit.FREEZE_IMMUNITY) KitManager.set(player, Kit.FREEZE_IMMUNITY);
                        else {
                            Audio.play(player, Sound.NOTE_PLING, 1, 0.8f);
                            Message.player(player, Strings.KIT + C.YELLOW + "You already have selected " + C.BOLD + C.GOLD + KitManager.getString(player) + " Kit" + C.ITALIC + C.YELLOW + "!");
                        }
                        player.getInventory().setItem(1, ItemManager.SELECTED_KIT);
                        return;

                    case DIAMOND_SWORD:
                        if(KitManager.get(player) != Kit.RUSHER) KitManager.set(player, Kit.RUSHER);
                        else {
                            Audio.play(player, Sound.NOTE_PLING, 1, 0.8f);
                            Message.player(player, Strings.KIT + C.YELLOW + "You already have selected " + C.BOLD + C.GOLD + KitManager.getString(player) + " Kit" + C.ITALIC + C.YELLOW + "!");
                        }
                        player.getInventory().setItem(1, ItemManager.SELECTED_KIT);
                        return;

                    case TNT:
                        if(KitManager.get(player) != Kit.POWERUP_MASTER) KitManager.set(player, Kit.POWERUP_MASTER);
                        else {
                            Audio.play(player, Sound.NOTE_PLING, 1, 0.8f);
                            Message.player(player, Strings.KIT + C.YELLOW + "You already have selected " + C.BOLD + C.GOLD + KitManager.getString(player) + " Kit" + C.ITALIC + C.YELLOW + "!");
                        }
                        player.getInventory().setItem(1, ItemManager.SELECTED_KIT);
                        return;

                    case INK_SACK:
                        Kit kit = KitManager.get(player);
                        if(kit == Kit.NONE) {
                            Message.player(player, Strings.KIT + C.RED + "You haven't got selected any kit!");
                            Audio.play(player, Sound.NOTE_BASS, 3f, 0.7f);
                            return;
                        }
                        DefaultKitManager.set(player.getName(), kit);
                        Message.player(player, Strings.KIT + C.AQUA + "Your default kit was changed to " + KitManager.getString(player) + C.RESET + C.AQUA + "!");
                        Audio.play(player, Sound.EXPLODE, 3f, 0.7f);
                        Audio.play(player, Sound.NOTE_BASS, 3f, 0.6f);
                }
            } else if(e.getInventory().getTitle().equals(C.PINK + "My statistics")) {
                Audio.play(player, (item.getType() == Material.AIR) ? Sound.CLICK : Sound.SUCCESSFUL_HIT, 3f, 0.5f+random.nextFloat());
            }
        } else if(player.getWorld() == game) {
            if(e.getInventory().getTitle().equals(C.AQUA + "Select Player")) {
                if(!item.hasItemMeta() || item.getItemMeta().getDisplayName() == null) {
                    Audio.play(player, Sound.CLICK, 3f, 1f);
                    return;
                }
                Player target = Players.get(item.getItemMeta().getDisplayName().substring(2));
                if(target == null) {
                    player.closeInventory();
                    Message.player(player, C.RED + "This player is already offline on this server! He probably left the game.");
                    Audio.play(player, Sound.NOTE_BASS, 3f, 1f);
                    return;
                }
                player.teleport(target.getLocation().add(0, 1, 0));
                Message.player(player, C.GREEN + "You were successfully teleported to " + C.AQUA + target.getName() + C.GREEN + "!");
                Audio.play(player, Sound.SUCCESSFUL_HIT, 3f, 1.3f);

                SpectatorCompassFocus.set(player, target);

                e.setCancelled(true);

            } else e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageEvent e) {
        if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPVP(EntityDamageByEntityEvent e) {
        e.setCancelled(true);

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player && Game.isRunning()) {

            Player target = (Player) e.getEntity(),
                   attacker = (Player) e.getDamager();

            if(attacker.getItemInHand() == null) return;
            ItemStack item = attacker.getItemInHand();

            if(item.equals(ItemManager.KNOCK_BALL) && Game.canKnock(attacker, target)) {
                e.setCancelled(false);
                e.setDamage(0);

                if(KitManager.get(attacker) == Kit.POWERUP_MASTER && random.nextInt(8) == 0) Message.player(attacker, Strings.KIT +
                        C.GREEN + "15% chance successful -> " + item.getItemMeta().getDisplayName() + C.GREEN + " saved!");
                else attacker.getInventory().remove(item);

                Particles.spawn(Effect.FLAME, target.getLocation().add(0, 1, 0), 80);
                Particles.spawn(Effect.CRIT, target.getLocation().add(0, 1, 0), 80);
                Particles.spawn(Effect.EXPLOSION_LARGE, target.getLocation().add(0, 1, 0), 1);
                Audio.play(target.getLocation().add(new Vector(0, 1, 0)) ,Sound.IRONGOLEM_HIT, 2.3f, 0.4f);
                Audio.play(target.getLocation().add(new Vector(0, 1, 0)) ,Sound.EXPLODE, 1.6f, 0.4f);
            }
            else if(item.equals(ItemManager.FREEZE_STICK)) Frozen.tryFreeze(attacker, target);
            else if(item.equals(ItemManager.UNFREEZE_STICK)) Frozen.tryUnfreeze(attacker, target);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
        if(Game.status != Status.RUNNING) {
            e.getItem().remove();
            return;
        }
        if(e.getItem().getCustomName() != null || e.getItem().hasMetadata("UNPICKABLE")) return;

        Player player = e.getPlayer();

        ItemStack item = e.getItem().getItemStack();
        if(Teams.get(player) == Team.SPECTATORS || Teams.get(player) == Team.NONE || player.getInventory().contains(item) || player.getItemOnCursor().equals(item)) return;

        e.setCancelled(false);

        Audio.play(player, Sound.SUCCESSFUL_HIT, 1f, 1.8f);
        Message.actionBar(player, C.GREEN + C.BOLD + "+ " + C.RESET + C.PINK + "You collected " + item.getItemMeta().getDisplayName() + C.PINK + "!");
        if(KitManager.get(player) == Kit.POWERUP_MASTER) {
            EffectManager.add(player, PotionEffectType.SPEED, 140, 2);
            EffectManager.add(player, PotionEffectType.JUMP, 140, 2);
            Audio.play(player, Sound.SUCCESSFUL_HIT, 1, 1.2f);

            Message.player(player, Strings.KIT + C.GREEN + "Speed II. and Jump-boost II. received for 7s!");
        }
        StatsManager.add(player.getName(), Statistics.COLLECTED_POWERUPS);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.updateInventory();
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHunger(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        
        if(PunishManager.isBanned(player.getName()) && !AdminManager.isAdmin(player.getName())) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, PunishManager.getBanMessage(player.getName()));
            return;
        }
        if(Bukkit.hasWhitelist() && !AdminManager.isAdmin(player.getName()) && !player.isWhitelisted()) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, C.translate(plugin.getConfig().getString("kick-messages.whitelist")));
            return;
        }
        if(Players.isFullServer() || (!Game.isRunning() && Players.isMax())) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, C.translate(plugin.getConfig().getString("kick-messages.full-server")) + C.AQUA +
                    "\nOn this server " + (Players.count() < 2 ? "is":"are") + " currently " + Players.count() + "/" + Players.MAXIMUM + " players!\n" + C.DARK_AQUA +
                    "Game is starting in " + Countdown.countdown + " seconds, after that you can to spectate the game.");
            return;
        }
        e.allow();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Message.xpBar(player, 0);
        player.setMaxHealth(8);
        player.getInventory().setHeldItemSlot(2);

        if(Game.isRunning()) {
            e.setJoinMessage("");
            player.setGameMode(GameMode.SURVIVAL);
            Spectators.add(player);
            GameScoreboard.update(player);

        } else {
            e.setJoinMessage(RankManager.getPrefix(player, true) + C.DARK_GRAY + player.getName() + " joined the Game! (" + C.GREEN + Players.count() +
                    C.DARK_GRAY + "/" + C.GREEN + Players.MAXIMUM + C.DARK_GRAY + ")");

            player.setGameMode(GameMode.ADVENTURE);
            Message.joinInfoMessage(player);
            player.setAllowFlight(false);
            Visibility.showForOthers(player);
            Teleporter.toSpawn(player);
            if(DefaultKitManager.hasDefaultKit(player.getName())) KitManager.set(player, DefaultKitManager.get(player.getName()));
            ItemManager.setLobbyItems(player);
            Countdown.runTask();
            GameScoreboard.update();
        }
        NameTagFormat.update();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        KitManager.remove(player);
        Frozen.remove(player);
        ItemManager.clearItems(player);
        Boards.remove(player);
        RankManager.remove(player);
        LobbyTransporter.cancelTask(player);
        SpectatorCompassFocus.remove(player);
        Message.consoleOutputReaders.remove(player);

        if(Game.isRunning()) {
            e.setQuitMessage("");

            Team team = Teams.get(player);
            if(Game.status != Status.ENDING && (team == Team.RED || team == Team.BLUE)) {

                if(Players.count(Team.RED) - (team == Team.RED ? 1:0) == 0) {
                    if(Game.status == Status.STARTING) Game.allPlayersLeftGameBeforeStart = true;
                    else {
                        Message.broadcast(Strings.GAME + C.AQUA + C.BOLD + "BLUE" + C.GREEN + " team wins! (" + C.ITALIC + C.PINK + "All " + C.RED + "RED " + C.PINK +
                                "players left the Game!" + C.BOLD + C.GREEN + ")");
                        Game.end(Team.BLUE);
                    }
                } else if(Players.count(Team.BLUE) - (team == Team.BLUE ? 1:0) == 0) {
                    if(Game.status == Status.STARTING) Game.allPlayersLeftGameBeforeStart = true;
                    else {
                        Message.broadcast(Strings.GAME + C.RED + C.BOLD + "RED" + C.GREEN + " team wins! (" + C.ITALIC + C.PINK + "All " + C.AQUA + "BLUE " + C.PINK +
                                "players left the Game!" + C.GREEN + C.BOLD + ")");
                        Game.end(Team.RED);
                    }
                }
            }
        } else {
            e.setQuitMessage(RankManager.getPrefix(player, true) + C.DARK_GRAY + player.getName() + " left the Game! (" + C.RED + (Players.count()-1) +
                    C.DARK_GRAY + "/" + C.RED + Players.MAXIMUM + C.DARK_GRAY + ")");

            if(Players.count()-1 < Players.MINIMUM) {
                Game.status = Status.UNABLE_TO_START;
                Countdown.stop();
            }
        }
        Teams.remove(player);
        GameScoreboard.updateAfterDelay(1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnload(ChunkUnloadEvent e) {
        e.setCancelled(true);
    }
}