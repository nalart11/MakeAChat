package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RenameCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "У вас в руке должен быть предмет.");
            return true;
        }

        ItemMeta meta = item.getItemMeta();

        if (args.length > 1 && args[0].equalsIgnoreCase("name")) {
            StringBuilder newName = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                newName.append(args[i]).append(" ");
            }

            Component parsedName = miniMessage.deserialize(newName.toString().trim());
            meta.displayName(parsedName);
            item.setItemMeta(meta);

            player.sendMessage(Component.text(ChatColor.GREEN + "Название предмета изменено на: ").append(item.displayName()));

            return true;
        }

        if (args.length > 2 && args[0].equalsIgnoreCase("lore")) {
            int lineIndex;
            try {
                lineIndex = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Номер строки должен быть числом.");
                return true;
            }

            StringBuilder loreText = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                loreText.append(args[i]).append(" ");
            }

            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            while (lore.size() <= lineIndex) {
                lore.add(Component.text(""));
            }

            Component parsedLore = miniMessage.deserialize(loreText.toString().trim());
            lore.set(lineIndex, parsedLore);
            meta.lore(lore);
            item.setItemMeta(meta);

            player.sendMessage(Component.text(ChatColor.GREEN + "Лор предмета изменён на: ").append(item.displayName()));

            return true;
        }

        player.sendMessage(ChatColor.RED + "Использование: /rename <name/lore> <аргументы>");
        return true;
    }

    // Реализация автодополнения
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("name", "lore");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("lore")) {
            List<String> loreLines = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                loreLines.add(String.valueOf(i));
            }
            return loreLines;
        }

        return Collections.emptyList();
    }
}
