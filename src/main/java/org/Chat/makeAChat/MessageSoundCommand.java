package org.Chat.makeAChat;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSoundCommand implements CommandExecutor, TabCompleter {

    private final MakeAChat plugin;
    private final Map<String, Sound> soundMap = new HashMap<>();

    public MessageSoundCommand(MakeAChat plugin) {
        this.plugin = plugin;

        // Сопоставление сокращённых названий звуков с полными идентификаторами
        soundMap.put("cat.ambient", Sound.ENTITY_CAT_AMBIENT);
        soundMap.put("anvil.land", Sound.BLOCK_ANVIL_LAND);
        soundMap.put("noteblock.bell", Sound.BLOCK_NOTE_BLOCK_BELL);
        soundMap.put("noteblock.chime", Sound.BLOCK_NOTE_BLOCK_CHIME);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("Использование: /msgs <sound>");
            return true;
        }

        // Получаем введённый сокращённый звук
        String soundKey = args[0].toLowerCase();

        // Проверяем, существует ли введённый звук в нашем маппинге
        if (!soundMap.containsKey(soundKey)) {
            player.sendMessage("Недопустимый звук. Введите один из доступных звуков.");
            return true;
        }

        // Устанавливаем звук для игрока
        Sound selectedSound = soundMap.get(soundKey);
        plugin.setPlayerSound(player, selectedSound);
        player.sendMessage("Звук уведомлений изменён на: " + soundKey);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Возвращаем список доступных звуков, фильтруя по начальной строке
            List<String> completions = new ArrayList<>();
            String currentInput = args[0].toLowerCase();
            for (String key : soundMap.keySet()) {
                if (key.startsWith(currentInput)) {
                    completions.add(key);
                }
            }
            return completions;
        }
        return null;
    }
}
