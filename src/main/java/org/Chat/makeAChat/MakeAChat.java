package org.Chat.makeAChat;

import org.Chat.makeAChat.TeleportCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class MakeAChat extends JavaPlugin implements Listener {

    private final MiniMessage miniMessage = MiniMessage.miniMessage(); // Инициализация MiniMessage
    private final HashMap<Player, Player> lastMessaged = new HashMap<>(); // Отслеживание последних отправителей
    private final HashMap<Player, Sound> playerSounds = new HashMap<>();

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        // Регистрируем событие чата
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("goto").setExecutor(new TeleportCommand(this));
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

    public void setPlayerSound(Player player, Sound sound) {
        playerSounds.put(player, sound);
        savePlayerSound(player);  // Сохраняем звук при его установке
    }

    public Sound getPlayerSound(Player player) {
        return playerSounds.getOrDefault(player, Sound.BLOCK_NOTE_BLOCK_BELL); // Return a default sound if none is set
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

    // Метод для обновления последнего отправителя сообщения
    public void setLastMessaged(Player sender, Player recipient) {
        lastMessaged.put(recipient, sender);
    }

    // Метод для получения последнего отправителя
    public Player getLastMessaged(Player player) {
        return lastMessaged.get(player);
    }

    // Получаем префикс игрока из LuckPerms
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

    // Получаем суффикс игрока из LuckPerms
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
        // Получаем игрока и его сообщение
        Player player = event.getPlayer();
        String playerName = player.getDisplayName();
        String message = event.getMessage();

        // Проверяем, есть ли в сообщении маркер ":loc:"
        if (message.contains(":loc:")) {
            // Получаем координаты игрока
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();

            // Получаем название мира
            String worldName = player.getWorld().getName();

            // Определяем цвет в зависимости от мира
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
                    color = "<white>"; // Цвет по умолчанию
            }

            // Форматируем строку с координатами и измерением
            String location = color + "<click:run_command:'/goto " + worldName + " " + x + " " + y + " " + z + "'>["
                    + x + "x/" + y + "y/" + z + "z, " + worldName + "]</click><reset>";

            // Заменяем ":loc:" на кликабельный текст с координатами игрока
            message = message.replace(":loc:", location);
        }

        // Получаем префикс и суффикс игрока
        String prefix = getPlayerPrefix(player);
        String suffix = getPlayerSuffix(player);

        // Форматируем сообщение, включая префикс, суффикс и текст игрока
        String formattedMessage = prefix + playerName + suffix + " > " + message;

        // Парсим MiniMessage в Component
        Component parsedMessage = miniMessage.deserialize(formattedMessage);

        // Отправляем сообщение всем игрокам
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(parsedMessage);
        }

        // Отменяем оригинальный формат чата
        event.setCancelled(true);
    }
}