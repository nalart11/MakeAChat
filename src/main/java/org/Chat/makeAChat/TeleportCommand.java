package org.Chat.makeAChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class TeleportCommand implements CommandExecutor, TabCompleter {
    private final MakeAChat plugin; // Ссылка на экземпляр плагина

    public TeleportCommand(MakeAChat plugin) {
        this.plugin = plugin; // Инициализируем ссылку на плагин
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Проверяем количество аргументов
            String worldName = args.length > 0 ? args[0] : player.getWorld().getName(); // Если мир не указан, берем текущий мир
            int x = 0, y = -60, z = 0; // Устанавливаем координаты по умолчанию

            // Если аргументов больше 1, пытаемся получить координаты
            if (args.length == 4) {
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§4Координаты должны быть числами.");
                    return true;
                }
            } else if (args.length != 1) {
                player.sendMessage("Использование: /goto §d<world> §c[x] §a[y] §3[z]");
                return true;
            }

            // Проверяем, существует ли мир
            World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                player.teleport(new Location(world, x, y, z));
                player.sendMessage("§aВы были телепортированы в мир " + worldName);
            } else {
                player.sendMessage("§4Мир не найден.");
                return true;
            }

            return true;
        }
        return false;
    }

    // Метод для автодополнения миров
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> worldNames = new ArrayList<>();
            for (World world : plugin.getServer().getWorlds()) {
                worldNames.add(world.getName()); // Добавляем имена всех доступных миров
            }
            return worldNames; // Возвращаем список миров для автодополнения
        }
        return null; // Если мы не находимся на первом аргументе, не возвращаем ничего
    }
}