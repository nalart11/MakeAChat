package org.Chat.makeAChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только игрокам.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Использование: /enchant <зачарование> <уровень>");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null) {
            player.sendMessage(ChatColor.RED + "Ваша рука пуста!");
            return true;
        }

        Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());
        if (enchantment == null) {
            player.sendMessage(ChatColor.RED + "Неверное зачарование.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Уровень должен быть числом.");
            return true;
        }

        itemInHand.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(ChatColor.GREEN + "Зачарование " + enchantment.getKey().getKey() + " наложено на "
                + itemInHand.getType() + " с уровнем " + level + ".");

        return true;
    }
}
