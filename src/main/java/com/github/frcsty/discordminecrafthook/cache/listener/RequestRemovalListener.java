package com.github.frcsty.discordminecrafthook.cache.listener;

import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.github.frcsty.discordminecrafthook.util.Message;
import com.github.frcsty.discordminecrafthook.util.Replace;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class RequestRemovalListener implements RemovalListener<String, UUID> {

    @NotNull
    private final ConfigStorage configStorage;

    public RequestRemovalListener(@NotNull final ConfigStorage configStorage) {
        this.configStorage = configStorage;
    }

    @Override
    public void onRemoval(@NotNull final RemovalNotification<String, UUID> notification) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(notification.getValue());

        if (!offlinePlayer.isOnline()) {
            return;
        }

        final Player player = (Player) offlinePlayer;
        Message.send(player, Replace.replaceString(
                this.configStorage.getConfigString("messages.code-expired"),
                "{expired-code}", notification.getKey()
        ));
    }
}
