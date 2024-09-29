package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MakeAChat extends JavaPlugin implements Listener {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final HashMap<Player, Player> lastMessaged = new HashMap<>();
    private final HashMap<Player, Sound> playerSounds = new HashMap<>();
    private HashMap<Player, Location> lastLocation = new HashMap<>();

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        // Регистрируем событие чата
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("goto").setExecutor(new TeleportCommand(this));
        this.getCommand("back").setExecutor(new BackCommand(this));
        this.getCommand("msg").setExecutor(new MessageCommand(this));
        this.getCommand("r").setExecutor(new ReplyCommand(this));
        this.getCommand("msgs").setExecutor(new MessageSoundCommand(this));
        this.getCommand("rename").setExecutor(new RenameCommand());
        this.getCommand("enchant").setExecutor(new EnchantCommand(this));
        getLogger().info("MakeAChat плагин активирован!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MakeAChat плагин деактивирован.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerSound(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerSound(player);
    }

    public void setLastLocation(Player player, Location location) {
        lastLocation.put(player, location);
    }

    public Location getLastLocation(Player player) {
        return lastLocation.get(player);
    }


    public void setPlayerSound(Player player, Sound sound) {
        playerSounds.put(player, sound);
        savePlayerSound(player);
    }

    public Sound getPlayerSound(Player player) {
        return playerSounds.getOrDefault(player, Sound.BLOCK_NOTE_BLOCK_BELL);
    }


    public void savePlayerSound(Player player) {
        String path = "players." + player.getUniqueId().toString() + ".sound";
        Sound sound = getPlayerSound(player);
        config.set(path, sound.toString());
        saveConfig();
    }

    public void loadPlayerSound(Player player) {
        String path = "players." + player.getUniqueId().toString() + ".sound";
        if (config.contains(path)) {
            String soundName = config.getString(path);
            Sound sound = Sound.valueOf(soundName);
            setPlayerSound(player, sound);
        }
    }

    public void setLastMessaged(Player sender, Player recipient) {
        lastMessaged.put(recipient, sender);
    }

    public Player getLastMessaged(Player player) {
        return lastMessaged.get(player);
    }

    public String getPlayerPrefix(Player player) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix != null) {
                return prefix;
            }
        }
        return "";
    }

    public String getPlayerSuffix(Player player) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String suffix = user.getCachedData().getMetaData().getSuffix();
            if (suffix != null) {
                return suffix;
            }
        }
        return "";
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getDisplayName();
        String message = event.getMessage();

        if (message.contains(":loc:")) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();

            String worldName = player.getWorld().getName();

            String color;
            switch (worldName) {
                case "world":
                    color = "<green>";
                    break;
                case "world_nether":
                    color = "<red>";
                    break;
                case "world_the_end":
                    color = "<light_purple>";
                    break;
                default:
                    color = "<white>";
            }

            String location = color + "<click:run_command:'/goto " + worldName + " " + x + " " + y + " " + z + "'>["
                    + x + "x/" + y + "y/" + z + "z, " + worldName + "]</click><reset>";

            message = message.replace(":loc:", location);
        }

        String prefix = getPlayerPrefix(player);
        String suffix = getPlayerSuffix(player);

        String formattedMessage = prefix + playerName + suffix + " > " + message;

        Component parsedMessage = miniMessage.deserialize(formattedMessage);

        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(parsedMessage);
        }

        event.setCancelled(true);
    }
}