package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final MakeAChat plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public EnchantCommand(MakeAChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /enchant <игрок> <зачарование> <уровень>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return true;
        }

        ItemStack itemInHand = target.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            sender.sendMessage(ChatColor.RED + "У игрока " + target.getName() + " в руке ничего нет!");
            return true;
        }

        Enchantment enchantment = Enchantment.getByName(args[1].toUpperCase());
        if (enchantment == null) {
            sender.sendMessage(ChatColor.RED + "Неверное зачарование.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Уровень должен быть числом.");
            return true;
        }

        itemInHand.addUnsafeEnchantment(enchantment, level);

        Component displayName = itemInHand.getItemMeta().hasDisplayName() ? itemInHand.getItemMeta().displayName() : Component.text(itemInHand.getType().toString());

        String prefix = plugin.getPlayerPrefix(target);
        String suffix = plugin.getPlayerSuffix(target);

        // Форматируем сообщение с использованием MiniMessage
        String enchantmentMessage = String.format("Зачарование <green>%s</green> с уровнем <gold>%d</gold> наложено на <aqua>%s</aqua> игроку %s%s%s.",
                enchantment.getKey().getKey(),
                level,
                miniMessage.serialize(displayName),
                prefix,
                target.getName(),
                suffix
        );

        // Отправляем сообщение отправителю только если это не тот же игрок
        sender.sendMessage(miniMessage.deserialize(enchantmentMessage));
        if (sender != target) {
            // Отправляем сообщение только целевому игроку если это не отправитель
            target.sendMessage(miniMessage.deserialize(enchantmentMessage));
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }

        if (args.length == 2) {
            List<String> enchantments = new ArrayList<>();
            for (Enchantment enchantment : Enchantment.values()) {
                enchantments.add(enchantment.getKey().getKey());
            }
            return enchantments;
        }

        return Arrays.asList();
    }
}
