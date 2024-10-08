package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
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

        String senderPrefix = plugin.getPlayerPrefix(playerSender);
        String senderSuffix = plugin.getPlayerSuffix(playerSender);
        String targetPrefix = plugin.getPlayerPrefix(target);
        String targetSuffix = plugin.getPlayerSuffix(target);

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        String formattedMessage = senderPrefix + playerSender.getName() + senderSuffix + " <yellow>→</yellow> "
                + targetPrefix + target.getName() + targetSuffix + ": <gray>" + message.toString().trim() + "</gray>";

        Component parsedMessage = miniMessage.deserialize(formattedMessage);

        target.sendMessage(parsedMessage);
        playerSender.sendMessage(parsedMessage);

        Sound selectedSound = plugin.getPlayerSound(target);

        target.playSound(target.getLocation(), selectedSound, 1.0F, 1.0F);

        plugin.setLastMessaged(playerSender, target);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialName)) {
                    suggestions.add(player.getName());
                }
            }
        }

        return suggestions;
    }
}