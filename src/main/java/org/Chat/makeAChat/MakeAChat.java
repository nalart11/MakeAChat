package org.Chat.makeAChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MakeAChat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MakeAChat плагин активирован!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MakeAChat плагин деактивирован.");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Component parsedMessage = LegacyComponentSerializer.legacySection().deserialize(message);
        event.setMessage(parsedMessage.toString());
    }
}