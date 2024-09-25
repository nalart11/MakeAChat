package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Sound;

public class MessageCommand implements CommandExecutor {

    private final MiniMessage miniMessage = MiniMessage.miniMessage(); // Инициализация MiniMessage
    private final MakeAChat plugin;

    public MessageCommand(MakeAChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Использование: /msg §c<игрок> §e<сообщение>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return true;
        }

        Player playerSender = (Player) sender;

        // Получаем префиксы и суффиксы
        String senderPrefix = plugin.getPlayerPrefix(playerSender);
        String senderSuffix = plugin.getPlayerSuffix(playerSender);
        String targetPrefix = plugin.getPlayerPrefix(target);
        String targetSuffix = plugin.getPlayerSuffix(target);

        // Собираем сообщение
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

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
