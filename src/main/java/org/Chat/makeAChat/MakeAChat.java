    package org.Chat.makeAChat;

    import net.kyori.adventure.text.Component;
    import net.kyori.adventure.text.minimessage.MiniMessage;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.player.AsyncPlayerChatEvent;
    import org.bukkit.plugin.java.JavaPlugin;
    import net.luckperms.api.LuckPermsProvider;
    import net.luckperms.api.model.user.User;

    public class MakeAChat extends JavaPlugin implements Listener {

        private final MiniMessage miniMessage = MiniMessage.miniMessage(); // Инициализация MiniMessage

        @Override
        public void onEnable() {
            // Регистрируем событие чата
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("MakeAChat плагин активирован!");
        }

        @Override
        public void onDisable() {
            getLogger().info("MakeAChat плагин деактивирован.");
        }

        // Получаем префикс игрока из LuckPerms
        private String getPlayerPrefix(Player player) {
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
        private String getPlayerSuffix(Player player) {
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
                        color = "<purple>";
                        break;
                    default:
                        color = "<white>"; // Цвет по умолчанию
                }

                // Форматируем строку с координатами и измерением
                String location = color + "[" + x + "/" + y + "/" + z + ", " + worldName + "]" + "<reset>";

                // Заменяем ":loc:" на координаты игрока
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
