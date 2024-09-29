package org.Chat.makeAChat;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final MakeAChat plugin;

    public BackCommand(MakeAChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location lastLoc = plugin.getLastLocation(player);
            if (lastLoc != null) {
                player.teleport(lastLoc);
                player.sendMessage("§aВы вернулись на своё предыдущее место.");
            } else {
                player.sendMessage("§cНет сохранённого местоположения для возврата.");
            }
            return true;
        }
        return false;
    }
}
