package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Sound;

public class ReplyCommand implements CommandExecutor {

    private final MiniMessage miniMessage = MiniMessage.miniMessage(); // Инициализация MiniMessage
    private final MakeAChat plugin;

    public ReplyCommand(MakeAChat plugin) {
        this.plugin = plugin;
    }

    // Получаем префикс игрока из LuckPerms
    private String getPlayerPrefix(Player player) {
        return plugin.getPlayerPrefix(player);
    }

    // Получаем суффикс игрока из LuckPerms
    private String getPlayerSuffix(Player player) {
        return plugin.getPlayerSuffix(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду.");
            return true;
        }

        Player playerSender = (Player) sender;

        // Получаем последнего отправителя
        Player target = plugin.getLastMessaged(playerSender);
        if (target == null) {
            playerSender.sendMessage("§cНет игрока, которому можно ответить.");
            return true;
        }

        if (args.length < 1) {
            playerSender.sendMessage("Использование: /r §e<сообщение>");
            return true;
        }

        // Собираем сообщение
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Получаем префиксы и суффиксы
        String senderPrefix = getPlayerPrefix(playerSender);
        String senderSuffix = getPlayerSuffix(playerSender);
        String targetPrefix = getPlayerPrefix(target);
        String targetSuffix = getPlayerSuffix(target);

        // Форматируем сообщение с использованием MiniMessage
        String formattedMessage = senderPrefix + playerSender.getName() + senderSuffix + " <yellow>→</yellow> "
                + targetPrefix + target.getName() + targetSuffix + ": <gray>" + message.toString().trim() + "</gray>";

        // Парсим сообщение в Component
        Component parsedMessage = miniMessage.deserialize(formattedMessage);

        // Отправляем сообщение
        target.sendMessage(parsedMessage);
        playerSender.sendMessage(parsedMessage);

        // Воспроизводим звук мяукания кошки для получателя
        target.playSound(target.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1.0F, 1.0F);

        // Обновляем последнего собеседника для команды /r
        plugin.setLastMessaged(playerSender, target);

        return true;
    }
}