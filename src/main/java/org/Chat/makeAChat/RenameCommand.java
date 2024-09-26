package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RenameCommand implements CommandExecutor {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Проверяем, есть ли предмет в руке
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "У вас в руке должен быть предмет.");
            return true;
        }

        ItemMeta meta = item.getItemMeta();

        // Обработка команды для изменения названия
        if (args.length > 1 && args[0].equalsIgnoreCase("name")) {
            // Собираем новое название
            StringBuilder newName = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                newName.append(args[i]).append(" ");
            }

            // Устанавливаем новое название с помощью MiniMessage
            Component parsedName = miniMessage.deserialize(newName.toString().trim());
            meta.displayName(parsedName);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "Название предмета изменено.");
            return true;
        }

        // Обработка команды для изменения лора
        if (args.length > 2 && args[0].equalsIgnoreCase("lore")) {
            // Проверяем, что второй аргумент — это число (номер строки)
            int lineIndex;
            try {
                lineIndex = Integer.parseInt(args[1]) - 1;  // Индекс начинается с 0
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Номер строки должен быть числом.");
                return true;
            }

            // Собираем текст для лора
            StringBuilder loreText = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                loreText.append(args[i]).append(" ");
            }

            // Получаем или создаём список лора
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            // Расширяем лор до нужного размера, если нужно
            while (lore.size() <= lineIndex) {
                lore.add(Component.text(""));  // Добавляем пустые строки
            }

            // Изменяем конкретную строку лора
            Component parsedLore = miniMessage.deserialize(loreText.toString().trim());
            lore.set(lineIndex, parsedLore);
            meta.lore(lore);
            item.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "Лор предмета изменён.");
            return true;
        }

        // Если команда не распознана или недостаточно аргументов
        player.sendMessage(ChatColor.RED + "Использование: /rename <name|lore> <аргументы>");
        return true;
    }
}
